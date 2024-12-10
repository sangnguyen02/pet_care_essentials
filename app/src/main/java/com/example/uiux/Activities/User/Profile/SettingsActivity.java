package com.example.uiux.Activities.User.Profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.uiux.Activities.SplashActivity;
import com.example.uiux.Activities.User.AccountWallet.DisplayAccountWallet;
import com.example.uiux.Activities.User.AccountWallet.RegisterWalletActivity;
import com.example.uiux.Model.AccountWallet;
import com.example.uiux.R;
import com.example.uiux.Utils.CurrencyFormatter;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SettingsActivity extends AppCompatActivity {
    String accountId;
    MaterialCardView mcv_address_setting, mcv_sign_out,mcv_wallet,mcv_view_wallet;
    ImageView img_back_settings;
    FirebaseAuth mAuth;
    DatabaseReference walletRef;
    String wallet_Id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.transparent));
        setContentView(R.layout.activity_settings);

        initWidget();
        mAuth = FirebaseAuth.getInstance();

        SharedPreferences preferences =  getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        accountId = preferences.getString("accountID", null);
        Log.e("Wallet account 0", accountId);

        walletRef = FirebaseDatabase.getInstance().getReference("Account Wallet");
        checkWalletExist();



        img_back_settings.setOnClickListener(view -> finish());

        mcv_address_setting.setOnClickListener(view -> {
            Intent gotoAddress= new Intent(SettingsActivity.this, UpdateAddressActivity.class);
            gotoAddress.putExtra("account_id",accountId);
            startActivity(gotoAddress);
        });
        mcv_wallet.setOnClickListener(view -> {

        });

        mcv_view_wallet.setOnClickListener(view -> {
            if(wallet_Id == null) {
                Intent gotoWallet= new Intent(SettingsActivity.this, RegisterWalletActivity.class);
                gotoWallet.putExtra("account_id",accountId);
                startActivity(gotoWallet);
            } else {
                Intent gotoWallet= new Intent(SettingsActivity.this, DisplayAccountWallet.class);
                gotoWallet.putExtra("wallet_id",wallet_Id);
                startActivity(gotoWallet);
            }

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
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("accountType");  // This will remove the accountType value
        editor.apply();

        mAuth.signOut();  // Firebase Auth sign out
        Intent intent = new Intent(SettingsActivity.this, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    void checkWalletExist() {
        walletRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                // Duyệt qua tất cả các Wallet
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    AccountWallet wallet = snapshot.getValue(AccountWallet.class);
                    Log.e("Wallet ID", wallet.getWallet_id());
                    Log.e("Wallet account 1", wallet.getAccount_id());
                    Log.e("Wallet account 2", accountId);


                    if (wallet != null && wallet.getAccount_id().equals(accountId)) {
                        wallet_Id=wallet.getWallet_id();
                        Log.e("Wallet ID", wallet_Id);
                        break;

                    }
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SettingsActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

