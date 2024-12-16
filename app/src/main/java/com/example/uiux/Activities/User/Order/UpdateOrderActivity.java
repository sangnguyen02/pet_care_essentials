package com.example.uiux.Activities.User.Order;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

import com.example.uiux.Activities.Admin.Branch.UpdateBranchStoreActivity;
import com.example.uiux.Adapters.BranchStoreAdapter;
import com.example.uiux.Adapters.ChipAdapter;
import com.example.uiux.Adapters.ChipOrderStatusAdapter;
import com.example.uiux.Adapters.OrderAdminAdapter;
import com.example.uiux.Model.BranchStore;
import com.example.uiux.Model.Order;
import com.example.uiux.Model.Supplies;
import com.example.uiux.R;
import com.example.uiux.Utils.OrderStatus;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UpdateOrderActivity extends AppCompatActivity {
    private RecyclerView rcv_admin_order, rcv_chip_order;
    private OrderAdminAdapter orderAdminAdapter;
    private ImageView img_back_order_status;
    private List<Order> orderList = new ArrayList<>();
    ChipOrderStatusAdapter chipOrderStatusAdapter;
    List<String> chipList;
    private DatabaseReference databaseReference;
    private ProgressBar progressBar_order_admin;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
        setContentView(R.layout.activity_update_order);

        initWidget();
        img_back_order_status.setOnClickListener(view -> finish());

        rcv_admin_order.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        orderAdminAdapter = new OrderAdminAdapter(orderList, this);
        rcv_admin_order.setAdapter(orderAdminAdapter);
        // databaseReference = FirebaseDatabase.getInstance().getReference("Branch Store");

        loadOrder();
        rcv_chip_order.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        chipOrderStatusAdapter = new ChipOrderStatusAdapter(chipList, this, this::filterOrderByChip);
        rcv_chip_order.setAdapter(chipOrderStatusAdapter);
    }
    void initWidget() {
        progressBar_order_admin = findViewById(R.id.progressBar_order_admin);
        img_back_order_status = findViewById(R.id.img_back_order_status);
        rcv_admin_order = findViewById(R.id.rcv_admin_order);
        rcv_chip_order = findViewById(R.id.rcv_chip_order);
        chipList = new ArrayList<>();
        chipList.add("ALL");
        chipList.add(OrderStatus.getStatusName(OrderStatus.PENDING));
        chipList.add(OrderStatus.getStatusName(OrderStatus.PREPARING));
        chipList.add(OrderStatus.getStatusName(OrderStatus.SHIPPING));
        chipList.add(OrderStatus.getStatusName(OrderStatus.DELIVERED));
        chipList.add(OrderStatus.getStatusName(OrderStatus.CANCELED));
        chipList.add(OrderStatus.getStatusName(OrderStatus.RETURNED));

    }

    private void filterOrderByChip(String chipText) {
        List<Order> filteredList = new ArrayList<>();

        if ("ALL".equalsIgnoreCase(chipText)) {
            filteredList = orderList;
        } else {
            int status = -1;
            for (int i = 0; i <= 5; i++) {
                if (OrderStatus.getStatusName(i).equalsIgnoreCase(chipText)) {
                    status = i;
                    break;
                }
            }
            for (Order order : orderList) {
                if (order.getStatus() == status) {
                    filteredList.add(order);
                }
            }
        }

        orderAdminAdapter.updateList(filteredList);
    }
    private void loadOrder() {
        progressBar_order_admin.setVisibility(View.VISIBLE);
        databaseReference = FirebaseDatabase.getInstance().getReference("Order");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear();
                for (DataSnapshot suppSnapshot : snapshot.getChildren()) {
                    Order order = suppSnapshot.getValue(Order.class);
                    if (order.getStatus() != OrderStatus.RETURN_PRODUCT_WAITING
                            && order.getStatus() != OrderStatus.RETURN_REQUEST_PENDING) {
                        orderList.add(order);
                    }

                }
                orderAdminAdapter.notifyDataSetChanged();
                progressBar_order_admin.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateOrderActivity.this, "Failed to load Order.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}