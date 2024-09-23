package com.example.uiux.Activities.Admin.Category;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.uiux.Model.Account_Address;
import com.example.uiux.Model.Category;
import com.example.uiux.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class EditCategoryActivity extends AppCompatActivity {
    private EditText categoryName;
    private ImageView img_category;
    private Button choose_Img, saveBtn;
    private Spinner status;
    private String category_id;
    private DatabaseReference categoryRef;
    private Uri selectedImageUri; // Biến lưu ảnh đã chọn
    private static final int PICK_IMAGE_REQUEST = 100;
    private FirebaseStorage storage; // Dùng Firebase Storage để lưu trữ ảnh

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_category);

        categoryName = findViewById(R.id.edtCategoryName);
        img_category = findViewById(R.id.img_cate);
        choose_Img = findViewById(R.id.btn_choose_image);
        saveBtn = findViewById(R.id.btnSaveCategory);
        status = findViewById(R.id.spinnerCategoryStatus);
        String[] statusArray = getResources().getStringArray(R.array.category_status_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statusArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Gán adapter cho Spinner
        status.setAdapter(adapter);

        category_id = getIntent().getStringExtra("category_id");
        categoryRef = FirebaseDatabase.getInstance().getReference("Category").child(category_id);
        storage = FirebaseStorage.getInstance(); // Khởi tạo Firebase Storage

        loadCategoryData();

        // Chọn ảnh
        choose_Img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        // Lưu danh mục
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveCategoryData();
            }
        });
    }

    // Phương thức tải dữ liệu hiện tại từ Firebase
    private void loadCategoryData() {
        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Category category = snapshot.getValue(Category.class);
                    categoryName.setText(category.getName());

                    if (category.getImageUrl() != null && !category.getImageUrl().isEmpty()) {
                        Glide.with(EditCategoryActivity.this).load(category.getImageUrl()).into(img_category);
                    }
                    int statusValue = category.getStatus();
                    status.setSelection(statusValue);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditCategoryActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Phương thức chọn ảnh từ thư viện
    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            Glide.with(this).load(selectedImageUri).into(img_category); // Hiển thị ảnh đã chọn
        }
    }

    // Phương thức lưu dữ liệu sau khi chỉnh sửa
    private void saveCategoryData() {
        String updatedName = categoryName.getText().toString().trim();
        int updatedStatus = status.getSelectedItemPosition();

        // Cập nhật dữ liệu cơ bản của danh mục (trừ ảnh)
        categoryRef.child("name").setValue(updatedName);
        categoryRef.child("status").setValue(updatedStatus);

        // Nếu người dùng chọn ảnh mới
        if (selectedImageUri != null) {
            uploadImageToStorage();
        } else {
            Toast.makeText(EditCategoryActivity.this, "Category updated successfully", Toast.LENGTH_SHORT).show();
        }
    }

    // Phương thức tải ảnh lên Firebase Storage
    private void uploadImageToStorage() {
        StorageReference storageRef = storage.getReference().child("categories/" + category_id + ".jpg");
        storageRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    // Cập nhật URL ảnh mới vào Firebase Realtime Database
                    categoryRef.child("imageUrl").setValue(imageUrl)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(EditCategoryActivity.this, "Category updated successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(EditCategoryActivity.this, "Failed to update image URL", Toast.LENGTH_SHORT).show();
                                }
                            });
                }))
                .addOnFailureListener(e -> Toast.makeText(EditCategoryActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show());
    }
}
