package com.example.uiux.Activities.Admin.Services;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
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

import com.example.uiux.Activities.Admin.Supplies.SuppliesActivity;
import com.example.uiux.Activities.User.Service.DisplayOrderServiceActivity;
import com.example.uiux.Adapters.BookingServiceAdminAdapter;
import com.example.uiux.Adapters.UserDisplayService;
import com.example.uiux.Model.BranchStore;
import com.example.uiux.Model.Category;
import com.example.uiux.Model.ServiceOrder;
import com.example.uiux.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DisplayServiceBookingActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private BookingServiceAdminAdapter adminDisplayServiceAdapter;
    private ImageView img_back_order_status;
    private Spinner spinnerBranch;
    private List<ServiceOrder> serviceOrderList = new ArrayList<>();
    private DatabaseReference databaseReference;

    private List<String> branchList = new ArrayList<>();
    private Map<String, String> branchMap = new HashMap<>(); // Map lưu trữ tên chi nhánh và id chi nhánh

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
        setContentView(R.layout.activity_display_service_booking);
        initWidget();

        img_back_order_status.setOnClickListener(view -> finish());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        FetchSpinnerBranch();

        adminDisplayServiceAdapter = new BookingServiceAdminAdapter(this,serviceOrderList);
        recyclerView.setAdapter(adminDisplayServiceAdapter);


        // Tải đơn đặt dịch vụ mặc định (tất cả chi nhánh)
        loadOrderService(null); // null để không lọc chi nhánh ban đầu




    }

private void FetchSpinnerBranch() {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("Branch Store");
    ref.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            branchList.clear(); // Đảm bảo làm mới danh sách chi nhánh
            branchMap.clear(); // Xóa map cũ

            // Thêm chi nhánh mặc định "Tất cả chi nhánh"
            branchList.add("All Branches");
            branchMap.put("All Branches", null); // null là tất cả các chi nhánh

            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                BranchStore branch = snapshot.getValue(BranchStore.class);
                if (branch != null && branch.getStatus() != 2 && branch.getStatus() != 3) {
                    branchList.add(branch.getBranch_name());
                    branchMap.put(branch.getBranch_name(), branch.getBranch_Store_id()); // Lưu tên chi nhánh và id của chi nhánh
                }
            }

            // Thiết lập adapter cho Spinner
            ArrayAdapter<String> adapter = new ArrayAdapter<>(DisplayServiceBookingActivity.this, android.R.layout.simple_spinner_item, branchList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerBranch.setAdapter(adapter);

            spinnerBranch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedBranchName = branchList.get(position);
                    String branchId = branchMap.get(selectedBranchName);
                    loadOrderService(branchId); // Lọc đơn hàng theo chi nhánh đã chọn
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // Không làm gì khi không có mục nào được chọn
                }
            });

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            // Xử lý nếu có lỗi
        }
    });
}
    void initWidget() {
        img_back_order_status = findViewById(R.id.img_back_order_status);
        recyclerView = findViewById(R.id.rcv_my_store);
        spinnerBranch= findViewById(R.id.spinner_branch);
    }
private void loadOrderService(String branchId) {
    databaseReference = FirebaseDatabase.getInstance().getReference("Service Order");

    databaseReference.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            serviceOrderList.clear();

            for (DataSnapshot serviceOrderSnapshot : snapshot.getChildren()) {
                ServiceOrder serviceOrder = serviceOrderSnapshot.getValue(ServiceOrder.class);

                // Kiểm tra account_id và trạng thái
                if (serviceOrder != null  && serviceOrder.getStatus() == 0) {
                    // Kiểm tra nếu có lọc chi nhánh
                    if (branchId == null || serviceOrder.getBranch_id().equals(branchId)) {
                        serviceOrderList.add(serviceOrder);
                    }
                }
            }

            adminDisplayServiceAdapter.notifyDataSetChanged();

            if (serviceOrderList.isEmpty()) {
                Toast.makeText(DisplayServiceBookingActivity.this, "No orders found.", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(DisplayServiceBookingActivity.this, "Failed to load orders: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("FirebaseError", error.getMessage());
        }
    });
}
}