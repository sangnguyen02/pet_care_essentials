package com.example.uiux.Activities.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.uiux.Activities.Admin.Branch.BranchStoreActivity;
import com.example.uiux.Activities.Admin.Branch.TestMapActivity;
//import com.example.uiux.Activities.Admin.Branch.UpdateBranchStoreActivity;
import com.example.uiux.Activities.Admin.Category.CategoryActivity;
import com.example.uiux.Activities.Admin.Category.UpdateCategoryActivity;
import com.example.uiux.Activities.Admin.Services.ServiceActivity;
import com.example.uiux.Activities.Admin.Services.UpdateServiceActivity;
import com.example.uiux.Activities.Admin.Supplies.UpdateSuppliesActivity;
import com.example.uiux.Activities.Admin.Supplies.SuppliesActivity;
import com.example.uiux.Activities.Admin.Supplies.SuppliesImportActivity;
import com.example.uiux.Activities.Admin.Type.TypeActivity;
import com.example.uiux.Activities.Admin.Type.UpdateTypeActivity;
import com.example.uiux.Activities.SplashActivity;
import com.example.uiux.Activities.User.Map.BranchMapActivity;
import com.example.uiux.Activities.User.Map.RouteBranchActivity;
import com.example.uiux.Activities.User.Profile.SettingsActivity;
import com.example.uiux.R;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.UUID;

public class MainActivityAdmin extends AppCompatActivity {
    FirebaseAuth mAuth;
    MaterialCardView category_btn,type_btn,supplies_btn,supplies_import,service_btn, sign_out_admin,branchStore_btn,mapbtn;
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
        mAuth = FirebaseAuth.getInstance();

        // Khởi tạo Database Reference cho các model
        suppliesRef = FirebaseDatabase.getInstance().getReference("Supplies");
        suppliesImageRef = FirebaseDatabase.getInstance().getReference("Supplies_Image");
        suppliesImportRef = FirebaseDatabase.getInstance().getReference("Supplies_Import");
        suppliesPriceRef = FirebaseDatabase.getInstance().getReference("Supplies_Price");
        suppliesReviewRef = FirebaseDatabase.getInstance().getReference("Supplies_Review");


        category_btn=findViewById(R.id.category);
        type_btn=findViewById(R.id.type);
        supplies_btn=findViewById(R.id.supplies);
        supplies_import=findViewById(R.id.supplies_import);
        service_btn=findViewById(R.id.service);
        branchStore_btn=findViewById(R.id.branchStore);
        sign_out_admin=findViewById(R.id.sign_out_admin);
        mapbtn=findViewById(R.id.map);


        category_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivityAdmin.this, CategoryActivity.class);
                startActivity(intent);
            }
        });
        type_btn.setOnClickListener(view -> {
            Intent intent=new Intent(MainActivityAdmin.this, TypeActivity.class);
            startActivity(intent);
        });



        category_btn.setOnClickListener(v -> {
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
        supplies_import.setOnClickListener(view -> {
            Intent intent=new Intent(MainActivityAdmin.this, SuppliesImportActivity.class);
            startActivity(intent);

        });
        service_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivityAdmin.this, UpdateServiceActivity.class);
                startActivity(intent);
            }
        });
        branchStore_btn.setOnClickListener(view -> {
            Intent intent=new Intent(MainActivityAdmin.this, RouteBranchActivity.class);
            startActivity(intent);

        });
        mapbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivityAdmin.this, TestMapActivity.class);
                startActivity(intent);
            }
        });

        sign_out_admin.setOnClickListener(view -> {
            signOutUser();
        });
    }

    private void signOutUser() {
        mAuth.signOut();  // Firebase Auth sign out
        Intent intent = new Intent(MainActivityAdmin.this, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

