package com.example.uiux.Activities.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.uiux.Activities.Admin.Category.UpdateCategoryActivity;
import com.example.uiux.Activities.Admin.Supplies.UpdateSuppliesActivity;
import com.example.uiux.Activities.Admin.Supplies.SuppliesActivity;
import com.example.uiux.Activities.Admin.Type.TypeActivity;
import com.example.uiux.Activities.Admin.Type.UpdateTypeActivity;
import com.example.uiux.R;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.UUID;

public class MainActivityAdmin extends AppCompatActivity {

    MaterialCardView category_btn, type_btn, supplies_btn;
    DatabaseReference suppliesRef;
    DatabaseReference suppliesImageRef;
    DatabaseReference suppliesImportRef;
    DatabaseReference suppliesPriceRef;
    DatabaseReference suppliesReviewRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.transparent));
        setContentView(R.layout.activity_main_admin);

        // Khởi tạo Database Reference cho các model
        suppliesRef = FirebaseDatabase.getInstance().getReference("Supplies");
        suppliesImageRef = FirebaseDatabase.getInstance().getReference("Supplies_Image");
        suppliesImportRef = FirebaseDatabase.getInstance().getReference("Supplies_Import");
        suppliesPriceRef = FirebaseDatabase.getInstance().getReference("Supplies_Price");
        suppliesReviewRef = FirebaseDatabase.getInstance().getReference("Supplies_Review");

        category_btn = findViewById(R.id.category);
        type_btn = findViewById(R.id.type);
        supplies_btn = findViewById(R.id.supplies);


        category_btn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivityAdmin.this, UpdateCategoryActivity.class);
            startActivity(intent);
        });

        supplies_btn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivityAdmin.this, UpdateSuppliesActivity.class);
            startActivity(intent);
        });

        type_btn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivityAdmin.this, UpdateTypeActivity.class);
            startActivity(intent);
        });
    }
}
