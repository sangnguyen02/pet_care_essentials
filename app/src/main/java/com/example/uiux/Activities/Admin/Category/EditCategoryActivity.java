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
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.uiux.Model.Account_Address;
import com.example.uiux.Model.Category;
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

import java.util.Objects;

public class EditCategoryActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private TextInputEditText edtCategoryName;
    private ImageView img_editCategory, imgv_back;;
    private MaterialButton saveBtn;
    private Spinner status;
    private String category_id;
    private DatabaseReference categoryRef;
    private Uri selectedImageUri;
    private static final int PICK_IMAGE_REQUEST = 100;
    private FirebaseStorage storage; // Dùng Firebase Storage để lưu trữ ảnh

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
        setContentView(R.layout.activity_edit_category);
        edtCategoryName = findViewById(R.id.edt_edit_category);
        imgv_back = findViewById(R.id.img_back_edit_category);
        img_editCategory = findViewById(R.id.img_editCategory);
        saveBtn = findViewById(R.id.btnSaveCategory);
        status = findViewById(R.id.spinnerCategoryStatus_edit);

        imgv_back.setOnClickListener(view -> {
            finish();
        });

        String[] statusArray = getResources().getStringArray(R.array.category_status_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statusArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Gán adapter cho Spinner
        status.setAdapter(adapter);

        category_id = getIntent().getStringExtra("category_id");
        categoryRef = FirebaseDatabase.getInstance().getReference("Category").child(category_id);
        storage = FirebaseStorage.getInstance(); // Khởi tạo Firebase Storage

        loadCategoryData();

        // Khởi tạo ActivityResultLauncher
        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                selectedImageUri = result.getData().getData();
                img_editCategory.setImageURI(selectedImageUri);
            }
        });

        // Chọn ảnh
        img_editCategory.setOnClickListener(view -> chooseImage());

        // Lưu danh mục
        saveBtn.setOnClickListener(view -> saveCategoryData());
    }

    // Phương thức tải dữ liệu hiện tại từ Firebase
    private void loadCategoryData() {
        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Category category = snapshot.getValue(Category.class);
                    edtCategoryName.setText(category.getName());
                    status.setSelection(category.getStatus());

                    if (category.getImageUrl() != null && !category.getImageUrl().isEmpty()) {
                        Glide.with(EditCategoryActivity.this).load(category.getImageUrl()).into(img_editCategory);
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
        pickImageLauncher.launch(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            img_editCategory.setImageURI(selectedImageUri);
        }
    }

    // Phương thức lưu dữ liệu sau khi chỉnh sửa
    private void saveCategoryData() {
        String updatedName = Objects.requireNonNull(edtCategoryName.getText()).toString().trim();
        int updatedStatus = status.getSelectedItemPosition();

        // Cập nhật dữ liệu cơ bản của danh mục (trừ ảnh)
        categoryRef.child("name").setValue(updatedName);
        categoryRef.child("status").setValue(updatedStatus);

        // Nếu người dùng chọn ảnh mới
        if (selectedImageUri != null) {
            uploadImageToStorage();
            finish();
        } else {
            Toast.makeText(EditCategoryActivity.this, "Category updated successfully", Toast.LENGTH_SHORT).show();
            finish();
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
