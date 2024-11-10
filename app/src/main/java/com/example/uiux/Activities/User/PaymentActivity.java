package com.example.uiux.Activities.User;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiux.Activities.User.Profile.UpdateAddressActivity;
import com.example.uiux.Adapters.CartAdapter;
import com.example.uiux.Adapters.CartPaymentAdapter;
import com.example.uiux.Adapters.DeliveryMethodAdapter;
import com.example.uiux.Adapters.PaymentMethodAdapter;
import com.example.uiux.Model.Account_Address;
import com.example.uiux.Model.CartItem;
import com.example.uiux.Model.DeliveryMethod;
import com.example.uiux.Model.PaymentMethod;
import com.example.uiux.R;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PaymentActivity extends AppCompatActivity {
    MaterialCardView mcv_payment_address;
    ImageView img_back_payment;
    TextView tv_buyer_name, tv_buyer_phone, tv_address_detail, tv_ward_district_province;
    RecyclerView rcv_cart_payment, rcv_payment_method, rcv_delivery_method;
    CartPaymentAdapter cartPaymentAdapter;
    PaymentMethodAdapter paymentMethodAdapter;
    DeliveryMethodAdapter deliveryMethodAdapter;
    SharedPreferences preferences;
    String accountId;
    List<CartItem> cartPaymentItemList = new ArrayList<>();
    List<PaymentMethod> paymentMethodList = new ArrayList<>();
    List<DeliveryMethod> deliveryMethodList = new ArrayList<>();

    private ActivityResultLauncher<Intent> addressLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        String selectedAddress = data.getStringExtra("selected_address");
                        if(selectedAddress != null) {
                            loadAddressFromPayment(selectedAddress);
                        }

                    }
                }
            }
    );
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

        mcv_payment_address = findViewById(R.id.mcv_payment_address);
        mcv_payment_address.setOnClickListener(view -> {
            Intent gotoAddress = new Intent(PaymentActivity.this, UpdateAddressActivity.class);
            gotoAddress.putExtra("account_id",accountId);
            gotoAddress.putExtra("from_payment_activity", true);
            addressLauncher.launch(gotoAddress);
        });

        // Payment method
        rcv_payment_method = findViewById(R.id.rcv_payment_method);
        rcv_payment_method.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        paymentMethodList.add(new PaymentMethod(1, getString(R.string.zalo_method), R.drawable.zalopay));
        paymentMethodList.add(new PaymentMethod(2,getString(R.string.cod_method), R.drawable.cod));
        paymentMethodAdapter = new PaymentMethodAdapter(this, paymentMethodList);
        rcv_payment_method.setAdapter(paymentMethodAdapter);

        // Delivery method
        rcv_delivery_method = findViewById(R.id.rcv_delivery_method);
        rcv_delivery_method.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        deliveryMethodList.add(new DeliveryMethod(1, getString(R.string.shipping_standard), 6000));
        deliveryMethodList.add(new DeliveryMethod(2, getString(R.string.shipping_express), 15000));
        deliveryMethodList.add(new DeliveryMethod(3, getString(R.string.shipping_expedited), 30000));
        deliveryMethodAdapter = new DeliveryMethodAdapter(this, deliveryMethodList);
        rcv_delivery_method.setAdapter(deliveryMethodAdapter);


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

    private void loadAddressFromPayment(String accountAddressId) {
        DatabaseReference addressRef = FirebaseDatabase.getInstance().getReference("Account_Address").child(accountAddressId);

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