package com.example.uiux.Activities.User.AccountWallet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.uiux.Model.AccountWallet;
import com.example.uiux.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class RegisterWalletActivity extends AppCompatActivity {

    private EditText etPin, etConfirmPin;
    private Button btnRegisterWallet;

    private String accountId;

    // Firebase Database Reference
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_wallet);

        SharedPreferences preferences =  getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        accountId = preferences.getString("accountID", null);
        Log.e("ID",accountId);
        etPin=findViewById(R.id.et_pin);
        etConfirmPin=findViewById(R.id.et_confirm_pin);
        btnRegisterWallet=findViewById(R.id.btn_register_wallet);

        btnRegisterWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GenerateWallet();
            }
        });
    }

    private void GenerateWallet() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Account Wallet");
        String pin = etPin.getText().toString().trim();
        String confirmPin = etConfirmPin.getText().toString().trim();

        if (TextUtils.isEmpty(pin) || TextUtils.isEmpty(confirmPin)) {
            Toast.makeText(this, "Please enter and confirm your PIN", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!pin.equals(confirmPin)) {
            Toast.makeText(this, "PINs do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy tất cả các Wallet và kiểm tra
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isAccountIdExist = false;

                // Duyệt qua tất cả các Wallet
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    AccountWallet wallet = snapshot.getValue(AccountWallet.class);
                    if (wallet != null && wallet.getAccount_id().equals(accountId)) {
                        isAccountIdExist = true;
                        break;
                    }
                }

                if (isAccountIdExist) {
                    // Nếu account_id đã tồn tại
                    Log.e("Firebase Check", "Account already registered!");
                    Toast.makeText(RegisterWalletActivity.this, "Account already registered!", Toast.LENGTH_SHORT).show();
                } else {
                    // Nếu account_id chưa tồn tại, tiến hành đăng ký
                    Log.e("Firebase Check", "Account not found, proceeding to register.");
                    registerNewWallet(pin);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(RegisterWalletActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    // Phương thức để đăng ký wallet mới
    private void registerNewWallet(String pin) {
        String account_wallet_id = databaseReference.push().getKey();
        if (account_wallet_id == null) {
            Toast.makeText(this, "Failed to generate Wallet ID. Try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        AccountWallet accountWallet = new AccountWallet();
        accountWallet.setAccount_id(accountId);
        accountWallet.setWallet_id(account_wallet_id);
        accountWallet.setPIN(pin);
        accountWallet.setBalance(0);

        databaseReference.child(account_wallet_id).setValue(accountWallet).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.e("Firebase Write", "Wallet registered successfully!");
                Toast.makeText(RegisterWalletActivity.this, "Register successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("Firebase Write", "Wallet registration failed!");
                Toast.makeText(RegisterWalletActivity.this, "Failed to Register: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}