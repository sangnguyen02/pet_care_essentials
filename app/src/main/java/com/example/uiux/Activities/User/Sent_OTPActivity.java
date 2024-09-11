package com.example.uiux.Activities.User;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.uiux.Model.UserAccountRepository;
import com.example.uiux.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;

import java.util.concurrent.TimeUnit;

public class Sent_OTPActivity extends AppCompatActivity {

    private EditText inputCode1, inputCode2, inputCode3, inputCode4, inputCode5, inputCode6;
    private Button Btn_Verify;
    private String verificationId;
    private Long timeoutSeconds = 30L;
    private TextView resendOtpTextView, resendOTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sent_otpactivity);

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        String phone = intent.getStringExtra("phone");
        String PhoneNumer=intent.getStringExtra("PhoneNumber");
        Log.e("PhoneNumber", PhoneNumer);

        // Ánh xạ các view
        inputCode1 = findViewById(R.id.inputCode1);
        inputCode2 = findViewById(R.id.inputCode2);
        inputCode3 = findViewById(R.id.inputCode3);
        inputCode4 = findViewById(R.id.inputCode4);
        inputCode5 = findViewById(R.id.inputCode5);
        inputCode6 = findViewById(R.id.inputCode6);
        resendOtpTextView = findViewById(R.id.resend_otp_textview2);
        resendOTP = findViewById(R.id.resendOTP);
        Btn_Verify = findViewById(R.id.btn_verify);
        verificationId = intent.getStringExtra("verificationId");

        // Xử lý nhập OTP
        SennOTPInput();

        // Xử lý khi nhấn nút Verify
        Btn_Verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inputCode1.getText().toString().isEmpty() || inputCode2.getText().toString().isEmpty() ||
                        inputCode3.getText().toString().isEmpty() || inputCode4.getText().toString().isEmpty() ||
                        inputCode5.getText().toString().isEmpty() || inputCode6.getText().toString().isEmpty()) {
                    Toast.makeText(Sent_OTPActivity.this, "Enter OTP", Toast.LENGTH_SHORT).show();
                } else {
                    String code = inputCode1.getText().toString()
                            + inputCode2.getText().toString()
                            + inputCode3.getText().toString()
                            + inputCode4.getText().toString()
                            + inputCode5.getText().toString()
                            + inputCode6.getText().toString();
                    if (verificationId != null) {
                        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationId, code);
                        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            UserAccountRepository account=new UserAccountRepository();
                                            account.getAccountByPhone(PhoneNumer);
                                            Intent MainUserIntent = new Intent(getApplicationContext(), PhoneUpdateProfileActivity.class);
                                            MainUserIntent.putExtra("PhoneNumer",PhoneNumer);
                                            MainUserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(MainUserIntent);
                                        } else {
                                            Toast.makeText(Sent_OTPActivity.this, "Invalid Code", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }
            }
        });

        // Xử lý khi nhấn nút Resend OTP
        resendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                         phone, // Số điện thoại
                        timeoutSeconds, // Thời gian timeout
                        TimeUnit.SECONDS, // Đơn vị thời gian
                        Sent_OTPActivity.this, // Activity hiện tại
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                // Xử lý khi OTP được xác minh tự động
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(Sent_OTPActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                verificationId = s;
                                Log.e("OTP", s);
                                Toast.makeText(Sent_OTPActivity.this, "OTP Sent", Toast.LENGTH_SHORT).show();

                                // Bắt đầu đếm ngược
                                startResendTimer();
                            }
                        });
            }
        });
    }
    // Hàm để bắt đầu đếm ngược 30 giây
    private void startResendTimer() {
        // Ẩn nút Resend OTP và hiển thị TextView đếm ngược
        resendOTP.setVisibility(View.GONE);
        resendOtpTextView.setVisibility(View.VISIBLE);

        // Bộ đếm ngược 30 giây
        new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Cập nhật thời gian còn lại trong TextView
                resendOtpTextView.setText("Resend available in: " + millisUntilFinished / 1000 + "s");
            }

            @Override
            public void onFinish() {
                // Khi kết thúc đếm ngược, hiển thị lại nút Resend OTP và ẩn TextView
                resendOtpTextView.setVisibility(View.GONE);
                resendOTP.setVisibility(View.VISIBLE);
            }
        }.start();
    }
    // Hàm xử lý việc di chuyển con trỏ khi nhập mã OTP
    private void SennOTPInput() {
        inputCode1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    inputCode2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        inputCode2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    inputCode3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        inputCode3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    inputCode4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        inputCode4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    inputCode5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        inputCode5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    inputCode6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });
    }
}
