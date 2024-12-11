package com.example.uiux.Activities.User.Pet;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.uiux.Model.Pet;
import com.example.uiux.Model.Type;
import com.example.uiux.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class PetInfoActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView img1, img_back_add_pet_profile;
    private TextInputEditText petName,petAge, petWeight, petColor,petBreed;
    private Spinner petGender, petType;
    private MaterialButton petSubmit;

    private ProgressDialog progressDialog;
    private Uri[] imageUris = new Uri[1]; // Array to hold image URIs
    // Firebase
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference petDatabase;
    String accountId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
        setContentView(R.layout.activity_pet_info);


        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        accountId = preferences.getString("accountID", null);

        petName = findViewById(R.id.edt_name_add);
        petAge = findViewById(R.id.edt_age_add);
        petWeight = findViewById(R.id.edt_weight_add);
        petSubmit = findViewById(R.id.btnSubmit);
        petColor = findViewById(R.id.edt_color_add);
        petBreed = findViewById(R.id.edt_breed_add);

        img_back_add_pet_profile = findViewById(R.id.img_back_add_pet_profile);
        img1 = findViewById(R.id.img1);
        petGender = findViewById(R.id.spinner_gender_add);
        petType = findViewById(R.id.spinner_type_add);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("Pet_Image/");
        petDatabase = FirebaseDatabase.getInstance().getReference("Pet").child(accountId);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        FetchSpinnerGender();
        FetchSpinnerType();
        img_back_add_pet_profile.setOnClickListener(view -> {finish();});
        img1.setOnClickListener(view -> openImageChooser());

        petSubmit.setOnClickListener(view -> {
            if (imageUris != null && !Objects.requireNonNull(petName.getText()).toString().isEmpty() && !Objects.requireNonNull(petAge.getText()).toString().isEmpty()
                    && !Objects.requireNonNull(petWeight.getText()).toString().isEmpty() && !Objects.requireNonNull(petBreed.getText()).toString().isEmpty() && !Objects.requireNonNull(petColor.getText()).toString().isEmpty()) {
                uploadImageAndAddPet();
            } else {
                Toast.makeText(PetInfoActivity.this, "Please select correct", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void uploadImageAndAddPet() {
        progressDialog.show();
        List<String> imageUrls = new ArrayList<>();

        for (Uri imageUri : imageUris) {
            if (imageUri != null) {
                String imageId = UUID.randomUUID().toString();
                StorageReference imageRef = storageReference.child(imageId);
                imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        imageUrls.add(uri.toString()); // Collect image URLs
                        // Check if all images are uploaded
                        if (imageUrls.size() == countNonNullImageUris()) {
                            addPetToDatabase(imageUrls); // Call method to save supplies data
                        }
                    });
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(PetInfoActivity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }
    }
    private int countNonNullImageUris() {
        int count = 0;
        for (Uri uri : imageUris) {
            if (uri != null) count++;
        }
        return count;
    }
    private void addPetToDatabase(List<String> imageUrls) {
        String petId = petDatabase.push().getKey(); // Generate unique ID
        Pet pet= new Pet();
        pet.setPet_id(petId);
        pet.setPet_name(Objects.requireNonNull(petName.getText()).toString());
        pet.setColor( Objects.requireNonNull(petColor.getText()).toString());
        pet.setAge(Integer.valueOf(String.valueOf(petAge.getText().toString())));
        pet.setWeight(Integer.valueOf(String.valueOf(petWeight.getText().toString())));
        pet.setPet_breed(petBreed.getText().toString());
        pet.setGender(petGender.getSelectedItem().toString());
        pet.setPet_type( petType.getSelectedItem().toString());
        pet.setImageUrls(imageUrls);

        petDatabase.child(petId).setValue(pet).addOnCompleteListener(task -> {
            progressDialog.dismiss();
            if (task.isSuccessful()) {
                Toast.makeText(PetInfoActivity.this, "Pet added successfully!", Toast.LENGTH_SHORT).show();
                clearInputFields();
                finish();
            } else {
                Toast.makeText(PetInfoActivity.this, "Failed to add Pet: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void clearInputFields() {
        petName.setText("");
        petColor.setText("");
        petBreed.setText("");
        petWeight.setText("");
        petAge.setText("");
        for (int i = 0; i < imageUris.length; i++) {
            imageUris[i] = null;
            if (i == 0) {
                img1.setImageResource(R.drawable.logo_main);
            }
        }
    }
    private void FetchSpinnerType() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Type");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> typeList = new ArrayList<>(); // Danh sách chứa các "type" từ Firebase
                final Map<String, String> typeMap = new HashMap<>(); // Lưu type_id và type
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Type type = snapshot.getValue(Type.class);
                    if (type != null) {
                        typeList.add(type.getType()); // Thêm "type" vào danh sách
                        typeMap.put(type.getType(), type.getType_id()); // Map "type" với "type_id"
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(PetInfoActivity.this, android.R.layout.simple_spinner_item, typeList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                petType.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void FetchSpinnerGender() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(PetInfoActivity.this,
                R.array.genders, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        petGender.setAdapter(adapter);
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            int imageIndex = requestCode - PICK_IMAGE_REQUEST; // Adjust index based on requestCode
            if (imageIndex >= 0 && imageIndex < imageUris.length) { // Ensure index is within bounds
                imageUris[imageIndex] = selectedImageUri; // Store selected URI in the array
                if (imageIndex == 0) {
                    img1.setImageURI(selectedImageUri);
                }
            }
        }
    }

}