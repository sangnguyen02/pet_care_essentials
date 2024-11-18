package com.example.uiux.Activities.User;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
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

import com.example.uiux.Activities.User.Order.OrderActivity;
import com.example.uiux.Activities.User.Order.OrderPaymentActivity;
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
import java.util.List;
import java.util.Locale;

import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class PaymentActivity extends AppCompatActivity {
    MaterialCardView mcv_payment_address;
    MaterialButton btn_order;
    ImageView img_back_payment;
    TextView tv_buyer_name, tv_buyer_phone, tv_address_detail, tv_ward_district_province, tv_total_payment;
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
        orderRef = FirebaseDatabase.getInstance().getReference("Order").child(accountId);


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
        updateTotalPayment(totalPayment);
        deliveryMethodAdapter.setOnDeliveryMethodSelectedListener(cost -> {
            double totalWithDeliveryCost = totalPayment + cost;
            updateTotalPayment(totalWithDeliveryCost);
        });




        // Order
        btn_order.setOnClickListener(view -> {
            DeliveryMethod selectedDeliveryMethod = deliveryMethodAdapter.getSelectedPaymentMethod();
            if (selectedDeliveryMethod == null) {
                Toast.makeText(PaymentActivity.this, "Please select a delivery method.", Toast.LENGTH_SHORT).show();
                return;
            }

            PaymentMethod selectedPaymentMethod = paymentMethodAdapter.getSelectedPaymentMethod();
            if (selectedPaymentMethod == null) {
                Toast.makeText(PaymentActivity.this, "Please select a payment method.", Toast.LENGTH_SHORT).show();
                return;
            }
            order_create();
        });


    }

    private void order_create() {
        DeliveryMethod selectedDeliveryMethod = deliveryMethodAdapter.getSelectedPaymentMethod();
        if (selectedDeliveryMethod == null) {
            Toast.makeText(PaymentActivity.this, "Please select delivery method.", Toast.LENGTH_SHORT).show();
            return;
        }

        String expectedDeliveryDate = setExpected_delivery_date(
                new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()),
                selectedDeliveryMethod.getId()
        );

        String orderId = orderRef.push().getKey();
        List<String> supply_combined_key = setSupply_combined_key();
        if (orderId == null) {
            Toast.makeText(getApplicationContext(), "Failed to generate order ID.", Toast.LENGTH_SHORT).show();
            return;
        }
        Date dateOrder = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String formattedDate = dateFormat.format(dateOrder);

        Order order = new Order();
        order.setOrder_id(orderId);
        order.setCart_items_ordered(cartPaymentItemList);
        order.setDate_order(formattedDate);
        order.setExpected_delivery_date(expectedDeliveryDate);
        order.setDelivery_date("");
        order.setTotal_price(totalPayment);
        order.setName_customer(tv_buyer_name.getText().toString());
        order.setPhone_number(tv_buyer_phone.getText().toString());
        order.setAccount_id(accountId);
        order.setIs_completed(0);
        order.setAddress(tv_address_detail.getText().toString() + ", " + tv_ward_district_province.getText().toString());
        order.setStatus(OrderStatus.PENDING);
        orderRef.child(orderId).setValue(order).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Dữ liệu đã được lưu thành công
                    Toast.makeText(getApplicationContext(), "Order placed successfully.", Toast.LENGTH_SHORT).show();
                } else {
                    // Lỗi khi lưu dữ liệu
                    Toast.makeText(getApplicationContext(), "Failed to place order.", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        })

        ;

        Intent intent1 = new Intent(PaymentActivity.this, OrderActivity.class);
        intent1.putExtra("result","1");
        startActivity(intent1);
    }

    private void order() {
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ZaloPaySDK.init(553, Environment.SANDBOX);
        CreateOrder orderApi = new CreateOrder();
        try {
            JSONObject data = orderApi.createOrder(tv_total_payment.getText().toString());
            //lblZpTransToken.setVisibility(View.VISIBLE);
            String code = data.getString("returncode");
            Toast.makeText(getApplicationContext(), "return_code: " + code, Toast.LENGTH_LONG).show();

            if (code.equals("-5")) {
                // lblZpTransToken.setText("zptranstoken");
                // txtToken.setText(data.getString("zptranstoken"));
                String token = data.getString("zptranstoken");
                ZaloPaySDK.getInstance().payOrder(PaymentActivity.this, token, "demozpdk://app", new PayOrderListener()
                {

                    @Override
                    public void onPaymentSucceeded(String s, String s1, String s2) {



                    }

                    @Override
                    public void onPaymentCanceled(String s, String s1) {
                        Toast.makeText(getApplicationContext(), "Payment failed." + code, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {
                        Toast.makeText(getApplicationContext(), "Payment errored." + code, Toast.LENGTH_SHORT).show();
                    }
                });
                //IsDone();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private List<String> setSupply_combined_key() {
        List<String> supplyIds = new ArrayList<>();
        for (CartItem cartItem : cartPaymentItemList) {
            supplyIds.add(cartItem.getCombinedKey()); // Giả định `CartItem` có phương thức `getSupplyId()`
        }
        return supplyIds;
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

}