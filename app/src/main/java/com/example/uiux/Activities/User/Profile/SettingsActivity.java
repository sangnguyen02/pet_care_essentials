package com.example.uiux.Activities.User.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uiux.Activities.EntryActivity;
import com.example.uiux.Activities.SplashActivity;
import com.example.uiux.R;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;


public class SettingsActivity extends AppCompatActivity {
    String accountId;
    MaterialCardView mcv_address_setting, mcv_sign_out;
    ImageView img_back_settings;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.transparent));
        setContentView(R.layout.activity_settings);

        initWidget();
        mAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        accountId = intent.getStringExtra("account_id");

        img_back_settings.setOnClickListener(view -> finish());

        mcv_address_setting.setOnClickListener(view -> {
            Intent gotoAddress= new Intent(SettingsActivity.this, UpdateAddressActivity.class);
            gotoAddress.putExtra("account_id",accountId);
            startActivity(gotoAddress);
        });

        mcv_sign_out.setOnClickListener(view -> {
            signOutUser();
        });

    }
    private void initWidget()
    {
        img_back_settings = findViewById(R.id.img_back_settings);
        mcv_address_setting = findViewById(R.id.mcv_address_setting);
        mcv_sign_out = findViewById(R.id.mcv_sign_out);
    }

    private void signOutUser() {
        mAuth.signOut();  // Firebase Auth sign out
        Intent intent = new Intent(SettingsActivity.this, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

