package com.example.uiux.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.uiux.Activities.Admin.MainActivityAdmin;
import com.example.uiux.Activities.User.MainActivityUser;
import com.example.uiux.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


public class SplashActivity extends AppCompatActivity {

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;
    private FirebaseAuth mAuth;
    DatabaseReference accountRef;
    Integer accountType = 0;

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
                    String formattedPhoneNumber = formatPhoneNumber(currentUser.getPhoneNumber());
                    getAccountTypeByPhone(formattedPhoneNumber);
                } else {
                    Intent intent = new Intent(SplashActivity.this, EntryActivity.class);
                    startActivity(intent);
                }
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

    void getAccountTypeByPhone(String phone) {
        accountRef = FirebaseDatabase.getInstance().getReference("Account");
        accountRef.orderByChild("phone").equalTo(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot accountSnapshot : dataSnapshot.getChildren()) {
                        accountType = accountSnapshot.child("account_type").getValue(Integer.class);
                        Log.e("Account Info", "Account Type: " + accountType);

                        // Kiểm tra accountType và chuyển tới activity tương ứng
                        if (accountType == 1) {
                            Intent intent = new Intent(SplashActivity.this, MainActivityAdmin.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(SplashActivity.this, MainActivityUser.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("phone_number", phone);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    }
                } else {
                    Log.d("Account Info", "No account found with this phone number.");
                    Intent intent = new Intent(SplashActivity.this, EntryActivity.class);
                    startActivity(intent);
                    finish(); // Kết thúc SplashActivity
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Account Info", "Error fetching account: " + databaseError.getMessage());
            }
        });
    }
}
