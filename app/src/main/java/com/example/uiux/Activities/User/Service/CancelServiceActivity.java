package com.example.uiux.Activities.User.Service;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uiux.Model.CancelService;
import com.example.uiux.Model.ServiceOrder;
import com.example.uiux.R;
import com.example.uiux.Utils.OrderServiceStatus;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.nullness.qual.NonNull;

public class CancelServiceActivity extends AppCompatActivity {
    private DatabaseReference orderServiceRef;
    private  DatabaseReference cancelOrderServiceRef;

    private String accountId;
    private String orderServiceId;
    private RadioGroup rgCancelReasons;
    private EditText etOtherReason;
    private MaterialButton btnCancelOrder;
    private ImageView img_back_cancel_order;
    ServiceOrder currentServiceOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
        setContentView(R.layout.activity_cancel_service);
        img_back_cancel_order = findViewById(R.id.img_back_cancel_order);
        // Kết nối UI
        rgCancelReasons = findViewById(R.id.rg_cancel_reasons);
        etOtherReason = findViewById(R.id.et_other_reason);
        btnCancelOrder = findViewById(R.id.btn_cancel_order);

        SharedPreferences preferences =  getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        accountId = preferences.getString("accountID", null);
        Log.e("ID",accountId);
        Intent intent = getIntent();
        orderServiceId = intent.getStringExtra("orderServiceId");
        orderServiceRef = FirebaseDatabase.getInstance().getReference("Service Order");
        cancelOrderServiceRef = FirebaseDatabase.getInstance().getReference("Cancel Service Order");
;
        img_back_cancel_order.setOnClickListener(view -> finish());
        // Xử lý khi chọn lý do hủy
        rgCancelReasons.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_other_reason) {
                etOtherReason.setVisibility(View.VISIBLE);
            } else {
                etOtherReason.setVisibility(View.GONE);
            }
        });
        // Lấy thông tin ServiceOrder từ Firebase
        fetchOrderDetails();

        // Xử lý nút xác nhận hủy
        btnCancelOrder.setOnClickListener(v -> handleCancelOrder());
    }

private void handleCancelOrder() {

    // Lấy lý do hủy từ RadioGroup
    int selectedReasonId = rgCancelReasons.getCheckedRadioButtonId();
    if (selectedReasonId == -1) {
        Toast.makeText(this, "Please select a reason", Toast.LENGTH_SHORT).show();
        return;
    }

    RadioButton selectedReasonButton = findViewById(selectedReasonId);
    String reason = selectedReasonButton.getText().toString();

    // Nếu chọn "Other reasons", lấy nội dung từ EditText
    String detailReason;
    if (selectedReasonId == R.id.rb_other_reason) {
        detailReason = etOtherReason.getText().toString().trim();
        if (detailReason.isEmpty()) {
            Toast.makeText(this, "Please provide a reason", Toast.LENGTH_SHORT).show();
            return;
        }
    } else {
        detailReason = "";
    }

    // Kiểm tra dữ liệu ServiceOrder đã được tải
    if (currentServiceOrder == null) {
        Toast.makeText(this, "Order details not loaded yet", Toast.LENGTH_SHORT).show();
        return;
    }

    // Hiển thị hộp thoại xác nhận trước khi hủy
    new AlertDialog.Builder(this)
            .setTitle("Confirm Cancellation")
            .setMessage("Are you sure you want to cancel this order?")
            .setPositiveButton("Yes", (dialog, which) -> {
                // Xử lý hủy đơn hàng nếu người dùng chọn "Yes"
                proceedToCancelOrder(reason, detailReason);
            })
            .setNegativeButton("No", (dialog, which) -> {
                dialog.dismiss(); // Đóng hộp thoại nếu người dùng chọn "No"
            })
            .show();
}

    // Phương thức thực sự thực hiện hủy đơn hàng
    private void proceedToCancelOrder(String reason, String detailReason) {
        // Tạo đối tượng CancelService
        String cancelServiceId = cancelOrderServiceRef.push().getKey(); // Sinh ID hủy dịch vụ
        CancelService cancelService = new CancelService(
                cancelServiceId,
                orderServiceId,
                currentServiceOrder.getOrder_date(), // Lấy thời gian hiện tại
                reason,
                detailReason,
                currentServiceOrder.getService_name(),
                currentServiceOrder.getName(),
                currentServiceOrder.getPhone_number(),
                currentServiceOrder.getEmail(),
                currentServiceOrder.getTotal_price(),
                currentServiceOrder.getBranch_id(),
                currentServiceOrder.getBranch_name(),
                currentServiceOrder.getBranch_address()
        );

        // Lưu đối tượng CancelService vào Firebase
        cancelOrderServiceRef.child(orderServiceId).setValue(cancelService)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Order cancelled successfully", Toast.LENGTH_SHORT).show();
                    UpdateServiceOrder();
                    finish(); // Quay lại màn hình trước
                })
                .addOnFailureListener(e -> {
                    Log.e("CancelService", "Failed to cancel order", e);
                    Toast.makeText(this, "Failed to cancel order", Toast.LENGTH_SHORT).show();
                });
    }
    private void UpdateServiceOrder() {
        // Kiểm tra nếu orderServiceId không hợp lệ
        if (orderServiceId == null || orderServiceId.isEmpty()) {
            Toast.makeText(this, "Order ID is invalid", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cập nhật status của đơn hàng trong Firebase
        orderServiceRef.child(orderServiceId).child("status").setValue(OrderServiceStatus.CANCEL)
                .addOnSuccessListener(unused -> {
                    Log.i("UpdateServiceOrder", "Order status updated  successfully");

                })
                .addOnFailureListener(e -> {
                    Log.e("UpdateServiceOrder", "Failed to update order status", e);

                });
    }


    private void fetchOrderDetails() {
        // Lấy dữ liệu từ Firebase
        orderServiceRef.child(orderServiceId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Chuyển dữ liệu snapshot thành đối tượng ServiceOrder
                    currentServiceOrder = snapshot.getValue(ServiceOrder.class);
                    if (currentServiceOrder != null) {
                        Log.e("Order Details", "Service Name: " + currentServiceOrder.getService_name());
                    }
                } else {
                    Toast.makeText(CancelServiceActivity.this, "Order not found", Toast.LENGTH_SHORT).show();
                    finish(); // Quay lại nếu không tìm thấy đơn hàng
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase Error", error.getMessage());
                Toast.makeText(CancelServiceActivity.this, "Failed to fetch order details", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}