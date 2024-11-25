package com.example.uiux.Activities.User.AccountWallet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uiux.Activities.User.Order.PaymentNotificationActivity;
import com.example.uiux.Model.AccountWallet;
import com.example.uiux.Model.Order;
import com.example.uiux.Model.Voucher;
import com.example.uiux.R;
import com.example.uiux.ZaloPay.Api.CreateOrder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class DisplayAccountWallet extends AppCompatActivity {
    private EditText etBalance;
    private Button btnAddBalance;
    private TextView txtBalance;

    private String accountId;

    // Firebase Database Reference
    private DatabaseReference walletRef;
    private DatabaseReference walletHistoryRef;
    private String wallet_Id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_display_account_wallet);
        etBalance=findViewById(R.id.et_balance);
        btnAddBalance=findViewById(R.id.btn_add_balance);
        txtBalance=findViewById(R.id.balance);
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        // ZaloPay SDK Init
        ZaloPaySDK.init(553, Environment.SANDBOX);
        SharedPreferences preferences =  getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        accountId = preferences.getString("accountID", null);
        walletRef = FirebaseDatabase.getInstance().getReference("Account Wallet");
        // Gọi phương thức LoadBalance
        LoadBalance();
        btnAddBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent để mở ứng dụng MyWallet
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.example.myWallet");

                if (launchIntent != null) {
                    // Gửi dữ liệu kèm theo
                    launchIntent.putExtra("balanceAmount", etBalance.getText().toString());
                    launchIntent.putExtra("accountId", accountId);

                    // Mở ứng dụng MyWallet
                    startActivity(launchIntent);
                } else {
                    // Ứng dụng MyWallet không được cài đặt
                    Toast.makeText(DisplayAccountWallet.this, "Ứng dụng MyWallet chưa được cài đặt.", Toast.LENGTH_SHORT).show();
                }
            }
        });




    }
//    private void UpdateBalance() {
//        String addAmountStr = etBalance.getText().toString();
//        if (addAmountStr.isEmpty()) {
//            Toast.makeText(this, "Enter a valid amount!", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        double addAmount = Double.parseDouble(addAmountStr);
//
//        walletRef.child(wallet_Id).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                AccountWallet wallet = snapshot.getValue(AccountWallet.class);
//                if (wallet != null) {
//                    double newBalance = wallet.getBalance() + addAmount;
//                    wallet.setBalance(newBalance);
//
//                    walletRef.child(wallet_Id).setValue(wallet)
//                            .addOnSuccessListener(aVoid -> {
//                                txtBalance.setText(String.format("Balance: %.2f", newBalance));
//                                Toast.makeText(DisplayAccountWallet.this, "Balance updated successfully!", Toast.LENGTH_SHORT).show();
//                            })
//                            .addOnFailureListener(e -> {
//                                Toast.makeText(DisplayAccountWallet.this, "Failed to update balance!", Toast.LENGTH_SHORT).show();
//                            });
//                } else {
//                    Toast.makeText(DisplayAccountWallet.this, "Wallet not found!", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(DisplayAccountWallet.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void CreateHistoryMessage()
//    {
//
//    }

    private void LoadBalance() {
        // Kiểm tra nếu accountId null
        if (accountId == null) {
            txtBalance.setText("Account ID not found!");
            return;
        }

        // Truy vấn Firebase để tìm ví theo account_id
        walletRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                // Duyệt qua tất cả các Wallet
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    AccountWallet wallet = snapshot.getValue(AccountWallet.class);
                    if (wallet != null && wallet.getAccount_id().equals(accountId)) {
                        wallet_Id=wallet.getWallet_id();
                        txtBalance.setText(String.format("Balance: %.2f", wallet.getBalance()));
                        break;
                    }
                    else {
                        // Không tìm thấy ví cho account_id
                        txtBalance.setText("No wallet found for this account.");
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(DisplayAccountWallet.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onNewIntent(Intent intent2) {
        super.onNewIntent(intent2);
        ZaloPaySDK.getInstance().onResult(intent2);
    }


}