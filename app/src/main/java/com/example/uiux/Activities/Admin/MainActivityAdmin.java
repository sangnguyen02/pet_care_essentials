package com.example.uiux.Activities.Admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

//import com.example.uiux.Activities.Admin.Branch.UpdateBranchStoreActivity;
import com.example.uiux.Activities.Admin.Branch.BranchStoreActivity;
import com.example.uiux.Activities.Admin.Branch.UpdateBranchStoreActivity;
import com.example.uiux.Activities.Admin.Category.CategoryActivity;
import com.example.uiux.Activities.Admin.Category.UpdateCategoryActivity;
import com.example.uiux.Activities.Admin.Discount.UpdateDiscountActivity;
import com.example.uiux.Activities.Admin.Order.DisplayReturnActivity;
import com.example.uiux.Activities.Admin.Services.DisplayServiceBookingActivity;
import com.example.uiux.Activities.Admin.Services.UpdateServiceActivity;
import com.example.uiux.Activities.Admin.Statistic.StatisticAllRevenueActivity;
import com.example.uiux.Activities.Admin.Supplies.UpdateSuppliesActivity;
import com.example.uiux.Activities.Admin.Supplies.SuppliesImportActivity;
import com.example.uiux.Activities.Admin.Type.TypeActivity;
import com.example.uiux.Activities.Admin.Type.UpdateTypeActivity;
import com.example.uiux.Activities.Admin.Voucher.UpdateVoucherActivity;
import com.example.uiux.Activities.SplashActivity;
import com.example.uiux.Activities.User.MainActivityUser;
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
            branch_store_btn, order_approve_btn, order_review_btn, order_statistic_btn, discount_btn, vouncher_btn;

    ImageButton sign_out_admin;
    long lastBackPressedTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
        setContentView(R.layout.activity_main_admin);
        OnBackPressedDispatcher dispatcher = getOnBackPressedDispatcher();
        dispatcher.addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (lastBackPressedTime + 2000 > System.currentTimeMillis()) {
                    // Nếu bấm lần thứ 2 trong khoảng thời gian ngắn, thoát ứng dụng
                    finish(); // Hoặc gọi super.onBackPressed() nếu muốn thực hiện hành động mặc định
                } else {
                    // Nếu không, cập nhật thời gian và thông báo
                    lastBackPressedTime = System.currentTimeMillis();
                    Toast.makeText(MainActivityAdmin.this, "Press back again to exit", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mAuth = FirebaseAuth.getInstance();

        category_btn=findViewById(R.id.category);
        type_btn=findViewById(R.id.type);
        supplies_btn=findViewById(R.id.supply);
        supplies_import_btn=findViewById(R.id.import_supplies);

        service_btn=findViewById(R.id.service);
        service_booking_btn = findViewById(R.id.service_booking);
        branch_store_btn=findViewById(R.id.branch);

        order_approve_btn = findViewById(R.id.approve);
        order_review_btn = findViewById(R.id.review);
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
            Intent intent=new Intent(MainActivityAdmin.this, DisplayServiceBookingActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
        branch_store_btn.setOnClickListener(view -> {
            Intent intent=new Intent(MainActivityAdmin.this, UpdateBranchStoreActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        });

        order_approve_btn.setOnClickListener(view -> {
            Intent intent=new Intent(MainActivityAdmin.this, DisplayReturnActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        });

        order_review_btn.setOnClickListener(view -> {
            Intent intent=new Intent(MainActivityAdmin.this, UpdateOrderActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        order_statistic_btn.setOnClickListener(view -> {
            Intent intent=new Intent(MainActivityAdmin.this, StatisticAllRevenueActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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

