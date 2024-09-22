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

import com.example.uiux.R;
import com.google.android.material.card.MaterialCardView;

public class SettingsActivity extends AppCompatActivity {
    String accountId;
    MaterialCardView mcv_address_setting;
    ImageView img_back_settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.transparent));
        setContentView(R.layout.activity_settings);

        initWidget();

        Intent intent = getIntent();
        accountId = intent.getStringExtra("account_id");

        img_back_settings.setOnClickListener(view -> finish());

        mcv_address_setting.setOnClickListener(view -> {
            Intent gotoAddress= new Intent(SettingsActivity.this, UpdateAddressActivity.class);
            gotoAddress.putExtra("account_id",accountId);
            startActivity(gotoAddress);
        });

    }
    private void initWidget()
    {
        img_back_settings = findViewById(R.id.img_back_settings);
        mcv_address_setting = findViewById(R.id.mcv_address_setting);
    }
}

