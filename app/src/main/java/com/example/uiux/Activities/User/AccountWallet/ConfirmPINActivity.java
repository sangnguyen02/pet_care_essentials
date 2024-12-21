package com.example.uiux.Activities.User.AccountWallet;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uiux.Model.AccountWallet;
import com.example.uiux.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConfirmPINActivity extends AppCompatActivity {
    private EditText inputCode1, inputCode2, inputCode3, inputCode4, inputCode5, inputCode6;
    private DatabaseReference databaseReference;
    String wallet_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
        setContentView(R.layout.activity_confirm_pinactivity);
        initWidget();
        Intent intent= getIntent();

       wallet_id=intent.getStringExtra("wallet_id");
       databaseReference= FirebaseDatabase.getInstance().getReference("Account Wallet");

    }

    private void initWidget() {
        inputCode1 = findViewById(R.id.inputCode1);
        inputCode2 = findViewById(R.id.inputCode2);
        inputCode3 = findViewById(R.id.inputCode3);
        inputCode4 = findViewById(R.id.inputCode4);
        inputCode5 = findViewById(R.id.inputCode5);
        inputCode6 = findViewById(R.id.inputCode6);

        // Gắn TextWatcher cho từng EditText
        setupPinInput(inputCode1, inputCode2);
        setupPinInput(inputCode2, inputCode3);
        setupPinInput(inputCode3, inputCode4);
        setupPinInput(inputCode4, inputCode5);
        setupPinInput(inputCode5, inputCode6);

        // Ô cuối cùng kiểm tra PIN khi người dùng nhập xong
        inputCode6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 1) {
                    // Khi nhập xong ký tự cuối, kiểm tra mã PIN
                    String enteredPIN = inputCode1.getText().toString() +
                            inputCode2.getText().toString() +
                            inputCode3.getText().toString() +
                            inputCode4.getText().toString() +
                            inputCode5.getText().toString() +
                            inputCode6.getText().toString();

                    checkPIN(enteredPIN);
                }
            }
        });
    }

    private void checkPIN(String enteredPIN) {
        databaseReference.child(wallet_id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    AccountWallet wallet = task.getResult().getValue(AccountWallet.class);
                    if (wallet != null) {
                        String savedPIN = wallet.getPIN();
                        if (enteredPIN.equals(savedPIN)) {
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            showToast("Incorrect PIN. Please try again!");
                            clearPinInputs();
                        }
                    }
                } else {
                    showToast("Wallet does not exist. Please check again!");
                }
            } else {
                showToast("Error checking PIN code: " + task.getException().getMessage());
            }
        });
    }



    // Hàm xóa nội dung các ô nhập PIN nếu PIN sai
    private void clearPinInputs() {
        inputCode1.setText("");
        inputCode2.setText("");
        inputCode3.setText("");
        inputCode4.setText("");
        inputCode5.setText("");
        inputCode6.setText("");
        inputCode1.requestFocus(); // Đưa con trỏ về ô đầu tiên
    }

    private void setupPinInput(EditText currentEditText, EditText nextEditText) {
        currentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 1) {
                    // Khi nhập xong 1 ký tự, chuyển con trỏ sang ô tiếp theo
                    nextEditText.requestFocus();
                }
            }
        });
    }


    // Hàm hiển thị thông báo
    private void showToast(String message) {
        Toast.makeText(ConfirmPINActivity.this, message, Toast.LENGTH_SHORT).show();
    }

}