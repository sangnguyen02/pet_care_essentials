package com.example.uiux.Activities.User;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
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

import com.example.uiux.Activities.User.DisplayVoucher.DisplayVoucherActivity;
import com.example.uiux.Activities.User.Order.OrderPaymentActivity;
import com.example.uiux.Activities.User.Order.OrderActivity;
// import com.example.uiux.Activities.User.Order.OrderPaymentActivity;
import com.example.uiux.Activities.User.Order.PaymentNotificationActivity;
import com.example.uiux.Activities.User.Profile.UpdateAddressActivity;
import com.example.uiux.Adapters.CartAdapter;
import com.example.uiux.Adapters.CartPaymentAdapter;
import com.example.uiux.Adapters.DeliveryMethodAdapter;
import com.example.uiux.Adapters.PaymentMethodAdapter;
import com.example.uiux.Model.Account_Address;
import com.example.uiux.Model.CartItem;
import com.example.uiux.Model.DeliveryMethod;
import com.example.uiux.Model.Order;
import com.example.uiux.Model.PaymentMethod;
import com.example.uiux.Model.Supplies;
import com.example.uiux.Model.Voucher;
import com.example.uiux.R;
import com.example.uiux.Utils.CurrencyFormatter;
import com.example.uiux.Utils.OrderStatus;
import com.example.uiux.ZaloPay.Api.CreateOrder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class PaymentActivity extends AppCompatActivity {
    MaterialCardView mcv_payment_address,mcv_voucher;
    MaterialButton btn_order;
    ImageView img_back_payment;
    TextView tv_buyer_name, tv_buyer_phone, tv_address_detail, tv_ward_district_province, tv_total_payment, tv_vouncher_selected, tv_vouncher_discount_amount;
    RecyclerView rcv_cart_payment, rcv_payment_method, rcv_delivery_method;
    CartPaymentAdapter cartPaymentAdapter;
    PaymentMethodAdapter paymentMethodAdapter;
    DeliveryMethodAdapter deliveryMethodAdapter;
    SharedPreferences preferences;
    DatabaseReference orderRef;
    String accountId;
    List<CartItem> cartPaymentItemList = new ArrayList<>();
    List<PaymentMethod> paymentMethodList = new ArrayList<>();
    List<DeliveryMethod> deliveryMethodList = new ArrayList<>();
    Double totalPayment = 0.0;
    double totalWithDeliveryCost=0.0;

    Double totalDiscountedPayment = 0.0;

    private  double standardCost=6000;
    private  double expressCost=15000;
    private  double expeditedCost=30000;
    private double  deliveryCost=0;
    List<String>categoryList= new ArrayList<>();
    private Voucher voucher;
    String categoryType = "not value";
    String voucherId=null;
    int payment_index;
    String voucherCode = "";

    ArrayList<String> selectedSupplies;


    private ActivityResultLauncher<Intent> addressLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        String selectedAddress = data.getStringExtra("selected_address");

                        Log.e("ID ",selectedAddress);

                        if(selectedAddress != null) {
                            loadAddressFromPayment(selectedAddress);
                        }

                    }
                    else
                    {
                        Log.e("CHeck null","NUll");
                    }
                }
            }
    );

private ActivityResultLauncher<Intent> voucherLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    // Sau khi nhận được voucher ID từ DisplayVoucherActivity, bạn cập nhật lại giá
                    SharedPreferences preferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                    String selectedVoucher = preferences.getString("selected_voucher", null);
                    voucherId=selectedVoucher;


                    if (selectedVoucher != null) {
                        Log.e("Selected Voucher ID", selectedVoucher);
                        LoadVoucher(selectedVoucher);  // Tải thông tin voucher nếu cần
                        UpdateTotalPrice(selectedVoucher);  // Cập nhật tổng giá khi có voucher
                    }
                } else {
                    Log.e("Voucher", "No voucher selected or null data");
                }
            } else {
                Log.e("Voucher", "Activity didn't return RESULT_OK");
            }
        }
);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
        setContentView(R.layout.activity_payment);
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        // ZaloPay SDK Init
        ZaloPaySDK.init(553, Environment.SANDBOX);

        preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        accountId = preferences.getString("accountID", null);



        initWidget();

        mcv_voucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent voucherIntent= new Intent(PaymentActivity.this, DisplayVoucherActivity.class);
                Log.e("Cate type",categoryType);
                voucherIntent.putExtra("category",categoryType);
                voucherLauncher.launch(voucherIntent);

