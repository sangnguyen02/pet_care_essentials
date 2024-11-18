package com.example.uiux.Activities.User;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import com.example.uiux.Activities.User.DisplayVoucher.DisplayVoucherActivity;
import com.example.uiux.Activities.User.Order.OrderPaymentActivity;
import com.example.uiux.Activities.User.Profile.UpdateAddressActivity;
import com.example.uiux.Adapters.CartAdapter;
import com.example.uiux.Adapters.CartPaymentAdapter;
import com.example.uiux.Adapters.DeliveryMethodAdapter;
import com.example.uiux.Adapters.PaymentMethodAdapter;
import com.example.uiux.Model.Account_Address;
import com.example.uiux.Model.CartItem;
import com.example.uiux.Model.DeliveryMethod;
import com.example.uiux.Model.PaymentMethod;
import com.example.uiux.Model.Supplies;
import com.example.uiux.Model.Voucher;
import com.example.uiux.R;
import com.example.uiux.Utils.CurrencyFormatter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

public class PaymentActivity extends AppCompatActivity {
    MaterialCardView mcv_payment_address,mcv_voucher;
    MaterialButton btn_order;
    ImageView img_back_payment;
    TextView tv_buyer_name, tv_buyer_phone, tv_address_detail, tv_ward_district_province, tv_total_payment;
    RecyclerView rcv_cart_payment, rcv_payment_method, rcv_delivery_method;
    CartPaymentAdapter cartPaymentAdapter;
    PaymentMethodAdapter paymentMethodAdapter;
    DeliveryMethodAdapter deliveryMethodAdapter;
    SharedPreferences preferences;
    String accountId;
    List<CartItem> cartPaymentItemList = new ArrayList<>();
    List<PaymentMethod> paymentMethodList = new ArrayList<>();
    List<DeliveryMethod> deliveryMethodList = new ArrayList<>();
    Double totalPayment = 0.0;
    double totalWithDeliveryCost=0.0;

    Double totalDiscountedPayment = 0.0;
    String selectedVoucher="";
    private  double standardCost=6000;
    private  double expressCost=15000;
    private  double expeditedCost=30000;
    private double  deliveryCost=0;
    List<String>categoryList= new ArrayList<>();
    private Voucher voucher;


    private ActivityResultLauncher<Intent> addressLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        String selectedAddress = data.getStringExtra("selected_address");
                        selectedVoucher = getIntent().getStringExtra("selected_voucher");
                        Log.e("SElected",selectedVoucher);
                        if(selectedAddress != null) {
                            loadAddressFromPayment(selectedAddress);
                        }

                    }
                }
            }
    );
//    private ActivityResultLauncher<Intent> voucherLauncher = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            result -> {
//                if (result.getResultCode() == Activity.RECEIVER_EXPORTED) {
//                    Intent data = result.getData();
//                    if (data != null) {
//                         selectedVoucher = getIntent().getStringExtra("selected_voucher");
//                        Log.e("SElected",selectedVoucher);
//                        if(selectedVoucher != null) {
//                            LoadVoucher(selectedVoucher);
//                            Log.e("SElected",selectedVoucher);
//                        }
//
//                    }
//                }
//            }
//    );
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
        setContentView(R.layout.activity_payment);

        preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        accountId = preferences.getString("accountID", null);


        initWidget();

        mcv_voucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent voucherIntent= new Intent(PaymentActivity.this, DisplayVoucherActivity.class);
