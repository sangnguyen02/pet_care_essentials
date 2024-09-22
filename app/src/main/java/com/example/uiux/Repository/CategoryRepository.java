package com.example.uiux.Repository;
import com.example.uiux.Activities.User.Profile.UserUpdateProfile;
import com.example.uiux.Model.Category;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
public class CategoryRepository {

    private DatabaseReference databaseReference;

    public CategoryRepository() {
        // Khởi tạo Firebase Realtime Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Category");
    }

    // CREATE: Thêm mới một Category vào Realtime Database
    public void addCategory(Category category) {
        String categoryId = databaseReference.push().getKey();  // Tạo ID ngẫu nhiên cho Category
        category.setCategory_id(categoryId);
        databaseReference.child(categoryId).setValue(category)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Firebase", "Category added successfully: " + category.getName());
                    } else {
                        Log.e("Firebase", "Error adding category: " + task.getException().getMessage());
                    }
                });
    }

    // READ: Lấy toàn bộ danh sách Category từ Realtime Database
    public void getAllCategories(final CategoryDataStatus dataStatus) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Category> categories = new ArrayList<>();
                for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                    Category category = categorySnapshot.getValue(Category.class);
                    categories.add(category);
                }
                dataStatus.DataIsLoaded(categories);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Error fetching categories: " + databaseError.getMessage());
            }
        });
    }

    // UPDATE: Cập nhật một Category đã có trong Realtime Database
    public void updateCategory(String categoryId, Category category) {
        databaseReference.child(categoryId).setValue(category)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Firebase", "Category updated successfully: " + category.getName());
                    } else {
                        Log.e("Firebase", "Error updating category: " + task.getException().getMessage());
                    }
                });
    }

    // DELETE: Xóa một Category khỏi Realtime Database
    public void deleteCategory(String categoryId) {
        databaseReference.child(categoryId).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Firebase", "Category deleted successfully");
                    } else {
                        Log.e("Firebase", "Error deleting category: " + task.getException().getMessage());
                    }
                });
    }

    // Interface để trả về dữ liệu từ Firebase khi lấy danh sách Categories
    public interface CategoryDataStatus {
        void DataIsLoaded(List<Category> categories);
    }
    private void uploadImageToFirebase(Uri imageUri, DatabaseReference userRef) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("Account_Image/" + System.currentTimeMillis() + ".jpg");

        imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();

                // Update the image URL in Firebase
                userRef.child("image").setValue(imageUrl).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Update Image Category successfully" ,"Image Category updated successfully with image");
                    } else {
                        Log.d("Update Image Category failed" ,"Image Category updated failed with image");
                    }
                });
            }).addOnFailureListener(e -> {
                Log.d("Update Image Category" ,"Image Category updated  with image");
            });
        }).addOnFailureListener(e -> {
            Log.d("Update Image Category" ,"Failed");
        });
    }
}