//                }


            }
        });

        btn_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SendData();

            }
        });
        if (!accountId.isEmpty()) {
            loadAccountInfo(accountId);
        }


        selectedSupplies = getIntent().getStringArrayListExtra("selected_supplies");

        if (selectedSupplies != null) {
            loadSelectedItems(selectedSupplies);

        }
        Intent intent = getIntent();
        categoryType=intent.getStringExtra("categoryType");
        Log.e("Check TYpe Cate",categoryType);
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
        tv_vouncher_selected = findViewById(R.id.tv_vouncher_selected);
        tv_vouncher_discount_amount = findViewById(R.id.tv_vouncher_discount_amount);

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
        paymentMethodList.add(new PaymentMethod(0, getString(R.string.zalo_method), R.drawable.zalopay));
        paymentMethodList.add(new PaymentMethod(1,getString(R.string.cod_method), R.drawable.cod));
        paymentMethodList.add(new PaymentMethod(2,"Wallet",R.drawable.wallet));
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
    private void SendData() {
        orderRef = FirebaseDatabase.getInstance().getReference("Order");


        Intent gotoOrderPayment= new Intent(PaymentActivity.this, OrderPaymentActivity.class);


        DeliveryMethod selectedDeliveryMethod = deliveryMethodAdapter.getSelectedPaymentMethod();
        if (selectedDeliveryMethod == null) {
            Toast.makeText(PaymentActivity.this, "Please select delivery method.", Toast.LENGTH_SHORT).show();
            return;
        }

        String expectedDeliveryDate = setExpected_delivery_date(
                new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()),
                selectedDeliveryMethod.getId()
        );
        gotoOrderPayment.putExtra("expectedDeliveryDate",expectedDeliveryDate);

        String orderId = orderRef.push().getKey();
        if (orderId == null) {
            Toast.makeText(getApplicationContext(), "Failed to generate order ID.", Toast.LENGTH_SHORT).show();
            return;
        }
        else
        {
            gotoOrderPayment.putExtra("orderId",orderId);
            Log.e("ID",orderId);
        }
        Date dateOrder = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String formattedDate = dateFormat.format(dateOrder);

        gotoOrderPayment.putExtra("formattedDate",formattedDate);
        gotoOrderPayment.putExtra("voucherId",voucherId);
       // order.setCart_items_ordered(cartPaymentItemList);
        gotoOrderPayment.putExtra("totalPrice",totalDiscountedPayment);
        gotoOrderPayment.putExtra("name",tv_buyer_name.getText().toString());
        gotoOrderPayment.putExtra("phone",tv_buyer_phone.getText().toString());
        gotoOrderPayment.putExtra("accountId",accountId);
        gotoOrderPayment.putExtra("quantity", String.valueOf(cartPaymentItemList.size()));
        payment_index=paymentMethodAdapter.getSelectedPaymentMethod().getId();
        Log.e("Tusf", String.valueOf(payment_index));
        gotoOrderPayment.putExtra("payment_index",payment_index);


        Order order=new Order();
        order.setOrder_id(orderId);
        order.setCart_items_ordered(cartPaymentItemList);

        orderRef.child(orderId).setValue(order).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("Order", "Order successfully created in Firebase.");
                } else {
                    Log.e("Order", "Failed to create order: " + task.getException().getMessage());
                }

            }
        });


        gotoOrderPayment.putExtra("address",tv_address_detail.getText().toString() + ", " + tv_ward_district_province.getText().toString());