//                voucherIntent.putExtra("category",category);
                addressLauncher.launch(voucherIntent);
                UpdateTotalPrice();

            }
        });

        btn_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gotoOrderPayment= new Intent(PaymentActivity.this, OrderPaymentActivity.class);
                gotoOrderPayment.putExtra("totalPrice",totalDiscountedPayment);
                startActivity(gotoOrderPayment);
            }
        });
        if (!accountId.isEmpty()) {
            loadAccountInfo(accountId);
        }


        ArrayList<String> selectedSupplies = getIntent().getStringArrayListExtra("selected_supplies");
        if (selectedSupplies != null) {
            loadSelectedItems(selectedSupplies);

        }
        for (int i=0;i<categoryList.size();i++)
        {
          Log.e("Check",categoryList.get(0))  ;
        }

    }


    private void initWidget() {
        img_back_payment = findViewById(R.id.img_back_payment);
        img_back_payment.setOnClickListener(view -> finish());
        btn_order = findViewById(R.id.btn_order);
        tv_buyer_name = findViewById(R.id.tv_buyer_name);
        tv_buyer_phone = findViewById(R.id.tv_buyer_phone);
        tv_address_detail = findViewById(R.id.tv_address_detail);
        tv_ward_district_province = findViewById(R.id.tv_ward_district_province);
        tv_total_payment = findViewById(R.id.tv_total_payment);
        rcv_cart_payment = findViewById(R.id.rcv_cart_payment);
        rcv_cart_payment.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        cartPaymentAdapter = new CartPaymentAdapter(this, cartPaymentItemList);
        rcv_cart_payment.setAdapter(cartPaymentAdapter);
        mcv_voucher=findViewById(R.id.mcv_voucher);

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
        deliveryMethodList.add(new DeliveryMethod(1, getString(R.string.shipping_standard), standardCost));
        deliveryMethodList.add(new DeliveryMethod(2, getString(R.string.shipping_express), expressCost));
        deliveryMethodList.add(new DeliveryMethod(3, getString(R.string.shipping_expedited), expeditedCost));
        deliveryMethodAdapter = new DeliveryMethodAdapter(this, deliveryMethodList);
        rcv_delivery_method.setAdapter(deliveryMethodAdapter);
        updateTotalPayment(totalPayment);
        deliveryMethodAdapter.setOnDeliveryMethodSelectedListener(cost -> {

             totalWithDeliveryCost =cost+ totalPayment ;
            deliveryCost=cost;
            updateTotalPayment(totalWithDeliveryCost);
        });

    }
    private void loadSelectedItems(ArrayList<String> supplies) {
        for (String supply : supplies) {
            // Truy vấn từ Firebase dựa trên supply_id để lấy thông tin item
            DatabaseReference itemRef = FirebaseDatabase.getInstance().getReference("Cart")
                    .child(accountId).child(supply);
            DatabaseReference suppRef = FirebaseDatabase.getInstance().getReference("Supplies")
                    .child(accountId).child(supply);
            itemRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    CartItem cartItem = snapshot.getValue(CartItem.class);
                    if (cartItem != null) {
                        cartPaymentItemList.add(cartItem);
                        cartPaymentAdapter.notifyDataSetChanged();
                        totalPayment += cartItem.getTotalPrice();
                        tv_total_payment.setText(CurrencyFormatter.formatCurrency(totalPayment, getString(R.string.currency_vn)));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("FirebaseError", "Error fetching data: " + error.getMessage());
                }
            });

            suppRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    Supplies cartItem = snapshot.getValue(Supplies.class);
                    if (cartItem != null) {
                        categoryList.add(cartItem.getCategory());
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void updateTotalPayment(double total) {
        // Cập nhật TextView tổng tiền
        String currencyFormattedTotal = CurrencyFormatter.formatCurrency(total, getString(R.string.currency_vn));
        tv_total_payment.setText(currencyFormattedTotal);
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
    private void LoadVoucher(String selectedVouchers)
    {

            DatabaseReference voucherRef = FirebaseDatabase.getInstance().getReference("Voucher").child(selectedVouchers);
            voucherRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                     voucher= snapshot.getValue(Voucher.class);
                     Log.e("Voucher",voucher.getCategory());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

    }

    private void UpdateTotalPrice()
    {
//        DatabaseReference voucherRef=FirebaseDatabase.getInstance().getReference("Voucher");
//        voucherRef.child(selectedVoucher).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.exists())
//                {
//                    Voucher voucher=snapshot.getValue(Voucher.class);
//
//                }
//                else {
//                    // If no voucher is found, just show a message or keep the original total
//                    Toast.makeText(PaymentActivity.this, "Voucher not found or expired.", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e("FirebaseError", "Error fetching voucher data: " + error.getMessage());
//
//            }
//        });
        if(voucher!=null)
        {
            // Assuming that the voucher is a discount amount
            double discountPercent  = voucher.getDiscount_percent();
            double discountAmount;
            double totalWithDiscount;
            Log.e("Check",voucher.getCategory());
            if(voucher.getCategory()=="Ship")
            {

                discountAmount=deliveryCost*(discountPercent/100);
                Log.e("Check", String.valueOf(discountAmount));
                totalWithDiscount=totalWithDeliveryCost-discountAmount;

            }
            else
            {
                // Calculate the discount amount
                discountAmount = totalPayment * (discountPercent / 100.0);
                // Apply discount to total payment
                totalWithDiscount = totalPayment - discountAmount;

            }
            totalDiscountedPayment=totalWithDiscount;
            Log.e("Check", String.valueOf(totalDiscountedPayment));
            updateTotalPayment(totalWithDiscount);
        }
        else
        {
            Log.e("NULL","NULL");
        }
    }



}