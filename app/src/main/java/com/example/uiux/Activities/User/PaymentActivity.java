package com.example.uiux.Activities.User;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiux.Adapters.CartAdapter;
import com.example.uiux.Adapters.CartPaymentAdapter;
import com.example.uiux.Model.Account_Address;
import com.example.uiux.Model.CartItem;
import com.example.uiux.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PaymentActivity extends AppCompatActivity {
    ImageView img_back_payment;
    TextView tv_buyer_name, tv_buyer_phone, tv_address_detail, tv_ward_district_province;
    RecyclerView rcv_cart_payment;
    CartPaymentAdapter cartPaymentAdapter;
    SharedPreferences preferences;
    String accountId;
    List<CartItem> cartPaymentItemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
        setContentView(R.layout.activity_payment);

        preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        accountId = preferences.getString("accountID", null);

        initWidget();
        if (!accountId.isEmpty()) {
            loadAccountInfo(accountId);
        }


        ArrayList<String> selectedSupplies = getIntent().getStringArrayListExtra("selected_supplies");
        if (selectedSupplies != null) {
            loadSelectedItems(selectedSupplies);
        }


    }


    private void initWidget() {
        img_back_payment = findViewById(R.id.img_back_payment);
        img_back_payment.setOnClickListener(view -> finish());
        tv_buyer_name = findViewById(R.id.tv_buyer_name);
        tv_buyer_phone = findViewById(R.id.tv_buyer_phone);
        tv_address_detail = findViewById(R.id.tv_address_detail);
        tv_ward_district_province = findViewById(R.id.tv_ward_district_province);
        rcv_cart_payment = findViewById(R.id.rcv_cart_payment);
        rcv_cart_payment.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        cartPaymentAdapter = new CartPaymentAdapter(this, cartPaymentItemList);
        rcv_cart_payment.setAdapter(cartPaymentAdapter);
//        tv_total_amount = findViewById(R.id.tv_total_amount_price);



//        btn_buy.setOnClickListener(view -> {
//
//        });
    }

    private void loadSelectedItems(ArrayList<String> supplies) {
        for (String supply : supplies) {
            // Truy vấn từ Firebase dựa trên supply_id để lấy thông tin item
            DatabaseReference itemRef = FirebaseDatabase.getInstance().getReference("Cart")
                    .child(accountId).child(supply);

            itemRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    CartItem cartItem = snapshot.getValue(CartItem.class);
                    if (cartItem != null) {
                        cartPaymentItemList.add(cartItem);
                        cartPaymentAdapter.notifyDataSetChanged(); // Cập nhật adapter sau khi thêm item
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("FirebaseError", "Error fetching data: " + error.getMessage());
                }
            });
        }
    }

    private void loadAccountInfo(String accountId) {
        DatabaseReference accountRef = FirebaseDatabase.getInstance().getReference("Accounts").child(accountId);
        DatabaseReference addressRef = FirebaseDatabase.getInstance().getReference("Account_Address").child(accountId);

        // Load Account information
        accountRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Model.Account account = snapshot.getValue(Model.Account.class);
                if (account != null) {
                    // Set fullname and phone
                    TextView tvBuyerName = findViewById(R.id.tv_buyer_name);
                    TextView tvBuyerPhone = findViewById(R.id.tv_buyer_phone);

                    tvBuyerName.setText(account.getFullname());
                    tvBuyerPhone.setText(account.getPhone());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Failed to load account data: " + error.getMessage());
            }
        });

        // Load Address information
        addressRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Account_Address accountAddress = snapshot.getValue(Account_Address.class);
                if (accountAddress != null) {
                    // Set address details, ward, district, province
                    TextView tvAddressDetail = findViewById(R.id.tv_address_detail);
                    TextView tvWardDistrictProvince = findViewById(R.id.tv_ward_district_province);

                    tvAddressDetail.setText(accountAddress.getAddress_details());
                    String fullAddress = accountAddress.getWard() + ", " + accountAddress.getDistrict() + ", " + accountAddress.getProvince();
                    tvWardDistrictProvince.setText(fullAddress);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Failed to load address data: " + error.getMessage());
            }
        });
    }

}