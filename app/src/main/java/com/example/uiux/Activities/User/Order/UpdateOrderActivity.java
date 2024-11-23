package com.example.uiux.Activities.User.Order;

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

import com.example.uiux.Activities.Admin.Branch.UpdateBranchStoreActivity;
import com.example.uiux.Adapters.BranchStoreAdapter;
import com.example.uiux.Adapters.OrderAdminAdapter;
import com.example.uiux.Model.BranchStore;
import com.example.uiux.Model.Order;
import com.example.uiux.R;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UpdateOrderActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private OrderAdminAdapter orderAdminAdapter;
    private ImageView imgv_back_add_store;
    private MaterialCardView mcv_add_store;
    private List<Order> orderList = new ArrayList<>();
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_order);

        initWidget();
        imgv_back_add_store.setOnClickListener(view -> finish());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        orderAdminAdapter = new OrderAdminAdapter(orderList, this);
        recyclerView.setAdapter(orderAdminAdapter);
        // databaseReference = FirebaseDatabase.getInstance().getReference("Branch Store");

        loadOrder();
    }
    void initWidget() {
        imgv_back_add_store = findViewById(R.id.img_back_my_store);
        recyclerView = findViewById(R.id.rcv_my_store);
        mcv_add_store = findViewById(R.id.mcv_add_store);
    }
    private void loadOrder() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Order");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear();
                for (DataSnapshot suppSnapshot : snapshot.getChildren()) {
                    Order order = suppSnapshot.getValue(Order.class);
                    orderList.add(order);
                    Log.e("Order add",order.getOrder_id());
                }
                orderAdminAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateOrderActivity.this, "Failed to load Order.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}