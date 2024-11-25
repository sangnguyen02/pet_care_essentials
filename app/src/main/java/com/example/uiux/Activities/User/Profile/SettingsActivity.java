package com.example.uiux.Activities.User.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.uiux.Activities.SplashActivity;
import com.example.uiux.Activities.User.AccountWallet.DisplayAccountWallet;
import com.example.uiux.Activities.User.AccountWallet.RegisterWalletActivity;
import com.example.uiux.R;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;


public class SettingsActivity extends AppCompatActivity {
    String accountId;
    MaterialCardView mcv_address_setting, mcv_sign_out,mcv_wallet,mcv_view_wallet;
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
        mcv_wallet.setOnClickListener(view -> {
            Intent gotoWallet= new Intent(SettingsActivity.this, RegisterWalletActivity.class);
            gotoWallet.putExtra("account_id",accountId);
            startActivity(gotoWallet);
        });
        mcv_view_wallet.setOnClickListener(view -> {
            Intent gotoWallet= new Intent(SettingsActivity.this, DisplayAccountWallet.class);
            startActivity(gotoWallet);
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
        mcv_wallet=findViewById(R.id.mcv_register_wallet);
        mcv_view_wallet=findViewById(R.id.mcv_view_wallet);
    }

    private void signOutUser() {
        mAuth.signOut();  // Firebase Auth sign out
        Intent intent = new Intent(SettingsActivity.this, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

