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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uiux.Activities.Admin.MainActivityAdmin;
import com.example.uiux.Activities.Admin.Type.TypeActivity;
import com.example.uiux.Activities.User.Order.PaymentNotificationActivity;
import com.example.uiux.Activities.User.Order.PaypalActivity;
import com.example.uiux.Adapters.BalanceAdapter;
import com.example.uiux.Adapters.ChipAdapter;
import com.example.uiux.Adapters.WalletTransactionsAdapter;
import com.example.uiux.Model.AccountWallet;
import com.example.uiux.Model.Order;
import com.example.uiux.Model.Supplies;
import com.example.uiux.Model.Voucher;
import com.example.uiux.Model.WalletHistory;
import com.example.uiux.R;
import com.example.uiux.Utils.CurrencyFormatter;
import com.example.uiux.ZaloPay.Api.CreateOrder;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class DisplayAccountWallet extends AppCompatActivity {
    private RecyclerView rcv_balance, rcv_wallet_transactions;
    private List<String> balanceList;
    private List<WalletHistory> transactionList;
    private BalanceAdapter balanceAdapter;
    private WalletTransactionsAdapter walletTransactionsAdapter;
    private TextInputEditText etBalance;
    private MaterialButton btnAddBalance;
    private ImageView img_wallet_avatar, img_back_wallet;
    private TextView txtBalance, tv_wallet_username;
    private String accountId;
    private DatabaseReference walletRef, walletHistoryRef, accountRef;
    private String wallet_Id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
        setContentView(R.layout.activity_display_account_wallet);

        initWidget();

        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        // ZaloPay SDK Init
        ZaloPaySDK.init(553, Environment.SANDBOX);

        Intent intent = getIntent();
        wallet_Id = intent.getStringExtra("wallet_id");
        Log.e("Wallet account display", wallet_Id);


        SharedPreferences preferences =  getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        accountId = preferences.getString("accountID", null);
        Log.e(" account display", accountId);

        walletRef = FirebaseDatabase.getInstance().getReference("Account Wallet");
        walletHistoryRef = FirebaseDatabase.getInstance().getReference("Wallet History");
        accountRef = FirebaseDatabase.getInstance().getReference("Account");
        // Gọi phương thức LoadBalance
        loadBalance();
        loadUserProfile();
        loadWalletHistory();

        btnAddBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mở ConfirmPINActivity để xác nhận mã PIN
                Intent intent = new Intent(DisplayAccountWallet.this, ConfirmPINActivity.class);
                intent.putExtra("wallet_id", wallet_Id);
                confirmPINLauncher.launch(intent);
            }
        });


    }

    private void initWidget() {
        img_back_wallet = findViewById(R.id.img_back_wallet);
        img_back_wallet.setOnClickListener(view -> finish());
        img_wallet_avatar = findViewById(R.id.img_wallet_avatar);
        tv_wallet_username = findViewById(R.id.tv_wallet_username);
        etBalance=findViewById(R.id.et_balance);
        btnAddBalance=findViewById(R.id.btn_add_balance);
        txtBalance=findViewById(R.id.balance);
        rcv_balance = findViewById(R.id.rcv_balance);
        rcv_wallet_transactions = findViewById(R.id.rcv_wallet_transactions);

        balanceList = new ArrayList<>();
        balanceList.add("10000");
        balanceList.add("20000");
        balanceList.add("50000");
        balanceList.add("100000");
        balanceList.add("200000");
        balanceList.add("500000");
        balanceList.add("1000000");
        balanceList.add("2000000");
        balanceList.add("5000000");
        balanceList.add("10000000");
        transactionList = new ArrayList<>();


        rcv_balance.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        balanceAdapter = new BalanceAdapter(balanceList, this);
        balanceAdapter.setOnItemClickListener(balance -> {
            etBalance.setText(balance);
        });
        rcv_balance.setAdapter(balanceAdapter);

        rcv_wallet_transactions.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
        walletTransactionsAdapter = new WalletTransactionsAdapter(transactionList, this);
        rcv_wallet_transactions.setAdapter(walletTransactionsAdapter);
    }


    private void loadBalance() {
        // Kiểm tra nếu accountId null
        if (accountId == null) {
            txtBalance.setText("Account ID not found!");
            return;
        }

        // Truy vấn Firebase để tìm ví theo account_id
        walletRef.child(wallet_Id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if(dataSnapshot.exists()) {
                    AccountWallet wallet = dataSnapshot.getValue(AccountWallet.class);
                    txtBalance.setText(CurrencyFormatter.formatCurrency(wallet.getBalance(), getString(R.string.currency_vn)));
                } else {
                        // Không tìm thấy ví cho account_id
                    Log.e("Log Wallet account display", "No wallet found for this account.");

                    txtBalance.setText("No wallet found for this account.");
                }

                Log.e("Wallet ID", wallet_Id);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(DisplayAccountWallet.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
//    @Override
//    protected void onNewIntent(Intent intent2) {
//        super.onNewIntent(intent2);
//        ZaloPaySDK.getInstance().onResult(intent2);
//    }

    private void loadUserProfile() {
        accountRef.child(accountId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Get user data
                    Model.Account account = dataSnapshot.getValue(Model.Account.class);

                    if (account != null) {
                        tv_wallet_username.setText(account.getFullname() != null ? account.getFullname() : "Chưa cập nhật");
                        // Load image if it exists
                        if (account.getImage() != null) {
                            Glide.with(getApplicationContext())
                                    .load(account.getImage())
                                    .placeholder(R.drawable.user)
                                    .error(R.drawable.user)
                                    .into(img_wallet_avatar);
                        }
                    }

                } else {
                    // If the user doesn't exist, prompt them to create a profile
                    Toast.makeText(getApplicationContext(), "No existing profile found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                Toast.makeText(getApplicationContext(), "Failed to load data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadWalletHistory() {
        Log.e("Wallet ID trong history", wallet_Id);

        walletHistoryRef.orderByChild("wallet_id").equalTo(wallet_Id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                transactionList.clear();
                if (dataSnapshot.exists()) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        WalletHistory walletHistory = snapshot.getValue(WalletHistory.class);
                        transactionList.add(walletHistory);
                    }
                    Log.e("Transaction List", String.valueOf(transactionList.size()));
                    walletTransactionsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadBalance();
//        loadWalletHistory();
    }
    // Xử lý kết quả từ ConfirmPINActivity
    private final ActivityResultLauncher<Intent> confirmPINLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // Nếu PIN đúng, chuyển sang PaypalActivity
                    Intent intent = new Intent(DisplayAccountWallet.this, PaypalActivity.class);
                    intent.putExtra("account_id", accountId);
                    intent.putExtra("wallet_id", wallet_Id);
                    intent.putExtra("balance", etBalance.getText().toString());
                    startActivity(intent);
                } else {
                    // Nếu PIN sai hoặc người dùng hủy
                    Toast.makeText(this, "PIN không hợp lệ hoặc bạn đã hủy.", Toast.LENGTH_SHORT).show();
                }
            });
}