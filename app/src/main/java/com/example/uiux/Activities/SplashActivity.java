package com.example.uiux.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.uiux.Activities.User.MainActivityUser;
import com.example.uiux.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;


public class SplashActivity extends AppCompatActivity {

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.transparent));
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();

        runnable = new Runnable() {
            @Override
            public void run() {
                FirebaseUser currentUser = mAuth.getCurrentUser();  // Lấy thông tin user đã đăng nhập
                if (currentUser != null) {
                    Intent intent = new Intent(SplashActivity.this, MainActivityUser.class);
                    Bundle bundle = new Bundle();
                    String formattedPhoneNumber = formatPhoneNumber(currentUser.getPhoneNumber()); // Định dạng lại số điện thoại
                    //Log.e("Phone", Objects.requireNonNull(currentUser.getPhoneNumber()));
                    bundle.putString("phone_number", formattedPhoneNumber);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(SplashActivity.this, EntryActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        };
        handler.postDelayed(runnable, 3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    private String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber.startsWith("+84")) {
            return phoneNumber.replace("+84", "0"); // Thay thế +84 bằng 0
        } else if (phoneNumber.startsWith("84")) {
            return phoneNumber.replace("84", "0"); // Thay thế 84 bằng 0
        }
        return phoneNumber; // Nếu không có mã quốc gia, trả về số nguyên gốc
    }
}
