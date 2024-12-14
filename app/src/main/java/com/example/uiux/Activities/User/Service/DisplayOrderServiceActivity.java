package com.example.uiux.Activities.User.Service;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiux.Activities.User.Order.UpdateOrderActivity;
import com.example.uiux.Adapters.OrderAdminAdapter;
import com.example.uiux.Adapters.UserDisplayService;
import com.example.uiux.Model.Order;
import com.example.uiux.Model.ServiceOrder;
import com.example.uiux.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DisplayOrderServiceActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private UserDisplayService userDisplayServiceAdapter;
    private ImageView img_back_order_status;
    private List<ServiceOrder> serviceOrderList = new ArrayList<>();
    private DatabaseReference databaseReference;
    private String accountId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_display_order_service);
        initWidget();
        SharedPreferences preferences =  getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        accountId = preferences.getString("accountID", null);
        Log.e("ID",accountId);
        img_back_order_status.setOnClickListener(view -> finish());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userDisplayServiceAdapter = new UserDisplayService(this,serviceOrderList);
        recyclerView.setAdapter(userDisplayServiceAdapter);
        // databaseReference = FirebaseDatabase.getInstance().getReference("Branch Store");

        loadOrderService();
    }
    void initWidget() {
        img_back_order_status = findViewById(R.id.img_back_order_status);
        recyclerView = findViewById(R.id.rcv_my_store);
    }
    private void loadOrderService() {
        // Tham chiếu tới Firebase "Order Service"
        databaseReference = FirebaseDatabase.getInstance().getReference("Service Order");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Xóa danh sách cũ trước khi thêm mới
                serviceOrderList.clear();

                // Duyệt qua từng Service Order trong Firebase
                for (DataSnapshot serviceOrderSnapshot : snapshot.getChildren()) {
                    ServiceOrder serviceOrder = serviceOrderSnapshot.getValue(ServiceOrder.class);

                    // Kiểm tra `account_id` khớp với người dùng hiện tại
                    if (serviceOrder != null && serviceOrder.getAccount_id().equals(accountId)&&serviceOrder.getStatus()==0) {
                        serviceOrderList.add(serviceOrder);
                    }
                }

                // Cập nhật adapter
                userDisplayServiceAdapter.notifyDataSetChanged();

                if (serviceOrderList.isEmpty()) {
                    Toast.makeText(DisplayOrderServiceActivity.this, "No orders found for this account.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi khi tải dữ liệu từ Firebase
                Toast.makeText(DisplayOrderServiceActivity.this, "Failed to load orders: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("FirebaseError", error.getMessage());
            }
        });
    }
}