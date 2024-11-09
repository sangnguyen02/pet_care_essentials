package com.example.uiux.Activities.User.Pet;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import com.bumptech.glide.Glide;
import com.example.uiux.Activities.Admin.Supplies.EditSuppliesActivity;
import com.example.uiux.Model.Pet;
import com.example.uiux.Model.Supplies;
import com.example.uiux.Model.Supplies_Price;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class EditPetInfoActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView img1, img2, img3, img4, img_back_add_supply;
    private TextInputEditText petName,petAge, petWeight, petColor,petBreed;
    private Spinner petGender, petType;
    private MaterialButton petSave;

    private ProgressDialog progressDialog;
    private Uri[] imageUris = new Uri[4]; // Array to hold image URIs
    // Firebase
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference petDatabase;
    private String pet_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_pet_info);
        petName = findViewById(R.id.edt_name);
        petAge = findViewById(R.id.edt_age);
        petWeight = findViewById(R.id.edt_weight);
        petSave = findViewById(R.id.btnSubmit);
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
        pet_id = getIntent().getStringExtra("pet_id");
        petDatabase = FirebaseDatabase.getInstance().getReference("Pet").child(pet_id);
        storage = FirebaseStorage.getInstance();
        loadPetData();
        petSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePetData();
            }
        });

    }
    private void savePetData() {
        String updatedName = petName.getText().toString().trim();
        String updatedColor = petColor.getText().toString().trim();
        Integer updateWeight = Integer.valueOf(petWeight.getText().toString().trim());
        Integer updateAge = Integer.valueOf(petAge.getText().toString().trim());

        // Cập nhật dữ liệu cơ bản của danh mục (trừ ảnh)
        petDatabase.child("pet_name").setValue(updatedName);
        petDatabase.child("pet_color").setValue(updatedColor);
        petDatabase.child("pet_weight").setValue(updateWeight);
        petDatabase.child("pet_age").setValue(updateAge);



        uploadImageToStorage();
    }
    private void uploadImageToStorage() {
        // Tạo danh sách các URL ảnh mới
        List<String> newImageUrls = new ArrayList<>();

        // Lặp qua các ảnh đã chọn
        for (int i = 0; i < imageUris.length; i++) {
            Uri selectedImageUri = imageUris[i];
            if (selectedImageUri != null) {
                // Tạo một StorageReference cho mỗi ảnh
                String imageName = "pet_" + pet_id + "_" + i + ".jpg";
                StorageReference storageRef = storage.getReference().child("pet/" + imageName);

                // Upload từng ảnh và lấy URL
                int finalI = i; // Biến cuối cùng để sử dụng trong inner class
                storageRef.putFile(selectedImageUri)
                        .addOnSuccessListener(taskSnapshot -> {
                            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                String imageUrl = uri.toString();
                                newImageUrls.add(imageUrl); // Thêm URL vào danh sách

                                // Kiểm tra nếu tất cả các hình ảnh đã được tải lên
                                if (newImageUrls.size() == countSelectedImages()) {
                                    // Lưu danh sách URL vào Firebase
                                    petDatabase.child("imageUrls").setValue(newImageUrls)
                                            .addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(EditPetInfoActivity.this, "Supplies updated successfully", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(EditPetInfoActivity.this, "Failed to update image URLs", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            });
                        })
                        .addOnFailureListener(e -> Toast.makeText(EditPetInfoActivity.this, "Failed to upload image " + (finalI + 1), Toast.LENGTH_SHORT).show());
            }
        }
    }

    // Đếm số ảnh đã chọn
    private int countSelectedImages() {
        int count = 0;
        for (Uri imageUri : imageUris) {
            if (imageUri != null) {
                count++;
            }
        }
        return count;
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

                ArrayAdapter<String> adapter = new ArrayAdapter<>(EditPetInfoActivity.this, android.R.layout.simple_spinner_item, typeList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                petType.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void FetchSpinnerGender() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(EditPetInfoActivity.this,
                R.array.genders, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        petGender.setAdapter(adapter);
    }

    private void loadPetData( ) {
        petDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Pet pet = snapshot.getValue(Pet.class);
                    petName.setText(pet.getPet_name());
                    petAge.setText(String.valueOf(pet.getAge()));
                    petWeight.setText(String.valueOf(pet.getWeight()));
                    petBreed.setText(pet.getPet_breed());
                    int status = Integer.parseInt(pet.getGender());
                    petGender.setSelection(status);
                    // Khi load dữ liệu từ Firebase
                    String typeFromDb = pet.getPet_type();
                    int index2=getPositionByName(petType,typeFromDb);
                    petType.setSelection(index2);

                    if (pet.getImageUrls() != null) {
                        List<String> imageUrls = pet.getImageUrls();
                        for (int i = 0; i < imageUrls.size(); i++) {
                            if (imageUrls.get(i) != null) {
                                switch (i) {
                                    case 0:
                                        Glide.with(EditPetInfoActivity.this).load(imageUrls.get(i)).into(img1);
                                        break;
                                    case 1:
                                        Glide.with(EditPetInfoActivity.this).load(imageUrls.get(i)).into(img2);
                                        break;
                                    case 2:
                                        Glide.with(EditPetInfoActivity.this).load(imageUrls.get(i)).into(img3);
                                        break;
                                    case 3:
                                        Glide.with(EditPetInfoActivity.this).load(imageUrls.get(i)).into(img4);
                                        break;
                                }
                            }
                        }
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setSpinnerValue(Spinner spinner, String value) {
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        int position = adapter.getPosition(value);
        spinner.setSelection(position);
    }
    private int getPositionByName(Spinner spinner,String name) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        if (adapter != null) {
            for (int i = 0; i < adapter.getCount(); i++) {
                if (adapter.getItem(i).equals(name)) {
                    return i; // Return the position if a match is found
                }
            }
        }
        return -1; // Return -1 if the item is not found
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