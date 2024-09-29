package com.example.uiux.Activities.Admin.Supplies;

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

import com.bumptech.glide.Glide;
import com.example.uiux.Activities.Admin.Category.EditCategoryActivity;
import com.example.uiux.Model.Category;
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
import java.util.UUID;

public class EditSuppliesActivity extends AppCompatActivity {

    private ImageView img1, img2, img3, img4;
    private TextInputEditText suppName, suppSellPrice, suppCostPrice, suppQuantity, suppDescription;
    private Spinner suppSize, suppStatus, suppCate, suppType;
    private MaterialButton suppSave;
    private static final int PICK_IMAGE_REQUEST = 100;
    private String supplies_id;
    private FirebaseStorage storage;
    private DatabaseReference suppliesRef;
    private Uri[] imageUris = new Uri[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_supplies);

        suppName = findViewById(R.id.edt_name);
        suppSellPrice = findViewById(R.id.edt_sell_price);
        suppCostPrice = findViewById(R.id.edt_cost_price);
        suppQuantity = findViewById(R.id.edt_quantity);
        suppSave = findViewById(R.id.btnSave);
        suppDescription = findViewById(R.id.edt_description);
        img1 = findViewById(R.id.img1);
        img2 = findViewById(R.id.img2);
        img3 = findViewById(R.id.img3);
        img4 = findViewById(R.id.img4);
        suppSize = findViewById(R.id.spinner_size);
        suppStatus = findViewById(R.id.spinner_status);
        suppCate = findViewById(R.id.spinner_category);
        suppType = findViewById(R.id.spinner_type);

        // Load spinners
        FetchSpinnerCategory();
        FectchSpinnerSize();
        FectchSpinnerStatus();
        FetchSpinnerType();

        img1.setOnClickListener(view -> openImageChooser(0));
        img2.setOnClickListener(view -> openImageChooser(1));
        img3.setOnClickListener(view -> openImageChooser(2));
        img4.setOnClickListener(view -> openImageChooser(3));

        supplies_id = getIntent().getStringExtra("supplies_id");
        Log.e("supplies ID",supplies_id);
        suppliesRef = FirebaseDatabase.getInstance().getReference("Supplies").child(supplies_id);
        storage = FirebaseStorage.getInstance();
        loadSuppliesData();

       suppSave.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               saveSuppliesData();
           }
       });
    }

    private void saveSuppliesData() {
        String updatedName = suppName.getText().toString().trim();
        Double updateSellPrice= Double.valueOf(suppSellPrice.getText().toString().trim());
        Double updateCostPrice= Double.valueOf(suppCostPrice.getText().toString().trim());
        Integer updateQuantity = Integer.valueOf(suppQuantity.getText().toString().trim());
        String updateDescription =suppDescription.getText().toString().trim();

//        supplies.setSize( suppSize.getSelectedItem().toString());
//        supplies.setStatus( suppStatus.getSelectedItemPosition());
//        supplies.setCategory(suppCate.getSelectedItem().toString());
//        supplies.setType( suppType.getSelectedItem().toString());
//        supplies.setImageUrls(imageUrls);
        // Cập nhật dữ liệu cơ bản của danh mục (trừ ảnh)
        suppliesRef.child("name").setValue(updatedName);
        suppliesRef.child("status").setValue(suppStatus.getSelectedItemPosition());
        suppliesRef.child("category").setValue(suppCate.getSelectedItem().toString());
        suppliesRef.child("cost_price").setValue(updateCostPrice);
        suppliesRef.child("description").setValue(updateDescription);
       // suppliesRef.child("imageUrls").setValue(updateDescription);
        suppliesRef.child("quantity").setValue(updateQuantity);
        suppliesRef.child("sell_price").setValue(updateSellPrice);
        suppliesRef.child("size ").setValue( suppSize.getSelectedItem().toString());
        suppliesRef.child("type ").setValue(suppType.getSelectedItem().toString());

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
                String imageName = "supplies_" + supplies_id + "_" + i + ".jpg";
                StorageReference storageRef = storage.getReference().child("supplies/" + imageName);

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
                                    suppliesRef.child("imageUrls").setValue(newImageUrls)
                                            .addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(EditSuppliesActivity.this, "Supplies updated successfully", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(EditSuppliesActivity.this, "Failed to update image URLs", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            });
                        })
                        .addOnFailureListener(e -> Toast.makeText(EditSuppliesActivity.this, "Failed to upload image " + (finalI + 1), Toast.LENGTH_SHORT).show());
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


    private void loadSuppliesData( ) {
     suppliesRef.addListenerForSingleValueEvent(new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot snapshot) {
             if (snapshot.exists()) {
                 Supplies supplies = snapshot.getValue(Supplies.class);

                 suppName.setText(supplies.getName());
                 suppSellPrice.setText(String.valueOf(supplies.getSell_price()));
                 suppCostPrice.setText(String.valueOf(supplies.getCost_price()));
                 suppQuantity.setText(String.valueOf(supplies.getQuantity()));
                 suppDescription.setText(supplies.getDescription());
//                 setSpinnerValue(suppSize, supplies.getSize());
//                 setSpinnerValue(suppStatus, String.valueOf(supplies.getStatus()));
//                 setSpinnerValue(suppCate, supplies.getCategory());
//                 setSpinnerValue(suppType, supplies.getType());
                 if (supplies.getImageUrls()!=null)
                 {
                     if(supplies.getImageUrls().get(0)!=null)
                     {
                         Glide.with(EditSuppliesActivity.this).load(supplies.getImageUrls().get(0)).into(img1);
                     }
                     if(supplies.getImageUrls().get(1)!=null)
                     {
                         Glide.with(EditSuppliesActivity.this).load(supplies.getImageUrls().get(1)).into(img2);
                     }
                     if(supplies.getImageUrls().get(2)!=null)
                     {
                         Glide.with(EditSuppliesActivity.this).load(supplies.getImageUrls().get(2)).into(img3);
                     }
                     if(supplies.getImageUrls().get(3)!=null)
                     {
                         Glide.with(EditSuppliesActivity.this).load(supplies.getImageUrls().get(3)).into(img4);
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
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(EditSuppliesActivity.this,
                R.array.size_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        suppSize.setAdapter(adapter);
    }

    private void FectchSpinnerStatus() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(EditSuppliesActivity.this,
                R.array.suplies_status, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        suppStatus.setAdapter(adapter);
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

                ArrayAdapter<String> adapter = new ArrayAdapter<>(EditSuppliesActivity.this, android.R.layout.simple_spinner_item, typeList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                suppType.setAdapter(adapter);
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

                ArrayAdapter<String> adapter = new ArrayAdapter<>(EditSuppliesActivity.this, android.R.layout.simple_spinner_item, cateList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                suppCate.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
