package com.example.uiux.Activities.User.Pet;

import android.app.ProgressDialog;
import android.content.Intent;
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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uiux.Activities.Admin.Supplies.SuppliesActivity;
import com.example.uiux.Model.Pet;
import com.example.uiux.Model.Supplies;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class PetInfoActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView img1, img2, img3, img4, img_back_add_supply;
    private TextInputEditText petName,petAge, petWeight, petColor,petBreed;
    private Spinner petGender, petType;
    private MaterialButton petSubmit;

    private ProgressDialog progressDialog;
    private Uri[] imageUris = new Uri[4]; // Array to hold image URIs
    // Firebase
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference petDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pet_info);
        petName = findViewById(R.id.edt_name);
        petAge = findViewById(R.id.edt_age);
        petWeight = findViewById(R.id.edt_weight);
        petSubmit = findViewById(R.id.btnSubmit);
        petColor = findViewById(R.id.edt_color);
        petBreed = findViewById(R.id.edt_breed);

        img_back_add_supply = findViewById(R.id.img_back_add_supply);
        img1 = findViewById(R.id.img1);
        img2 = findViewById(R.id.img2);
        img3 = findViewById(R.id.img3);
        img4 = findViewById(R.id.img4);
        petGender = findViewById(R.id.spinner_gender);
        petType = findViewById(R.id.spinner_type);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("Pet_Image/");
        petDatabase = FirebaseDatabase.getInstance().getReference("Pet");
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        FetchSpinnerGender();
        FetchSpinnerType();
        img_back_add_supply.setOnClickListener(view -> {finish();});
        img1.setOnClickListener(view -> openImageChooser(0));
        img2.setOnClickListener(view -> openImageChooser(1));
        img3.setOnClickListener(view -> openImageChooser(2));
        img4.setOnClickListener(view -> openImageChooser(3));

        petSubmit.setOnClickListener(view -> {
            if (imageUris != null && !Objects.requireNonNull(petName.getText()).toString().isEmpty()&& !Objects.requireNonNull(petAge.getText()).toString().isEmpty()
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
        pet.setPet_name( Objects.requireNonNull(petName.getText()).toString());
        pet.setColor( Objects.requireNonNull(petColor.getText()).toString());
        pet.setAge(Integer.valueOf(String.valueOf(petAge.getText().toString())));
        pet.setWeight(Integer.valueOf(String.valueOf(petAge.getText().toString())));
        pet.setPet_breed( petBreed.getText().toString());
        pet.setGender(petGender.getSelectedItem().toString());
        pet.setPet_type( petType.getSelectedItem().toString());
        pet.setImageUrls(imageUrls);

        petDatabase.child(petId).setValue(petId).addOnCompleteListener(task -> {
            progressDialog.dismiss();
            if (task.isSuccessful()) {
                Toast.makeText(PetInfoActivity.this, "Pet added successfully!", Toast.LENGTH_SHORT).show();
                // Sau khi thêm sản phẩm thành công, thêm giá của sản phẩm vào bảng Supplies_Price

                clearInputFields(); // Optional: clear the input fields after successful upload
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
            imageUris[i] = null; // Clear image URIs
            switch (i) {
                case 0:
                    img1.setImageResource(R.drawable.logo); // Set to a placeholder image
                    break;
                case 1:
                    img2.setImageResource(R.drawable.logo);
                    break;
                case 2:
                    img3.setImageResource(R.drawable.logo);
                    break;
                case 3:
                    img4.setImageResource(R.drawable.logo);
                    break;
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

    private void openImageChooser(int index) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST + index);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            int imageIndex = requestCode - PICK_IMAGE_REQUEST; // Adjust index based on requestCode
            if (imageIndex >= 0 && imageIndex < imageUris.length) { // Ensure index is within bounds
                imageUris[imageIndex] = selectedImageUri; // Store selected URI in the array
                switch (imageIndex) {
                    case 0:
                        img1.setImageURI(selectedImageUri);
                        break;
                    case 1:
                        img2.setImageURI(selectedImageUri);
                        break;
                    case 2:
                        img3.setImageURI(selectedImageUri);
                        break;
                    case 3:
                        img4.setImageURI(selectedImageUri);
                        break;
                }
            }
        }
    }

}