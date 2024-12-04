package com.example.uiux.Activities.User.Order;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.uiux.Model.AccountWallet;
import com.example.uiux.Model.WalletHistory;
import com.example.uiux.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.paypal.checkout.approve.Approval;
import com.paypal.checkout.approve.OnApprove;
import com.paypal.checkout.createorder.CreateOrder;
import com.paypal.checkout.createorder.CreateOrderActions;
import com.paypal.checkout.createorder.CurrencyCode;
import com.paypal.checkout.createorder.OrderIntent;
import com.paypal.checkout.createorder.UserAction;
import com.paypal.checkout.order.Amount;
import com.paypal.checkout.order.AppContext;
import com.paypal.checkout.order.CaptureOrderResult;
import com.paypal.checkout.order.OnCaptureComplete;
import com.paypal.checkout.order.OrderRequest;
import com.paypal.checkout.order.PurchaseUnit;
import com.paypal.checkout.paymentbutton.PaymentButtonContainer;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PaypalActivity extends AppCompatActivity {

    private static final String TAG = "MyTag";
    PaymentButtonContainer paymentButtonContainer;
    DatabaseReference walletRef;
    DatabaseReference wallet_history;
    private String wallet_Id;
    private String account_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.transparent));
        setContentView(R.layout.activity_paypal);

        paymentButtonContainer = findViewById(R.id.payment_button_container);
        walletRef = FirebaseDatabase.getInstance().getReference("Account Wallet");
        wallet_history = FirebaseDatabase.getInstance().getReference("Wallet History");
        wallet_Id = getIntent().getStringExtra("wallet_id");
//        Log.e("wallet_Id",wallet_Id);
        account_id = getIntent().getStringExtra("account_id");
//        Log.e("account_id",account_id);
        String amount =getIntent().getStringExtra("balance");
//        Log.e("Money",amount);
        String amountUSD = convertVNDToUSD(amount);
//        Log.e("Money",amountUSD);


        paymentButtonContainer.setup(
                new CreateOrder() {
                    @Override
                    public void create(@NotNull CreateOrderActions createOrderActions) {
                        Log.d(TAG, "create: ");
                        ArrayList<PurchaseUnit> purchaseUnits = new ArrayList<>();
                        purchaseUnits.add(
                                new PurchaseUnit.Builder()
                                        .amount(
                                                new Amount.Builder()
                                                        .currencyCode(CurrencyCode.USD)
                                                        .value(amountUSD)
                                                        .build()
                                        )
                                        .build()
                        );
                        OrderRequest order = new OrderRequest(
                                OrderIntent.CAPTURE,
                                new AppContext.Builder()
                                        .userAction(UserAction.PAY_NOW)
                                        .build(),
                                purchaseUnits
                        );
                        createOrderActions.create(order, (CreateOrderActions.OnOrderCreated) null);
                    }
                },
                new OnApprove() {
                    @Override
                    public void onApprove(@NotNull Approval approval) {
                        approval.getOrderActions().capture(new OnCaptureComplete() {
                            @Override
                            public void onCaptureComplete(@NotNull CaptureOrderResult result) {
                                Log.d(TAG, String.format("CaptureOrderResult: %s", result));
                                Toast.makeText(PaypalActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                                UpdateBalance(amount);
                                CreateHistory(amount);
                            }
                        });
                    }
                }
        );
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
        private void UpdateBalance(String addAmountStr) {

        if (addAmountStr.isEmpty()) {
            Toast.makeText(this, "Enter a valid amount!", Toast.LENGTH_SHORT).show();
            return;
        }

        double addAmount = Double.parseDouble(addAmountStr);

        walletRef.child(wallet_Id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                AccountWallet wallet = snapshot.getValue(AccountWallet.class);
                if (wallet != null) {
                    double newBalance = wallet.getBalance() + addAmount;
                    wallet.setBalance(newBalance);

                    walletRef.child(wallet_Id).setValue(wallet)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(PaypalActivity.this, "Balance updated successfully!", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(PaypalActivity.this, "Failed to update balance!", Toast.LENGTH_SHORT).show();
                            });
                } else {
                    Toast.makeText(PaypalActivity.this, "Wallet not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PaypalActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void CreateHistory(String message) {
        // Tạo ID duy nhất cho lịch sử ví
        String walletHistoryId = wallet_history.push().getKey();

        if (walletHistoryId == null) {
            Toast.makeText(this, "Failed to generate history ID!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy thời gian hiện tại (ngày và giờ)
        String currentDate = getCurrentFormattedDate();

        // Tạo đối tượng WalletHistory
        WalletHistory history = new WalletHistory();
        history.setWallet_history_id(walletHistoryId);
        history.setWallet_id(wallet_Id);
        history.setMessage(message);
        history.setStatus("+");
        history.setDate(currentDate);

        // Lưu vào Firebase
        wallet_history.child(walletHistoryId).setValue(history)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "History created successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to create history: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    private String getCurrentFormattedDate() {
        // Tạo định dạng với mẫu ngày
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // Lấy ngày giờ hiện tại
        Date date = new Date();
        // Định dạng ngày giờ thành chuỗi
        return dateFormat.format(date);
    }

    private String convertVNDToUSD(String amountVND) {
        final double EXCHANGE_RATE = 25400.0; // Tỷ giá giả định
        try {
            // Chuyển đổi chuỗi sang số
            double amountInVND = Double.parseDouble(amountVND);
            // Chuyển đổi sang USD
            double amountInUSD = amountInVND / EXCHANGE_RATE;
            // Định dạng số theo chuẩn Locale.US
            return String.format(Locale.US, "%.2f", amountInUSD);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Invalid VND amount: " + amountVND, e);
            return "0.00"; // Giá trị mặc định trong trường hợp lỗi
        }
    }

}