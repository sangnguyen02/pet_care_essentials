package com.example.uiux.Activities.Admin.Services;

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
import com.example.uiux.Model.Category;
import com.example.uiux.Model.Service;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ServiceActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView img1, img2, img3, img4, img_back_add_service;
    private TextInputEditText service_name, service_SellPrice,  service_time, service_Description;
    private Spinner service_Size, service_Status, service_Cate, service_Type;
    private MaterialButton suppSubmit;

    private ProgressDialog progressDialog;
    private Uri[] imageUris = new Uri[4]; // Array to hold image URIs
    // Firebase
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference serviceDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_service);
        service_name = findViewById(R.id.edt_name);
        service_SellPrice = findViewById(R.id.edt_sell_price);
        service_time = findViewById(R.id.edt_time);
        suppSubmit = findViewById(R.id.btnSubmit);
        service_Description = findViewById(R.id.edt_description);
        img_back_add_service = findViewById(R.id.img_back_add_service);
        img1 = findViewById(R.id.img1);
        img2 = findViewById(R.id.img2);
        img3 = findViewById(R.id.img3);
        img4 = findViewById(R.id.img4);
        service_Size = findViewById(R.id.spinner_size);
        service_Status = findViewById(R.id.spinner_status);
        service_Cate = findViewById(R.id.spinner_category);
        service_Type = findViewById(R.id.spinner_type);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("Service_Image/");
        serviceDatabase = FirebaseDatabase.getInstance().getReference("Service");
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        FetchSpinnerCategory();
        FectchSpinnerSize();
        FectchSpinnerStatus();
        FetchSpinnerType();
        img_back_add_service.setOnClickListener(view -> {finish();});
        img1.setOnClickListener(view -> openImageChooser(0));
        img2.setOnClickListener(view -> openImageChooser(1));
        img3.setOnClickListener(view -> openImageChooser(2));
        img4.setOnClickListener(view -> openImageChooser(3));

        suppSubmit.setOnClickListener(view -> {
            if (imageUris != null && !Objects.requireNonNull(service_name.getText()).toString().isEmpty()&& !Objects.requireNonNull(service_SellPrice.getText()).toString().isEmpty()&& !Objects.requireNonNull(service_time.getText()).toString().isEmpty()
                    && !Objects.requireNonNull(service_Description.getText()).toString().isEmpty()) {
                uploadImageAndAddService();
            } else {
                Toast.makeText(ServiceActivity.this, "Please select correct", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void uploadImageAndAddService() {
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
                            addServiceToDatabase(imageUrls); // Call method to save supplies data
                        }
                    });
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(ServiceActivity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
    private void addServiceToDatabase(List<String> imageUrls) {
        String service_Id = serviceDatabase.push().getKey(); // Generate unique ID

        Service service= new Service();
        service.setService_id(service_Id);
        service.setName( Objects.requireNonNull(service_name.getText()).toString());
        service.setSell_price( Double.valueOf(Objects.requireNonNull(service_SellPrice.getText()).toString()));
        service.setTime_estimate(Integer.valueOf(Objects.requireNonNull(service_time.getText()).toString()));
        service.setDescription(Objects.requireNonNull(service_Description.getText()).toString());
        service.setSize( service_Size.getSelectedItem().toString());
        service.setStatus( service_Status.getSelectedItemPosition());
        service.setCategory(service_Cate.getSelectedItem().toString());
        service.setType( service_Type.getSelectedItem().toString());
        service.setImageUrls(imageUrls);


        serviceDatabase.child(service_Id).setValue(service).addOnCompleteListener(task -> {
            progressDialog.dismiss();
            if (task.isSuccessful()) {
                Toast.makeText(ServiceActivity.this, "Service added successfully!", Toast.LENGTH_SHORT).show();
                clearInputFields(); // Optional: clear the input fields after successful upload
            } else {
                Toast.makeText(ServiceActivity.this, "Failed to add Service: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void clearInputFields() {
        service_name.setText("");
        service_SellPrice.setText("");
        service_time.setText("");
        service_Description.setText("");
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

                ArrayAdapter<String> adapter = new ArrayAdapter<>(ServiceActivity.this, android.R.layout.simple_spinner_item, typeList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                service_Type.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void FetchSpinnerCategory() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Category");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> cateList = new ArrayList<>(); // Danh sách chứa các "type" từ Firebase
                final Map<String, String> cateMap = new HashMap<>(); // Lưu type_id và type
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Category category = snapshot.getValue(Category.class);
                    if (category != null && category.getStatus() == 0) {
                        cateList.add(category.getName());
                        cateMap.put(category.getName(), category.getCategory_id()); // Map "type" với "type_id"
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(ServiceActivity.this, android.R.layout.simple_spinner_item, cateList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                service_Cate.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void FectchSpinnerSize() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ServiceActivity.this,
                R.array.size_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        service_Size.setAdapter(adapter);
    }

    private void FectchSpinnerStatus() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ServiceActivity.this,
                R.array.service_status, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        service_Status.setAdapter(adapter);
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