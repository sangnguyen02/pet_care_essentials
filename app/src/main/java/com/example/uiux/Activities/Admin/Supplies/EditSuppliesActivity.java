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
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.uiux.Activities.Admin.Category.EditCategoryActivity;
import com.example.uiux.Model.Category;
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
import java.util.UUID;

public class EditSuppliesActivity extends AppCompatActivity {

    private ImageView img1, img2, img3, img4, img_back_edit_supply;
    private TextInputEditText suppName, suppQuantity, suppDescription, suppSellPrice;
    private Spinner suppStatus, suppCate, suppType;
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
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.transparent));
        setContentView(R.layout.activity_edit_supplies);

        suppName = findViewById(R.id.edt_name);
        suppQuantity = findViewById(R.id.edt_quantity);
        suppSellPrice = findViewById(R.id.edt_sell_price);
        suppSave = findViewById(R.id.btnSave);
        suppDescription = findViewById(R.id.edt_description);
        img_back_edit_supply = findViewById(R.id.img_back_edit_supply);
        img1 = findViewById(R.id.img1);
        img2 = findViewById(R.id.img2);
        img3 = findViewById(R.id.img3);
        img4 = findViewById(R.id.img4);
        suppStatus = findViewById(R.id.spinner_status);
        suppCate = findViewById(R.id.spinner_category);
        suppType = findViewById(R.id.spinner_type);

        // Load spinners
        FetchSpinnerCategory();
        FectchSpinnerStatus();
        FetchSpinnerType();
        img_back_edit_supply.setOnClickListener(view -> {finish();});
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
        Double updateSellPrice = Double.valueOf(suppSellPrice.getText().toString().trim());
        Integer updateQuantity = Integer.valueOf(suppQuantity.getText().toString().trim());
        String updateDescription =suppDescription.getText().toString().trim();

//        supplies.setStatus( suppStatus.getSelectedItemPosition());
//        supplies.setCategory(suppCate.getSelectedItem().toString());
//        supplies.setType( suppType.getSelectedItem().toString());
//        supplies.setImageUrls(imageUrls);
        // Cập nhật dữ liệu cơ bản của danh mục (trừ ảnh)
        suppliesRef.child("name").setValue(updatedName);
        suppliesRef.child("status").setValue(suppStatus.getSelectedItemPosition());
        suppliesRef.child("category").setValue(suppCate.getSelectedItem().toString());
        suppliesRef.child("description").setValue(updateDescription);
       // suppliesRef.child("imageUrls").setValue(updateDescription);
        suppliesRef.child("quantity").setValue(updateQuantity);
        suppliesRef.child("type ").setValue(suppType.getSelectedItem().toString());

    // Kiểm tra nếu giá bán thay đổi
    suppliesRef.child("sell_price").addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            Double oldSellPrice = snapshot.getValue(Double.class);
            if (oldSellPrice != null && !oldSellPrice.equals(updateSellPrice)) {
                // Nếu giá bán thay đổi, cập nhật giá mới và tạo bản ghi Supplies_Price
                suppliesRef.child("sell_price").setValue(updateSellPrice);
                addSuppliesPriceToDatabase(supplies_id, updateSellPrice);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(EditSuppliesActivity.this, "Failed to update price: " + error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    });

    uploadImageToStorage();
}
    private void addSuppliesPriceToDatabase(String supplyId, double sellPrice) {
        DatabaseReference suppliesPriceDatabase = FirebaseDatabase.getInstance().getReference("Supplies_Price");
        String suppliesPriceId = suppliesPriceDatabase.push().getKey(); // Tạo ID duy nhất cho bảng Supplies_Price

        // Tạo đối tượng Supplies_Price
        Supplies_Price suppliesPrice = new Supplies_Price();
        suppliesPrice.setSupplies_price_id(suppliesPriceId);
        suppliesPrice.setSupplies_id(supplyId);
        suppliesPrice.setSupply(Objects.requireNonNull(suppName.getText()).toString());


        // Định dạng ngày hiện tại theo hh:mm dd/MM/yyyy
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
        String formattedDate = dateFormat.format(new Date()); // Lấy ngày hiện tại và format
        suppliesPrice.setEffective_date(formattedDate); // Lưu ngày định dạng

        // Lưu vào Firebase Database
        suppliesPriceDatabase.child(suppliesPriceId).setValue(suppliesPrice).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(EditSuppliesActivity.this, "Supplies price added successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(EditSuppliesActivity.this, "Failed to add supplies price: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
                 DecimalFormat df = new DecimalFormat("0");
                 suppName.setText(supplies.getName());
                 suppQuantity.setText(String.valueOf(supplies.getQuantity()));
                 suppSellPrice.setText(String.valueOf(supplies.getSell_price()));
                 suppDescription.setText(supplies.getDescription());
                 int status = supplies.getStatus();
                 suppStatus.setSelection(status);
                 // Khi load dữ liệu từ Firebase
                 String categoryFromDb = supplies.getCategory(); // Giả sử lấy được tên danh mục từ Firebase
                 int index=getPositionByName(suppCate,categoryFromDb);
                 suppCate.setSelection(index);
                 String typeFromDb = supplies.getType();
                 int index2=getPositionByName(suppType,typeFromDb);
                 suppType.setSelection(index2);

                 if (supplies.getImageUrls() != null) {
                     List<String> imageUrls = supplies.getImageUrls();
                     for (int i = 0; i < imageUrls.size(); i++) {
                         if (imageUrls.get(i) != null) {
                             switch (i) {
                                 case 0:
                                     Glide.with(EditSuppliesActivity.this).load(imageUrls.get(i)).into(img1);
                                     break;
                                 case 1:
                                     Glide.with(EditSuppliesActivity.this).load(imageUrls.get(i)).into(img2);
                                     break;
                                 case 2:
                                     Glide.with(EditSuppliesActivity.this).load(imageUrls.get(i)).into(img3);
                                     break;
                                 case 3:
                                     Glide.with(EditSuppliesActivity.this).load(imageUrls.get(i)).into(img4);
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
