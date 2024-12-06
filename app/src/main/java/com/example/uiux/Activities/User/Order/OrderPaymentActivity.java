package com.example.uiux.Activities.User.Order;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uiux.Model.Order;
import com.example.uiux.Model.Voucher;
import com.example.uiux.R;
import com.example.uiux.ZaloPay.Api.CreateOrder;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;

import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class OrderPaymentActivity extends AppCompatActivity {
    TextView txtSoluong, txtTongTien,txtName,txtPhone,txtEmail,txtAddress,txtOrderDate,txtExpectedDeliveryDate;
    MaterialButton btnThanhToan,btnConfirm,btnWallet;
    ImageView img_back_order_confirm;
    String accountId;
    Double total;
    String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
        setContentView(R.layout.activity_order_payment);
        initWidget();

        ArrayList<String> selectedSupplies = getIntent().getStringArrayListExtra("selected_supplies");
        if (selectedSupplies != null) {
            // Xử lý dữ liệu selected_supplies tại đây nếu cần
            Log.d("OrderPaymentActivity", "Selected supplies: " + selectedSupplies.toString());
        }

        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        // ZaloPay SDK Init
        ZaloPaySDK.init(553, Environment.SANDBOX);

        Intent intent = getIntent();
         total = intent.getDoubleExtra("totalPrice", (double) 0);
        // Nhận dữ liệu từ Intent
        String expectedDeliveryDate = getIntent().getStringExtra("expectedDeliveryDate");
         orderId = getIntent().getStringExtra("orderId");
        String formattedDate = getIntent().getStringExtra("formattedDate");
        String voucherId = getIntent().getStringExtra("voucherId");
        String name = getIntent().getStringExtra("name");
        String phone = getIntent().getStringExtra("phone");
         accountId = getIntent().getStringExtra("accountId");
        String address = getIntent().getStringExtra("address");
        String quantity = getIntent().getStringExtra("quantity");
        int index = getIntent().getIntExtra("payment_index",0);
        Log.e("index", String.valueOf(index));


        String totalString = String.format("%.0f", total);
        txtTongTien.setText(Double.toString(total));
        txtName.setText(name);
        txtSoluong.setText(quantity);
        txtAddress.setText(address);
        txtExpectedDeliveryDate.setText(expectedDeliveryDate);
        txtOrderDate.setText(formattedDate);
        txtPhone.setText(phone);
        fetchEmailFromFirebase(accountId);
        if(index==1)
        {
            btnThanhToan.setVisibility(View.GONE);
            btnConfirm.setVisibility(View.VISIBLE);
            btnWallet.setVisibility(View.GONE);

        }else if(index==0)
        {
            btnThanhToan.setVisibility(View.VISIBLE);
            btnConfirm.setVisibility(View.GONE);
            btnWallet.setVisibility(View.GONE);
        }
        else
        {
            btnThanhToan.setVisibility(View.GONE);
            btnConfirm.setVisibility(View.GONE);
            btnWallet.setVisibility(View.VISIBLE);
        }
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (voucherId!=null)
                {
                    updateVoucherQuantity(voucherId);
                }
                updateOrder(orderId,0);

                if (selectedSupplies != null && !selectedSupplies.isEmpty()) {
                    for (String supply_combinedKey : selectedSupplies) {
                        DatabaseReference cartRef = FirebaseDatabase.getInstance()
                                .getReference("Cart")
                                .child(accountId)
                                .child(supply_combinedKey);
                        cartRef.removeValue().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("Firebase", "Deleted supply: " + supply_combinedKey);
                            } else {
                                Log.e("Firebase", "Failed to delete supply: " + supply_combinedKey);
                            }
                        });
                    }
                }

                Intent intent1= new Intent(OrderPaymentActivity.this,PaymentNotificationActivity.class);
                intent1.putExtra("result","Xac nhan thanh cong");
                startActivity(intent1);

            }
        });
        btnWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//
