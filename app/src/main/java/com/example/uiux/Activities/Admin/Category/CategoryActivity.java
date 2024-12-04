package com.example.uiux.Activities.Admin.Category;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.uiux.Model.Category;
import com.example.uiux.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;
import java.util.UUID;

public class CategoryActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private TextInputEditText edtCategoryName;
    private Spinner spinnerCategoryStatus;
    private ImageView img_addCategory, imgv_back;
    private MaterialButton btnAddCategory;
    private Uri imageUri;
    private ProgressDialog progressDialog;

    // Firebase
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference categoryDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
        setContentView(R.layout.activity_category);
        // Ánh xạ các view
        edtCategoryName = findViewById(R.id.edt_new_category);
        spinnerCategoryStatus = findViewById(R.id.spinnerCategoryStatus);
        imgv_back = findViewById(R.id.img_back_new_category);
        img_addCategory = findViewById(R.id.img_addCategory);
        btnAddCategory = findViewById(R.id.btnAddCategory);

        // Khởi tạo Firebase
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("Category_Image/");
        categoryDatabase = FirebaseDatabase.getInstance().getReference("Category");

        // Khởi tạo progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");

        imgv_back.setOnClickListener(view -> {
            finish();
        });

        // Spinner cho trạng thái Category
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.category_status_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoryStatus.setAdapter(adapter);

        // Khởi tạo ActivityResultLauncher
        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                imageUri = result.getData().getData();
                img_addCategory.setImageURI(imageUri);
            }
        });



        // Sự kiện chọn hình ảnh
        img_addCategory.setOnClickListener(v -> openImageChooser());

        // Sự kiện thêm Category
        btnAddCategory.setOnClickListener(v -> {
            if (imageUri != null && !Objects.requireNonNull(edtCategoryName.getText()).toString().isEmpty()) {
                uploadImageAndAddCategory();
            } else {
                Toast.makeText(CategoryActivity.this, "Please select an image and enter a category name", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Mở Intent để chọn hình ảnh từ thư viện
    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            img_addCategory.setImageURI(imageUri);
        }
    }

    // Hàm upload hình ảnh lên Firebase Storage và thêm thông tin Category
    private void uploadImageAndAddCategory() {
        progressDialog.show();

        // Tạo UUID cho hình ảnh
        String imageId = UUID.randomUUID().toString();
        StorageReference imageRef = storageReference.child(imageId);

        imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                // Khi tải lên thành công, lấy URL hình ảnh và lưu Category
                addCategoryToDatabase(uri.toString());
            });
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(CategoryActivity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    // Hàm lưu Category vào Firebase Realtime Database
    private void addCategoryToDatabase(String imageUrl) {
        String categoryId = categoryDatabase.push().getKey();  // Tạo ID danh mục
        String categoryName = Objects.requireNonNull(edtCategoryName.getText()).toString();
        int status = spinnerCategoryStatus.getSelectedItemPosition();  // Lấy trạng thái từ Spinner

        // Tạo đối tượng Category
        Category category = new Category(categoryId, categoryName, imageUrl, status);

        // Lưu Category vào Firebase
        categoryDatabase.child(categoryId).setValue(category).addOnCompleteListener(task -> {
            progressDialog.dismiss();
            if (task.isSuccessful()) {
                Toast.makeText(CategoryActivity.this, "Category added successfully", Toast.LENGTH_SHORT).show();
                // Reset giao diện sau khi thêm thành công
                edtCategoryName.setText("");
                img_addCategory.setImageResource(0);  // Xóa ảnh
            } else {
                Toast.makeText(CategoryActivity.this, "Failed to add category", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
