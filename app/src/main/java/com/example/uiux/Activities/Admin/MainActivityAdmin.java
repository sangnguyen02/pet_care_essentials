package com.example.uiux.Activities.Admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

//import com.example.uiux.Activities.Admin.Branch.UpdateBranchStoreActivity;
import com.example.uiux.Activities.Admin.Branch.BranchStoreActivity;
import com.example.uiux.Activities.Admin.Branch.UpdateBranchStoreActivity;
import com.example.uiux.Activities.Admin.Category.CategoryActivity;
import com.example.uiux.Activities.Admin.Category.UpdateCategoryActivity;
import com.example.uiux.Activities.Admin.Discount.UpdateDiscountActivity;
import com.example.uiux.Activities.Admin.Services.UpdateServiceActivity;
import com.example.uiux.Activities.Admin.Supplies.UpdateSuppliesActivity;
import com.example.uiux.Activities.Admin.Supplies.SuppliesImportActivity;
import com.example.uiux.Activities.Admin.Type.TypeActivity;
import com.example.uiux.Activities.Admin.Type.UpdateTypeActivity;
import com.example.uiux.Activities.Admin.Voucher.UpdateVoucherActivity;
import com.example.uiux.Activities.SplashActivity;
import com.example.uiux.Activities.User.Order.PaypalActivity;
import com.example.uiux.Activities.User.Order.UpdateOrderActivity;
import com.example.uiux.R;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivityAdmin extends AppCompatActivity {
    FirebaseAuth mAuth;
    MaterialCardView category_btn, type_btn, supplies_btn, supplies_import_btn, service_btn, service_booking_btn,
            branch_store_btn, order_approve_btn, order_statistic_btn, discount_btn, vouncher_btn;

    ImageButton sign_out_admin;
    DatabaseReference suppliesRef;
    DatabaseReference suppliesImageRef;
    DatabaseReference suppliesImportRef;
    DatabaseReference suppliesPriceRef;
    DatabaseReference suppliesReviewRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
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
        supplies_btn=findViewById(R.id.supply);
        supplies_import_btn=findViewById(R.id.import_supplies);

        service_btn=findViewById(R.id.service);
        service_booking_btn = findViewById(R.id.service_booking);
        branch_store_btn=findViewById(R.id.branch);

        order_approve_btn = findViewById(R.id.approve);
        order_statistic_btn = findViewById(R.id.statistic);

        discount_btn = findViewById(R.id.discount);
        vouncher_btn = findViewById(R.id.vouncher);

        sign_out_admin=findViewById(R.id.sign_out_admin);

        category_btn.setOnClickListener(view -> {
            Intent intent=new Intent(MainActivityAdmin.this, UpdateCategoryActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
        type_btn.setOnClickListener(view -> {
            Intent intent=new Intent(MainActivityAdmin.this, UpdateTypeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        });
        supplies_btn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivityAdmin.this, UpdateSuppliesActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        });
        supplies_import_btn.setOnClickListener(view -> {
            Intent intent=new Intent(MainActivityAdmin.this, SuppliesImportActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        });

        service_btn.setOnClickListener(view -> {
            Intent intent=new Intent(MainActivityAdmin.this, UpdateServiceActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        });
        service_booking_btn.setOnClickListener(view -> {
//            Intent intent=new Intent(MainActivityAdmin.this, UpdateServiceActivity.class);
//            startActivity(intent);
        });
        branch_store_btn.setOnClickListener(view -> {
            Intent intent=new Intent(MainActivityAdmin.this, UpdateBranchStoreActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        });

        order_approve_btn.setOnClickListener(view -> {
            Intent intent=new Intent(MainActivityAdmin.this, UpdateOrderActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        });
        order_statistic_btn.setOnClickListener(view -> {
//            Intent intent=new Intent(MainActivityAdmin.this, UpdateServiceActivity.class);
//            startActivity(intent);
        });

        discount_btn.setOnClickListener(view -> {
            Intent intent=new Intent(MainActivityAdmin.this, UpdateDiscountActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        });
        vouncher_btn.setOnClickListener(view -> {
            Intent intent=new Intent(MainActivityAdmin.this, UpdateVoucherActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        });

        sign_out_admin.setOnClickListener(view -> {
            signOutUser();
        });
    }

    private void signOutUser() {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("accountType");  // This will remove the accountType value
        editor.apply();

        mAuth.signOut();  // Firebase Auth sign out
        Intent intent = new Intent(MainActivityAdmin.this, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

