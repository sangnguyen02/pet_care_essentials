package com.example.uiux.Activities.User.CancelOrder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.uiux.Model.AccountWallet;
import com.example.uiux.Model.InfoCancelOrder;
import com.example.uiux.Model.Order;
import com.example.uiux.Model.WalletHistory;
import com.example.uiux.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class UserCancelOrderActivity extends AppCompatActivity {

    private RadioGroup rgCancelReasons;
    private EditText etOtherReason;
    private Button btnCancelOrder;
    private DatabaseReference cancelOrderRef;
    private  DatabaseReference orderRef;
    private  DatabaseReference accountWalletRef;
    private  DatabaseReference walletHistoryRef;
    private String orderId;
    private  String accountId;
    private String wallet_id;
    private double totalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_cancel_order);

        // Khởi tạo các widget
        rgCancelReasons = findViewById(R.id.rg_cancel_reasons);
        etOtherReason = findViewById(R.id.et_other_reason);
        btnCancelOrder = findViewById(R.id.btn_cancel_order);

        // Khởi tạo Firebase Reference
        cancelOrderRef = FirebaseDatabase.getInstance().getReference("Cancel Order");
        orderRef = FirebaseDatabase.getInstance().getReference("Order");
        accountWalletRef = FirebaseDatabase.getInstance().getReference("Account Wallet");
        walletHistoryRef = FirebaseDatabase.getInstance().getReference("Wallet History");


        // Lấy Order ID từ Intent
        Intent intent = getIntent();
        orderId = intent.getStringExtra("order_id");
        SharedPreferences preferences =  getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        accountId = preferences.getString("accountID", null);
        Log.e("ID",accountId);

        // Xử lý sự kiện chọn lý do
        rgCancelReasons.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_other_reason) {
                // Hiển thị EditText nếu chọn "Lý do khác"
                etOtherReason.setVisibility(View.VISIBLE);
            } else {
                // Ẩn EditText nếu chọn lý do khác
                etOtherReason.setVisibility(View.GONE);
            }
        });

        // Xử lý sự kiện khi nhấn nút Hủy đơn hàng
        btnCancelOrder.setOnClickListener(v -> cancelOrder());
    }

    private void cancelOrder() {
        int selectedReasonId = rgCancelReasons.getCheckedRadioButtonId();

        if (selectedReasonId == -1) {
            Toast.makeText(this, "Vui lòng chọn lý do hủy đơn hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        String otherType = "None"; // Giá trị mặc định nếu không có nhập lý do khác
        int type = 0; // Giá trị tương ứng với lý do hủy
        String reason;

        if (selectedReasonId == R.id.rb_other_reason) {
            // Lấy lý do khác
            otherType = etOtherReason.getText().toString().trim();
            if (otherType.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập lý do khác", Toast.LENGTH_SHORT).show();
                return;
            }
            reason = otherType;
        } else {
            // Lấy lý do từ RadioButton
            RadioButton selectedRadioButton = findViewById(selectedReasonId);
            reason = selectedRadioButton.getText().toString();

            // Xác định loại lý do dựa trên thứ tự trong RadioGroup
            type = rgCancelReasons.indexOfChild(selectedRadioButton) + 1;
        }

        // Lấy ngày hiện tại theo định dạng hh:mm dd/MM/yyyy
        String currentDate = new SimpleDateFormat("hh:mm dd/MM/yyyy", Locale.getDefault()).format(new Date());

        // Tạo InfoCancelOrder object
        String cancelId = cancelOrderRef.push().getKey();
        InfoCancelOrder cancelOrder = new InfoCancelOrder(cancelId, orderId, type, otherType, currentDate);

        // Gửi dữ liệu vào Firebase
        cancelOrderRef.child(cancelId).setValue(cancelOrder)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Hủy đơn hàng thành công", Toast.LENGTH_SHORT).show();
                        checkPayment(orderId);
                        updateOrderStatus(4);
                        finish();
                    } else {
                        Toast.makeText(this, "Hủy đơn hàng thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Hàm kiểm tra thanh toán của đơn hàng
    private void checkPayment(String orderId) {
        orderRef.child(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Kiểm tra xem đơn hàng có tồn tại không
                if (dataSnapshot.exists()) {
                    Order order = dataSnapshot.getValue(Order.class);
                    if (order != null && order.getIs_completed_payment() == 1) {
                        // Thanh toán hoàn tất
                        Toast.makeText(UserCancelOrderActivity.this, "Thanh toán hoàn tất", Toast.LENGTH_SHORT).show();
                        // Bạn có thể thực hiện các hành động khác nếu thanh toán đã hoàn tất
                        totalPrice=order.getTotal_price();
                        Refund();
                    } else {
                        // Thanh toán chưa hoàn tất
                        Toast.makeText(UserCancelOrderActivity.this, "Thanh toán chưa hoàn tất", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UserCancelOrderActivity.this, "Đơn hàng không tồn tại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý khi truy vấn bị hủy hoặc lỗi
                Toast.makeText(UserCancelOrderActivity.this, "Lỗi khi truy vấn đơn hàng", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void Refund() {
        // Truy vấn ví của người dùng từ Firebase theo accountId
        accountWalletRef.orderByChild("account_id").equalTo(accountId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Kiểm tra ví của người dùng có tồn tại không
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                AccountWallet accountWallet = snapshot.getValue(AccountWallet.class);
                                if (accountWallet != null) {
                                    // Lấy số dư hiện tại của ví
                                    double currentBalance = accountWallet.getBalance();
                                    wallet_id=accountWallet.getWallet_id();

                                    // Cập nhật số dư mới
                                    double newBalance = currentBalance + totalPrice;

                                    // Cập nhật lại số dư ví
                                    accountWalletRef.child(accountWallet.getWallet_id()).child("balance").setValue(newBalance)
                                            .addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(UserCancelOrderActivity.this, "Hoàn tiền thành công", Toast.LENGTH_SHORT).show();
                                                    // Tạo lịch sử giao dịch hoàn tiền
                                                    CreateHistory();
                                                } else {
                                                    Toast.makeText(UserCancelOrderActivity.this, "Hoàn tiền thất bại", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }
                        } else {
                            // Nếu ví của người dùng không tồn tại
                            Toast.makeText(UserCancelOrderActivity.this, "Ví không tồn tại", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Xử lý khi truy vấn bị lỗi
                        Toast.makeText(UserCancelOrderActivity.this, "Lỗi khi truy vấn ví người dùng", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void CreateHistory() {
        // Lấy ngày giờ hiện tại theo định dạng yyyy/MM/dd HH:mm:ss
        String currentDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(new Date());

        // Tạo ID cho lịch sử giao dịch mới
        String walletHistoryId = walletHistoryRef.push().getKey();

        // Tạo đối tượng WalletHistory
        WalletHistory walletHistory = new WalletHistory(
                walletHistoryId,    // ID của lịch sử giao dịch
                wallet_id,          // wallet_id của người dùng
                String.valueOf(totalPrice),  // Số tiền hoàn tiền (dưới dạng String)
                "+",                // Trạng thái giao dịch, "+" cho hoàn tiền
                currentDate         // Ngày giờ giao dịch
        );

        // Lưu WalletHistory vào Firebase
        walletHistoryRef.child(walletHistoryId).setValue(walletHistory)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Thông báo thành công khi ghi lịch sử
                        Toast.makeText(UserCancelOrderActivity.this, "Lịch sử giao dịch đã được ghi lại", Toast.LENGTH_SHORT).show();
                    } else {
                        // Thông báo thất bại khi ghi lịch sử
                        Toast.makeText(UserCancelOrderActivity.this, "Ghi lại lịch sử giao dịch thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void updateOrderStatus(int newStatus) {
        if (orderId == null || orderId.isEmpty()) {
            Log.e("UpdateOrderStatus", "Order ID is null or empty.");
            return;
        }

        // Cập nhật trường "status" trong đơn hàng
        orderRef.child(orderId).child("status").setValue(newStatus).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("UpdateOrderStatus", "Order status updated to: " + newStatus);
                Toast.makeText(this, "Trạng thái đơn hàng đã được cập nhật.", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("UpdateOrderStatus", "Failed to update order status: " + task.getException());
                Toast.makeText(this, "Cập nhật trạng thái đơn hàng thất bại.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
