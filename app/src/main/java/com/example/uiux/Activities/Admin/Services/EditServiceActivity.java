package com.example.uiux.Activities.Admin.Services;

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

public class EditServiceActivity extends AppCompatActivity {
    private ImageView img1, img2, img3, img4, img_back_edit_service;
    private TextInputEditText serviceName, serviceSellPrice,  serviceTime, serviceDescription;
    private Spinner serviceSize, serviceStatus, serviceCate, serviceType;
    private MaterialButton serviceSave;
    private static final int PICK_IMAGE_REQUEST = 100;
    private String service_id;
    private FirebaseStorage storage;
    private DatabaseReference serviceRef;
    private Uri[] imageUris = new Uri[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_service);
        serviceName = findViewById(R.id.edt_name);
        serviceSellPrice = findViewById(R.id.edt_sell_price);
        serviceTime = findViewById(R.id.edt_time);
        serviceSave = findViewById(R.id.btnSubmit);
        serviceDescription = findViewById(R.id.edt_description);
        img_back_edit_service = findViewById(R.id.img_back_edit_service);
        img1 = findViewById(R.id.img1);
        img2 = findViewById(R.id.img2);
        img3 = findViewById(R.id.img3);
        img4 = findViewById(R.id.img4);
        serviceSize = findViewById(R.id.spinner_size);
        serviceStatus = findViewById(R.id.spinner_status);
        serviceCate = findViewById(R.id.spinner_category);
        serviceType = findViewById(R.id.spinner_type);

        // Load spinners
        FetchSpinnerCategory();
        FectchSpinnerSize();
        FectchSpinnerStatus();
        FetchSpinnerType();
        img_back_edit_service.setOnClickListener(view -> {finish();});
        img1.setOnClickListener(view -> openImageChooser(0));
        img2.setOnClickListener(view -> openImageChooser(1));
        img3.setOnClickListener(view -> openImageChooser(2));
        img4.setOnClickListener(view -> openImageChooser(3));

        service_id = getIntent().getStringExtra("service_id");
        serviceRef = FirebaseDatabase.getInstance().getReference("Service").child(service_id);
        storage = FirebaseStorage.getInstance();
        loadServiceData();

        serviceSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveServiceData();
            }
        });

    }
    private void saveServiceData() {
        String updatedName = serviceName.getText().toString().trim();
        Double updateSellPrice= Double.valueOf(serviceSellPrice.getText().toString().trim());
        Integer updateTime = Integer.valueOf(serviceTime.getText().toString().trim());
        String updateDescription =serviceDescription.getText().toString().trim();

        // Cập nhật dữ liệu cơ bản của danh mục (trừ ảnh)
        serviceRef.child("name").setValue(updatedName);
        serviceRef.child("status").setValue(serviceStatus.getSelectedItemPosition());
        serviceRef.child("category").setValue(serviceCate.getSelectedItem().toString());
        serviceRef.child("description").setValue(updateDescription);
        // suppliesRef.child("imageUrls").setValue(updateDescription);
        serviceRef.child("time_estimate").setValue(updateTime);
        serviceRef.child("sell_price").setValue(updateSellPrice);
        serviceRef.child("size").setValue( serviceSize.getSelectedItem().toString());
        serviceRef.child("type").setValue(serviceType.getSelectedItem().toString());

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
                String imageName = "service_" + service_id + "_" + i + ".jpg";
                StorageReference storageRef = storage.getReference().child("service/" + imageName);

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
                                    serviceRef.child("imageUrls").setValue(newImageUrls)
                                            .addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(EditServiceActivity.this, "Service updated successfully", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(EditServiceActivity.this, "Failed to update image URLs", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            });
                        })
                        .addOnFailureListener(e -> Toast.makeText(EditServiceActivity.this, "Failed to upload image " + (finalI + 1), Toast.LENGTH_SHORT).show());
            }
        }
    }
    private int countSelectedImages() {
        int count = 0;
        for (Uri imageUri : imageUris) {
            if (imageUri != null) {
                count++;
            }
        }
        return count;
    }
    private void loadServiceData( ) {
        serviceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Service service = snapshot.getValue(Service.class);

                    serviceName.setText(service.getName());
                    serviceSellPrice.setText(String.valueOf(service.getSell_price()));
                    serviceTime.setText(String.valueOf(service.getTime_estimate()));
                    serviceDescription.setText(service.getDescription());
//                 setSpinnerValue(suppSize, supplies.getSize());
//                 setSpinnerValue(suppStatus, String.valueOf(supplies.getStatus()));
//                 setSpinnerValue(suppCate, supplies.getCategory());
//                 setSpinnerValue(suppType, supplies.getType());
                    if (service.getImageUrls()!=null)
                    {
                        if(service.getImageUrls().get(0)!=null)
                        {
                            Glide.with(EditServiceActivity.this).load(service.getImageUrls().get(0)).into(img1);
                        }
                        if(service.getImageUrls().get(1)!=null)
                        {
                            Glide.with(EditServiceActivity.this).load(service.getImageUrls().get(1)).into(img2);
                        }
                        if(service.getImageUrls().get(2)!=null)
                        {
                            Glide.with(EditServiceActivity.this).load(service.getImageUrls().get(2)).into(img3);
                        }
                        if(service.getImageUrls().get(3)!=null)
                        {
                            Glide.with(EditServiceActivity.this).load(service.getImageUrls().get(3)).into(img4);
                        }
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
    private void FectchSpinnerSize() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(EditServiceActivity.this,
                R.array.size_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        serviceSize.setAdapter(adapter);
    }

    private void FectchSpinnerStatus() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(EditServiceActivity.this,
                R.array.suplies_status, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        serviceStatus.setAdapter(adapter);
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

                ArrayAdapter<String> adapter = new ArrayAdapter<>(EditServiceActivity.this, android.R.layout.simple_spinner_item, typeList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                serviceType.setAdapter(adapter);
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

                ArrayAdapter<String> adapter = new ArrayAdapter<>(EditServiceActivity.this, android.R.layout.simple_spinner_item, cateList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                serviceCate.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}