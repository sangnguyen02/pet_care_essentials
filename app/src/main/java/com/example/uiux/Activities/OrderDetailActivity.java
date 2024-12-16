package com.example.uiux.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiux.Activities.User.CancelOrder.UserCancelOrderActivity;
import com.example.uiux.Activities.User.Order.EditOrderActivity;
import com.example.uiux.Activities.User.ReturnOrder.UserReturnOrderActivity;
import com.example.uiux.Adapters.CartPaymentAdapter;
import com.example.uiux.Adapters.DeliveryMethodAdapter;
import com.example.uiux.Adapters.OrderChildAdapter;
import com.example.uiux.Adapters.PaymentMethodAdapter;
import com.example.uiux.Model.CartItem;
import com.example.uiux.Model.DeliveryMethod;
import com.example.uiux.Model.Notification;
import com.example.uiux.Model.Order;
import com.example.uiux.Model.PaymentMethod;
import com.example.uiux.Model.Voucher;
import com.example.uiux.R;
import com.example.uiux.Utils.CurrencyFormatter;
import com.example.uiux.Utils.FCM.FcmNotificationSender;
import com.example.uiux.Utils.OrderStatus;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OrderDetailActivity extends AppCompatActivity {

    MaterialCardView mcv_order_update, mcv_tracking_bar;
    Spinner spinner_order_status;
    MaterialButton btn_order_save, btn_return_order, btn_cancel_order;
    ImageView img_back_order_detail;
    TextView tv_order_buyer_name, tv_order_buyer_phone, tv_order_address_detail, tv_order_ward_district_province, tv_order_total_amount, tv_order_detail_date, tv_tracking_bar;
    RecyclerView rcv_order_payment;
    CartPaymentAdapter cartPaymentAdapter;
    SharedPreferences preferences;
    DatabaseReference orderRef;
    String accountId, order_id;
    int accountType;
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
    Order order;
    View viewPending, viewPreparing, viewDelivering, viewCompleted;
    View linePendPrepare, linePrepareDeliver, lineDeliverComplete;
    TextView tvStatusPending, tvStatusPreparing, tvStatusDelivering, tvStatusCompleted, tv_order_detail_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
        setContentView(R.layout.activity_order_detail);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
        preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        accountId = preferences.getString("accountID", null);
        accountType = preferences.getInt("accountType", -1);
        order_id = getIntent().getStringExtra("order_id");
        orderRef = FirebaseDatabase.getInstance().getReference("Order").child(order_id);
        initWidget();
        Log.e("account type", String.valueOf(accountType));
        if (accountType == -1) {
            mcv_order_update.setVisibility(View.VISIBLE);
        }
//        mcv_order_update.setVisibility(View.VISIBLE);

        loadOrder();


    }

    @Override
    protected void onStart() {
        super.onStart();
        accountType = preferences.getInt("accountType", -1);
        if (accountType == -1) {
            mcv_order_update.setVisibility(View.VISIBLE);
        }
        loadOrder();
    }

    void initWidget() {
        spinner_order_status = findViewById(R.id.spinner_order_status);
        tv_order_detail_id = findViewById(R.id.tv_order_detail_id);
        viewPending = findViewById(R.id.view_pending);
        viewPreparing = findViewById(R.id.view_preparing);
        viewDelivering = findViewById(R.id.view_delivering);
        viewCompleted = findViewById(R.id.view_completed);
        linePendPrepare = findViewById(R.id.line_pend_prepare);
        linePrepareDeliver = findViewById(R.id.line_prepare_deliver);
        lineDeliverComplete = findViewById(R.id.line_deliver_complete);
        tvStatusPending = findViewById(R.id.tv_status_pending);
        tvStatusPreparing = findViewById(R.id.tv_status_preparing);
        tvStatusDelivering = findViewById(R.id.tv_status_delivering);
        tvStatusCompleted = findViewById(R.id.tv_status_completed);
        mcv_tracking_bar = findViewById(R.id.mcv_tracking_bar);
        tv_tracking_bar = findViewById(R.id.tv_tracking_bar);

        img_back_order_detail = findViewById(R.id.img_back_order_detail);
        img_back_order_detail.setOnClickListener(view -> finish());

        mcv_order_update = findViewById(R.id.mcv_order_update);
        btn_order_save = findViewById(R.id.btn_order_save);

        tv_order_buyer_name = findViewById(R.id.tv_order_buyer_name);
        tv_order_buyer_phone = findViewById(R.id.tv_order_buyer_phone);
        tv_order_address_detail = findViewById(R.id.tv_order_address_detail);
        tv_order_ward_district_province = findViewById(R.id.tv_order_ward_district_province);
        tv_order_total_amount = findViewById(R.id.tv_order_total_amount);
        tv_order_detail_date = findViewById(R.id.tv_order_detail_date);
        rcv_order_payment = findViewById(R.id.rcv_order_payment);
        rcv_order_payment.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        cartPaymentAdapter = new CartPaymentAdapter(this, cartPaymentItemList);
        rcv_order_payment.setAdapter(cartPaymentAdapter);
        FetchSpinnerStatus();


        btn_order_save.setOnClickListener(view -> {

            if(spinner_order_status.getSelectedItemPosition()==3)
            {
                // order.setIs_completed(1);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String currentDateAndTime = sdf.format(new Date());

                // Cập nhật delivery_date
                order.setDelivery_date(currentDateAndTime);
            }
            updateStatusUI(spinner_order_status.getSelectedItemPosition());
            order.setStatus(spinner_order_status.getSelectedItemPosition());


            // Nếu không chọn làm mặc định thì chỉ cập nhật địa chỉ hiện tại
            orderRef.setValue(order).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(OrderDetailActivity.this, "Order updated.", Toast.LENGTH_SHORT).show();
                    DatabaseReference userFCMTokenRef = FirebaseDatabase.getInstance()
                            .getReference("Account")
                            .child(order.getAccount_id())
                            .child("fcm_token");
                    userFCMTokenRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String userToken = snapshot.getValue(String.class);
                                String title = "Order " + order.getOrder_id();
                                String body = OrderStatus.getStatusMessage(order.getStatus());
                                FcmNotificationSender fcmNotificationSender = new FcmNotificationSender(
                                        userToken,
                                        title,
                                        body,
                                        order.getOrder_id(),
                                        OrderDetailActivity.this);
                                fcmNotificationSender.sendNotification();

//                                Log.e("User FCM TOken", userToken);

                                createNotification(order.getAccount_id(), body);
                                finish();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(OrderDetailActivity.this, "Failed to fetch user token: " + error.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
//                    finish();
                } else {
                    Toast.makeText(OrderDetailActivity.this, "Failed to edit Order ", Toast.LENGTH_SHORT).show();
                }
            });
        });



        btn_return_order = findViewById(R.id.btn_return_order);
        btn_return_order.setOnClickListener(view -> {
            Intent intent = new Intent(OrderDetailActivity.this, UserReturnOrderActivity.class);
            intent.putExtra("order_id", order_id); // Truyền order_id vào Intent
            Log.e("Order_ID", order_id);  // Ghi log để kiểm tra order_id
            startActivity(intent);
            finish();

        });

        btn_cancel_order = findViewById(R.id.btn_cancel_order);
        btn_cancel_order.setOnClickListener(view -> {
            Intent intent = new Intent(OrderDetailActivity.this, UserCancelOrderActivity.class);
            intent.putExtra("order_id", order_id); // Truyền order_id vào Intent
            Log.e("Order_ID", order_id);  // Ghi log để kiểm tra order_id
            startActivity(intent);
            finish();

        });

    }

    void loadOrder() {
        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    order = snapshot.getValue(Order.class);
                    if (order != null) {
                        // Cập nhật thông tin UI
                        tv_order_detail_id.setText("Order #" + order.getOrder_id());
                        tv_order_buyer_name.setText(order.getName_customer() != null ? order.getName_customer() : "N/A");
                        tv_order_buyer_phone.setText(order.getPhone_number() != null ? order.getPhone_number() : "N/A");
                        tv_order_address_detail.setText(order.getAddress() != null ? order.getAddress() : "N/A");
                        tv_order_total_amount.setText(CurrencyFormatter.formatCurrency(order.getTotal_price(), getString(R.string.currency_vn)));
                        tv_order_detail_date.setText(order.getDate_order() != null ? order.getDate_order() : "N/A");

                        cartPaymentItemList.clear();
                        if (order.getCart_items_ordered() != null) {
                            cartPaymentItemList.addAll(order.getCart_items_ordered());
                        }
                        cartPaymentAdapter.notifyDataSetChanged();

                        spinner_order_status.setSelection(order.getStatus());

                        updateStatusUI(order.getStatus());

                        if(order.getStatus() == OrderStatus.RETURNED) {
                            spinner_order_status.setEnabled(false);
                            btn_order_save.setVisibility(View.GONE);
                            tv_tracking_bar.setVisibility(View.GONE);
                            mcv_tracking_bar.setVisibility(View.GONE);
                        }

                        if(order.getStatus() == OrderStatus.DELIVERED) {
                            if(accountType == 0 && (order.getStatus() == OrderStatus.DELIVERED)) {
                                btn_return_order.setVisibility(View.VISIBLE);
                            }
                        }
//                        if(order.getStatus() == OrderStatus.PENDING || order.getStatus() == OrderStatus.PREPARING || order.getStatus() == OrderStatus.SHIPPING) {
//                            if(accountType == 0) {
//                                btn_cancel_order.setVisibility(View.VISIBLE);
//                            }
//                        }

                        if (accountType == 0 && (order.getStatus() == OrderStatus.PENDING ||
                                order.getStatus() == OrderStatus.PREPARING ||
                                order.getStatus() == OrderStatus.SHIPPING)) {
                            btn_cancel_order.setVisibility(View.VISIBLE);
                        }
                        if (order.getStatus() == OrderStatus.CANCELED) {
                            tv_tracking_bar.setVisibility(View.GONE);
                            mcv_tracking_bar.setVisibility(View.GONE);
                        }


                    }
                } else {
                    Toast.makeText(OrderDetailActivity.this, "Order not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OrderDetailActivity.this, "Failed to load order: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void FetchSpinnerStatus() {
        List<String> statusList = new ArrayList<>();
        statusList.add(OrderStatus.getStatusName(OrderStatus.PENDING));
        statusList.add(OrderStatus.getStatusName(OrderStatus.PREPARING));
        statusList.add(OrderStatus.getStatusName(OrderStatus.SHIPPING));
        statusList.add(OrderStatus.getStatusName(OrderStatus.DELIVERED));
        statusList.add(OrderStatus.getStatusName(OrderStatus.CANCELED));
        statusList.add(OrderStatus.getStatusName(OrderStatus.RETURNED));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                OrderDetailActivity.this,
                android.R.layout.simple_spinner_item,
                statusList
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_order_status.setAdapter(adapter);

        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Order order = snapshot.getValue(Order.class);
                    if (order != null) {
                        int currentStatus = order.getStatus();
                        if (currentStatus >= 0 && currentStatus < statusList.size()) {
                            spinner_order_status.setSelection(currentStatus);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OrderDetailActivity.this, "Failed to load status: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

//    private void FetchSpinnerStatus() {
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(OrderDetailActivity.this,
//                R.array.order_status, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner_order_status.setAdapter(adapter);
//    }

    private void updateStatusUI(int status) {
        // Màu hoàn thành
        int completedColor = ContextCompat.getColor(this, R.color.gradient);
        // Màu chưa hoàn thành
        int incompleteColor = ContextCompat.getColor(this, R.color.grey);

        // Đặt màu trạng thái
        viewPending.setBackgroundResource(status >= 0 ? R.drawable.shape_status_complete : R.drawable.shape_status_incomplete);
        tvStatusPending.setTextColor(status >= 0 ? completedColor : incompleteColor);

        linePendPrepare.setBackgroundColor(status >= 1 ? completedColor : incompleteColor);
        viewPreparing.setBackgroundResource(status >= 1 ? R.drawable.shape_status_complete : R.drawable.shape_status_incomplete);
        tvStatusPreparing.setTextColor(status >= 1 ? completedColor : incompleteColor);

        linePrepareDeliver.setBackgroundColor(status >= 2 ? completedColor : incompleteColor);
        viewDelivering.setBackgroundResource(status >= 2 ? R.drawable.shape_status_complete : R.drawable.shape_status_incomplete);
        tvStatusDelivering.setTextColor(status >= 2 ? completedColor : incompleteColor);

        lineDeliverComplete.setBackgroundColor(status >= 3 ? completedColor : incompleteColor);
        viewCompleted.setBackgroundResource(status >= 3 ? R.drawable.shape_status_complete : R.drawable.shape_status_incomplete);
        tvStatusCompleted.setTextColor(status >= 3 ? completedColor : incompleteColor);
    }

    private void createNotification(String accountId, String content) {
        DatabaseReference notifyRef = FirebaseDatabase.getInstance().getReference("Notification");
        String notificationId = notifyRef.push().getKey();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentTime = sdf.format(new Date());

        Notification notification = new Notification(
                notificationId,
                accountId,
                false,
                content,
                0,
                1,
                currentTime
        );

        if (notificationId != null) {
            notifyRef.child(notificationId).setValue(notification).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.e("Notification","Notification created successfully.");
                } else {
                    Log.e("Notification","Failed to create notification.");
                }
            });
        }
    }

}