//        selectedSupplies = getIntent().getStringArrayListExtra("selected_supplies");
        if (selectedSupplies != null) {
            gotoOrderPayment.putStringArrayListExtra("selected_supplies", selectedSupplies);
        }

        startActivity(gotoOrderPayment);

    }
    private String setExpected_delivery_date(String orderDate, int deliveryMethodId) {
        // Dựa trên deliveryMethodId, xác định số ngày giao hàng
        int additionalDays = 0;
        switch (deliveryMethodId) {
            case 1: // Standard
                additionalDays = 7;
                break;
            case 2: // Express
                additionalDays = 4;
                break;
            case 3: // Expedited
                additionalDays = 1;
                break;
        }

        // Parse orderDate thành đối tượng Date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date orderDateParsed = dateFormat.parse(orderDate);
            if (orderDateParsed != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(orderDateParsed);
                calendar.add(Calendar.DAY_OF_YEAR, additionalDays);
                return dateFormat.format(calendar.getTime());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orderDate; // Trả lại ngày đặt hàng nếu không parse được
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
        totalDiscountedPayment=total;
        tv_total_payment.setText(currencyFormattedTotal);
    }
//    private void loadAccountInfo(String accountId) {
//        DatabaseReference accountRef = FirebaseDatabase.getInstance().getReference("Account").child(accountId);
//        DatabaseReference addressRef = FirebaseDatabase.getInstance().getReference("Account_Address");
//
//        // Load Account information
//        accountRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Model.Account account = snapshot.getValue(Model.Account.class);
//                if (account != null) {
//                    // Set fullname and phone
//                    TextView tvBuyerName = findViewById(R.id.tv_buyer_name);
//                    TextView tvBuyerPhone = findViewById(R.id.tv_buyer_phone);
//
//                    tvBuyerName.setText(account.getFullname());
//                    tvBuyerPhone.setText(account.getPhone());
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e("FirebaseError", "Failed to load account data: " + error.getMessage());
//            }
//        });
//
//        // Load Address information
//        addressRef.orderByChild("account_id").equalTo(accountId).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Account_Address accountAddress = snapshot.getValue(Account_Address.class);
//                if (accountAddress != null) {
//                    // Set address details, ward, district, province
//                    TextView tvAddressDetail = findViewById(R.id.tv_address_detail);
//                    TextView tvWardDistrictProvince = findViewById(R.id.tv_ward_district_province);
//
//                    tvAddressDetail.setText(accountAddress.getAddress_details());
//                    String fullAddress = accountAddress.getWard() + ", " + accountAddress.getDistrict() + ", " + accountAddress.getProvince();
//                    tvWardDistrictProvince.setText(fullAddress);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e("FirebaseError", "Failed to load address data: " + error.getMessage());
//            }
//        });
//    }
private void loadAccountInfo(String accountId) {
    DatabaseReference accountRef = FirebaseDatabase.getInstance().getReference("Account").child(accountId);
    DatabaseReference addressRef = FirebaseDatabase.getInstance().getReference("Account_Address");

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
    addressRef.orderByChild("account_id").equalTo(accountId).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            Account_Address defaultAddress = null;
            Account_Address firstAddress = null;

            for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                Account_Address accountAddress = childSnapshot.getValue(Account_Address.class);

                if (accountAddress != null) {
                    if (firstAddress == null) {
                        firstAddress = accountAddress; // Save the first address found
                    }

                    if (accountAddress.getIs_default()) {
                        defaultAddress = accountAddress;
                        break; // Exit loop once default address is found
                    }
                }
            }

            // Use the default address if found, otherwise use the first address
            Account_Address selectedAddress = (defaultAddress != null) ? defaultAddress : firstAddress;

            if (selectedAddress != null) {
                // Set address details, ward, district, province
                TextView tvAddressDetail = findViewById(R.id.tv_address_detail);
                TextView tvWardDistrictProvince = findViewById(R.id.tv_ward_district_province);

                tvAddressDetail.setText(selectedAddress.getAddress_details());
                String fullAddress = selectedAddress.getWard() + ", " + selectedAddress.getDistrict() + ", " + selectedAddress.getProvince();
                tvWardDistrictProvince.setText(fullAddress);
            } else {
                Log.e("AddressInfo", "No address found for accountId: " + accountId);
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
                     voucherCode = voucher.getCode().toString();
                     tv_vouncher_selected.setText(voucherCode);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

    }
    private void UpdateTotalPrice(String selectedVoucher )
    {
        DatabaseReference voucherRef=FirebaseDatabase.getInstance().getReference("Voucher");
        voucherRef.child(selectedVoucher).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    Voucher voucher=snapshot.getValue(Voucher.class);
                    if(voucher!=null)
                    {
                        // Assuming that the voucher is a discount amount
                        double discountPercent  = voucher.getDiscount_percent();
                        double discountAmount;
                        double totalWithDiscount;
                        if("Ship".equals(voucher.getCategory()))
                        {
                            discountAmount=deliveryCost*(discountPercent/100);
                            totalWithDiscount=totalWithDeliveryCost-discountAmount;
                            tv_vouncher_discount_amount.setText("Saved " + CurrencyFormatter.formatCurrency(discountAmount, getString(R.string.currency_vn)));

                        }
                        else
                        {
                            // Calculate the discount amount
                            discountAmount = totalPayment * (discountPercent / 100.0);
                            tv_vouncher_discount_amount.setText("Saved" + CurrencyFormatter.formatCurrency(discountAmount, getString(R.string.currency_vn)));
                            // Apply discount to total payment
                            totalWithDiscount = totalPayment - discountAmount+deliveryCost;

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
                else {
                    // If no voucher is found, just show a message or keep the original total
                    Toast.makeText(PaymentActivity.this, "Voucher not found or expired.", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Error fetching voucher data: " + error.getMessage());

            }
        });

    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }



}