package com.example.uiux.Activities.Admin.Order;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uiux.Model.InfoReturnOrder;
import com.example.uiux.Model.Order;
import com.example.uiux.R;
import com.example.uiux.Utils.OrderStatus;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ApproveReturnOrderActivity extends AppCompatActivity {
    // Các TextView để hiển thị dữ liệu
    private TextView tvCustomerName, tvCustomerPhone, tvCustomerEmail, tvCustomerAddress;
    private TextView tvTotalPrice, tvPaymentStatus, tvDeliveryDate, tvOrderDate, tvRequestReturnDate;
    private TextView tvReturnReason, tvReturnDetailReason;
    private ImageView ivProduct1, ivProduct2, ivProduct3, ivProduct4;
    private Button approveBtn,rejectBtn;
    String info_return_id;
    String order_id;
    private DatabaseReference returnOrderRef;
    private DatabaseReference orderRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_approve_return_order);

        Intent intent=getIntent();
        info_return_id=intent.getStringExtra("info_return_id");
        order_id=intent.getStringExtra("order_id");
        initViews();
        returnOrderRef = FirebaseDatabase.getInstance().getReference("Return Order");
        orderRef = FirebaseDatabase.getInstance().getReference("Order");
        loadOrderData();
        loadInfoReturnData();
        approveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateOrderStatus(OrderStatus.RETURN_PRODUCT_WAITING);
            }
        });
        rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateOrderStatus(OrderStatus.DELIVERED);
            }
        });
    }
    private void initViews() {
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvCustomerPhone = findViewById(R.id.tvCustomerPhone);
        tvCustomerEmail = findViewById(R.id.tvCustomerEmail);
        tvCustomerAddress = findViewById(R.id.tvCustomerAddress);

        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvPaymentStatus = findViewById(R.id.tvPaymentStatus);
        tvDeliveryDate = findViewById(R.id.tvDeliveryDate);
        tvOrderDate = findViewById(R.id.tvOrderDate);
        tvRequestReturnDate = findViewById(R.id.tvRequestReturnDate);

        tvReturnReason = findViewById(R.id.tvReturnReason);
        tvReturnDetailReason = findViewById(R.id.tvReturnDetailReason);

        ivProduct1 = findViewById(R.id.ivProduct1);
        ivProduct2 = findViewById(R.id.ivProduct2);
        ivProduct3 = findViewById(R.id.ivProduct3);
        ivProduct4 = findViewById(R.id.ivProduct4);

        approveBtn=findViewById(R.id.btnAprove);
        rejectBtn=findViewById(R.id.btnReject);
    }

    private void loadOrderData()
    {
         {
        // Kiểm tra nếu order_id hợp lệ
        if (order_id == null || order_id.isEmpty()) {
            return;
        }

        // Lấy dữ liệu từ Firebase
        orderRef = FirebaseDatabase.getInstance().getReference("Order").child(order_id);
        orderRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                // Chuyển dữ liệu từ Firebase thành đối tượng Order
                Order order = task.getResult().getValue(Order.class);
                if (order != null) {
                    // Cập nhật giao diện với dữ liệu từ Order
                    tvCustomerName.setText("Name: " + order.getName_customer());
                    tvCustomerPhone.setText("Phone: " + order.getPhone_number());
                    tvCustomerEmail.setText("Email: " + order.getEmail());
                    tvCustomerAddress.setText("Address: " + order.getAddress());
                    tvTotalPrice.setText("Total Price: " + order.getTotal_price());
                    tvPaymentStatus.setText("Payment Status: " + (order.getIs_completed_payment() == 1 ? "Completed" : "Pending"));
                    tvDeliveryDate.setText("Delivery Date: " + order.getDelivery_date());
                    tvOrderDate.setText("Order Date: " + order.getDate_order());
                }
            } else {
                // Hiển thị lỗi nếu không tìm thấy dữ liệu
                tvCustomerName.setText("Error loading order data.");
            }
        }).addOnFailureListener(e -> {
            // Xử lý lỗi
            tvCustomerName.setText("Failed to load order data.");
        });
    }

    }
    private void loadInfoReturnData() {
        if (info_return_id == null || info_return_id.isEmpty()) {
            return;
        }

        returnOrderRef = FirebaseDatabase.getInstance().getReference("Return Order").child(info_return_id);
        returnOrderRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                InfoReturnOrder infoReturnOrder = task.getResult().getValue(InfoReturnOrder.class);
                if (infoReturnOrder != null) {
                    tvReturnReason.setText("Reason: " + getReturnReason(infoReturnOrder.getReason()));
                    tvReturnDetailReason.setText("Detail Reason: " + (infoReturnOrder.getDetail_reason() != null ? infoReturnOrder.getDetail_reason() : "N/A"));
                    tvRequestReturnDate.setText("Request Return Date: " + infoReturnOrder.getRequest_date());
                    loadProductImages(infoReturnOrder.getImageUrls());
                }
            } else {
                tvReturnReason.setText("Error loading return order data.");
            }
        }).addOnFailureListener(e -> {
            tvReturnReason.setText("Failed to load return order data.");
        });
    }

    private String getReturnReason(int reasonCode) {
        switch (reasonCode) {
            case 1:
                return "Wrong item";
            case 2:
                return "Item damaged";
            case 3:
                return "Item not as described";
            default:
                return "Other";
        }
    }

    private void loadProductImages(List<String> imageUrls) {
        // Kiểm tra nếu không có hình ảnh
        if (imageUrls == null || imageUrls.isEmpty()) {
            ivProduct1.setImageResource(R.drawable.logo);
            ivProduct2.setImageResource(R.drawable.logo);
            ivProduct3.setImageResource(R.drawable.logo);
            ivProduct4.setImageResource(R.drawable.logo);
        } else {
            // Tải hình ảnh từ URL vào các ImageView tương ứng
            if (imageUrls.size() > 0) {
                Picasso.get().load(imageUrls.get(0)).into(ivProduct1);
            }
            if (imageUrls.size() > 1) {
                Picasso.get().load(imageUrls.get(1)).into(ivProduct2);
            }
            if (imageUrls.size() > 2) {
                Picasso.get().load(imageUrls.get(2)).into(ivProduct3);
            }
            if (imageUrls.size() > 3) {
                Picasso.get().load(imageUrls.get(3)).into(ivProduct4);
            }
        }
    }

    private void UpdateOrderStatus(int status) {
        if (order_id == null || order_id.isEmpty()) {
            Log.e("UpdateOrderStatus", "Order ID is null or empty.");
            return;
        }
 
        // Cập nhật trạng thái trong Firebase
        orderRef.child(order_id).child("status").setValue(status).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Trạng thái đã được cập nhật
                String statusMessage = (status == OrderStatus.RETURN_PRODUCT_WAITING) ? "Yêu cầu trả hàng đã được phê duyệt." : "Yêu cầu trả hàng đã bị từ chối.";
                Toast.makeText(this, statusMessage, Toast.LENGTH_SHORT).show();
            } else {
                // Cập nhật thất bại
                Log.e("UpdateOrderStatus", "Failed to update order status: " + task.getException());
                Toast.makeText(this, "Cập nhật trạng thái đơn hàng thất bại.", Toast.LENGTH_SHORT).show();
            }
        });
    }


}