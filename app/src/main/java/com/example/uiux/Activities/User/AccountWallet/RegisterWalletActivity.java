package com.example.uiux.Activities.User.AccountWallet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.uiux.Model.AccountWallet;
import com.example.uiux.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class RegisterWalletActivity extends AppCompatActivity {
    private EditText inputCode1, inputCode2, inputCode3, inputCode4, inputCode5, inputCode6;
    private EditText confirmInput1, confirmInput2, confirmInput3, confirmInput4, confirmInput5, confirmInput6;
    private MaterialButton btnRegisterWallet;
    private String accountId;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.transparent));
        setContentView(R.layout.activity_register_wallet);

        SharedPreferences preferences =  getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        accountId = preferences.getString("accountID", null);
        Log.e("ID",accountId);

        initWidget();

    }

    void initWidget() {
        inputCode1 = findViewById(R.id.inputCode1);
        inputCode2 = findViewById(R.id.inputCode2);
        inputCode3 = findViewById(R.id.inputCode3);
        inputCode4 = findViewById(R.id.inputCode4);
        inputCode5 = findViewById(R.id.inputCode5);
        inputCode6 = findViewById(R.id.inputCode6);

        confirmInput1 = findViewById(R.id.confirm_input1);
        confirmInput2 = findViewById(R.id.confirm_input2);
        confirmInput3 = findViewById(R.id.confirm_input3);
        confirmInput4 = findViewById(R.id.confirm_input4);
        confirmInput5 = findViewById(R.id.confirm_input5);
        confirmInput6 = findViewById(R.id.confirm_input6);

        setupPinInputs();
        setupConfirmPinInputs();

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
        String pin = collectPin(inputCode1, inputCode2, inputCode3, inputCode4, inputCode5, inputCode6);
        String confirmPin = collectPin(confirmInput1, confirmInput2, confirmInput3, confirmInput4, confirmInput5, confirmInput6);

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


    private String collectPin(EditText... editTexts) {
        StringBuilder pinBuilder = new StringBuilder();
        for (EditText editText : editTexts) {
            String digit = editText.getText().toString().trim();
            if (TextUtils.isEmpty(digit)) {
                return "";
            }
            pinBuilder.append(digit);
        }
        return pinBuilder.toString();
    }


    private void setupPinInputs() {
        // Apply the same TextWatcher for all EditTexts
        EditText[] pinInputs = {inputCode1, inputCode2, inputCode3, inputCode4, inputCode5, inputCode6};

        for (int i = 0; i < pinInputs.length; i++) {
            final int index = i;

            pinInputs[index].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && index < pinInputs.length - 1) {
                        pinInputs[index + 1].requestFocus(); // Move to next input
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            pinInputs[index].setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (pinInputs[index].getText().toString().isEmpty() && index > 0) {
                        pinInputs[index - 1].requestFocus(); // Move to previous input on delete
                    }
                }
                return false;
            });
        }
    }

    private void setupConfirmPinInputs() {
        // Apply the same TextWatcher for all EditTexts
        EditText[] confirmPinInputs = {confirmInput1, confirmInput2, confirmInput3, confirmInput4, confirmInput5, confirmInput6};

        for (int i = 0; i < confirmPinInputs.length; i++) {
            final int index = i;

            confirmPinInputs[index].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && index < confirmPinInputs.length - 1) {
                        confirmPinInputs[index + 1].requestFocus(); // Move to next input
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            confirmPinInputs[index].setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (confirmPinInputs[index].getText().toString().isEmpty() && index > 0) {
                        confirmPinInputs[index - 1].requestFocus(); // Move to previous input on delete
                    }
                }
                return false;
            });
        }
    }


}