//                Intent intent1= new Intent(OrderPaymentActivity.this,PaymentNotificationActivity.class);
//                intent1.putExtra("result","Xac nhan thanh cong");
//                startActivity(intent1);
            }
        });



        btnThanhToan.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                CreateOrder orderApi = new CreateOrder();
                try {
                    JSONObject data = orderApi.createOrder(totalString);
                    //lblZpTransToken.setVisibility(View.VISIBLE);
                    String code = data.getString("returncode");
                    Toast.makeText(getApplicationContext(), "return_code: " + code, Toast.LENGTH_LONG).show();

                    if (code.equals("1")) {
                        // lblZpTransToken.setText("zptranstoken");
                        // txtToken.setText(data.getString("zptranstoken"));
                        String token = data.getString("zptranstoken");
                        ZaloPaySDK.getInstance().payOrder(OrderPaymentActivity.this, token, "demozpdk://app", new PayOrderListener()
                        {

                            @Override
                            public void onPaymentSucceeded(String s, String s1, String s2) {
                                Intent intent1= new Intent(OrderPaymentActivity.this,PaymentNotificationActivity.class);
                                intent1.putExtra("result","Thanh toan thanh cong");
                                if (voucherId!=null)
                                {
                                    updateVoucherQuantity(voucherId);
                                }
                                updateOrder(orderId,1);
                                startActivity(intent1);

                            }

                            @Override
                            public void onPaymentCanceled(String s, String s1) {
                                Intent intent1= new Intent(OrderPaymentActivity.this,PaymentNotificationActivity.class);
                                intent1.putExtra("result","Thanh toan bi cancel");
                                startActivity(intent1);

                            }

                            @Override
                            public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {
                                Intent intent1= new Intent(OrderPaymentActivity.this,PaymentNotificationActivity.class);
                                intent1.putExtra("result","Thanh toan bi loi");
                                startActivity(intent1);

                            }
                        });
                        //IsDone();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void initWidget() {
        img_back_order_confirm = findViewById(R.id.img_back_order_confirm);
        img_back_order_confirm.setOnClickListener(view -> finish());
        txtTongTien = findViewById(R.id.textViewTongTien);
        btnThanhToan = findViewById(R.id.buttonThanhToan);
        // Ánh xạ các TextView khác
        txtName = findViewById(R.id.textViewBuyerNameValue);
        txtPhone = findViewById(R.id.textViewPhoneValue);
        txtEmail = findViewById(R.id.textViewEmail);
        txtAddress = findViewById(R.id.textViewAddressValue);
        txtOrderDate = findViewById(R.id.textViewOrderDateValue);
        txtExpectedDeliveryDate = findViewById(R.id.textViewExpectedDeliveryDateValue);
        txtSoluong=findViewById(R.id.textViewQuantity);
        btnConfirm=findViewById(R.id.buttonConfirm);
        btnWallet=findViewById(R.id.buttonWallet);

    }
    private void updateOrder(String orderId,int index) {
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("Order").child(orderId);
        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Order order = snapshot.getValue(Order.class);
                    if (order != null) {
                        // Đảm bảo bạn đã thiết lập đúng tất cả các thuộc tính của order trước khi cập nhật
                        order.setDate_order(txtOrderDate.getText().toString());
                        order.setAddress(txtAddress.getText().toString());
                        order.setExpected_delivery_date(txtExpectedDeliveryDate.getText().toString());
                        order.setPhone_number(txtPhone.getText().toString());
                        order.setAccount_id(accountId);
                        order.setEmail(txtEmail.getText().toString());
                        order.setDelivery_date(""); // Điều này cần phải kiểm tra lại nếu có giá trị thực tế
                        order.setTotal_price(total);
                        order.setName_customer(txtName.getText().toString());
                        order.setIs_completed_payment(index); // Da thanh toan
                        order.setStatus(0); // Cập nhật trạng thái đúng nếu cần

                        // Cập nhật vào Firebase
                        orderRef.setValue(order)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(OrderPaymentActivity.this, "Order updated successfully", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(OrderPaymentActivity.this, "Failed to update order", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Log.e("UpdateOrder", "Order data not found.");
                        Toast.makeText(OrderPaymentActivity.this, "Order data error", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("UpdateOrder", "Order ID does not exist in Firebase.");
                    Toast.makeText(OrderPaymentActivity.this, "Order ID does not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OrderPaymentActivity.this, "Failed to fetch order: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateVoucherQuantity(String voucherId) {
        // Truy xuất đến cơ sở dữ liệu Firebase để lấy thông tin voucher theo voucherId
        DatabaseReference voucherRef = FirebaseDatabase.getInstance().getReference("Voucher").child(voucherId);

        // Lắng nghe sự thay đổi dữ liệu
        voucherRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Chuyển dữ liệu từ Firebase thành đối tượng Voucher
                    Voucher voucher = dataSnapshot.getValue(Voucher.class);

                    // Kiểm tra và cập nhật số lượng voucher nếu còn
                    if (voucher != null && voucher.getRemaining_quantity() > 0) {
                        // Giảm số lượng voucher đi 1
                        int newQuantity = voucher.getRemaining_quantity() - 1;
                        voucher.setRemaining_quantity(newQuantity);

                        // Cập nhật lại số lượng voucher trong Firebase
                        voucherRef.setValue(voucher);

                        // Thông báo cập nhật thành công
                        Toast.makeText(OrderPaymentActivity.this, "Voucher đã được giảm 1", Toast.LENGTH_SHORT).show();
                    } else {
                        // Nếu voucher đã hết
                        Toast.makeText(OrderPaymentActivity.this, "Voucher hết hạn sử dụng", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(OrderPaymentActivity.this, "Voucher không tồn tại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi khi đọc dữ liệu
                Toast.makeText(OrderPaymentActivity.this, "Failed to update voucher quantity: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void fetchEmailFromFirebase(String accountId) {
        if (accountId == null || accountId.isEmpty()) {
            txtEmail.setText("N/A");
            return;
        }

        // Tham chiếu tới nút 'users' trong cơ sở dữ liệu Firebase
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Account").child(accountId);

        // Lắng nghe dữ liệu từ Firebase
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Lấy email từ dữ liệu
                    String email = dataSnapshot.child("email").getValue(String.class);
                    txtEmail.setText(email != null ? email : "N/A");
                } else {
                    txtEmail.setText("N/A");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi khi đọc dữ liệu
                Toast.makeText(OrderPaymentActivity.this, "Failed to load email: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }




    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }
    }
