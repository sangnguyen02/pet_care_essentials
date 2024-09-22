package com.example.uiux.Activities.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.uiux.Activities.Admin.Category.CategoryActivity;
import com.example.uiux.Model.Supplies;
import com.example.uiux.Model.Supplies_Image;
import com.example.uiux.Model.Supplies_Import;
import com.example.uiux.Model.Supplies_Price;
import com.example.uiux.Model.Supplies_Review;
import com.example.uiux.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.UUID;

public class MainActivityAdmin extends AppCompatActivity {

    MaterialButton postSupply_btn,category_btn;
    DatabaseReference suppliesRef;
    DatabaseReference suppliesImageRef;
    DatabaseReference suppliesImportRef;
    DatabaseReference suppliesPriceRef;
    DatabaseReference suppliesReviewRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_admin);

        // Khởi tạo Database Reference cho các model
        suppliesRef = FirebaseDatabase.getInstance().getReference("Supplies");
        suppliesImageRef = FirebaseDatabase.getInstance().getReference("Supplies_Image");
        suppliesImportRef = FirebaseDatabase.getInstance().getReference("Supplies_Import");
        suppliesPriceRef = FirebaseDatabase.getInstance().getReference("Supplies_Price");
        suppliesReviewRef = FirebaseDatabase.getInstance().getReference("Supplies_Review");

        postSupply_btn = findViewById(R.id.post_supply);
        category_btn=findViewById(R.id.category);
        postSupply_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Tạo dữ liệu mẫu cho Supplies
                String suppliesId = UUID.randomUUID().toString();
                Supplies sampleSupply = new Supplies(
                        suppliesId,
                        "Sample Supply",
                        50.0,
                        30.0,
                        "Medium",
                        "This is a sample supply.",
                        1,
                        100,
                        "category123",
                        "typeABC"
                );

                // Tạo dữ liệu mẫu cho Supplies_Image
                String suppliesImageId = UUID.randomUUID().toString();
                Supplies_Image sampleImage = new Supplies_Image(
                        suppliesImageId,
                        suppliesId,
                        "https://example.com/sample-image.jpg"
                );

                // Tạo dữ liệu mẫu cho Supplies_Import
                String suppliesImportId = UUID.randomUUID().toString();
                Supplies_Import sampleImport = new Supplies_Import(
                        suppliesImportId,
                        suppliesId,
                        100,
                        100,
                        25.0,
                        new Date()
                );

                // Tạo dữ liệu mẫu cho Supplies_Price
                String suppliesPriceId = UUID.randomUUID().toString();
                Supplies_Price samplePrice = new Supplies_Price(
                        suppliesPriceId,
                        suppliesId,
                        50.0,
                        new Date()
                );

                // Tạo dữ liệu mẫu cho Supplies_Review
                String suppliesReviewId = UUID.randomUUID().toString();
                Supplies_Review sampleReview = new Supplies_Review(
                        suppliesReviewId,
                        "account123",
                        suppliesId,
                        5, // rating
                        "Great product!",
                        new Date(),
                        true,
                        true
                );

                // Lưu tất cả các model vào Firebase
                suppliesRef.child(suppliesId).setValue(sampleSupply)
                        .addOnSuccessListener(aVoid -> {
                            // Lưu các thông tin liên quan sau khi lưu Supplies thành công
                            suppliesImageRef.child(suppliesImageId).setValue(sampleImage);
                            suppliesImportRef.child(suppliesImportId).setValue(sampleImport);
                            suppliesPriceRef.child(suppliesPriceId).setValue(samplePrice);
                            suppliesReviewRef.child(suppliesReviewId).setValue(sampleReview);

                            Toast.makeText(MainActivityAdmin.this, "All data posted successfully", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(MainActivityAdmin.this, "Failed to post supply: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });
        category_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivityAdmin.this, CategoryActivity.class);
                startActivity(intent);
            }
        });
    }
}