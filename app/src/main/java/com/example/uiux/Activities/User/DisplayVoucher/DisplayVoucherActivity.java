package com.example.uiux.Activities.User.DisplayVoucher;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiux.Activities.Admin.Voucher.UpdateVoucherActivity;
import com.example.uiux.Activities.Admin.Voucher.VoucherActivity;
import com.example.uiux.Adapters.DisplayVoucherAdapter;
import com.example.uiux.Model.Voucher;
import com.example.uiux.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DisplayVoucherActivity extends AppCompatActivity {
    private ImageView img_back_choose_vouncher;
    private RecyclerView recyclerView;
    private DisplayVoucherAdapter displayVoucherAdapter;
    private List<Voucher> voucherList = new ArrayList<>();
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
        setContentView(R.layout.activity_display_voucher);

        img_back_choose_vouncher = findViewById(R.id.img_back_choose_vouncher);
        img_back_choose_vouncher.setOnClickListener(view -> finish());
        // Set up RecyclerView
        recyclerView = findViewById(R.id.rcv_my_vouncher); // ID from XML layout
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        displayVoucherAdapter = new DisplayVoucherAdapter(voucherList, this);
        recyclerView.setAdapter(displayVoucherAdapter);

        // Load vouchers from Firebase
        loadDisplayVoucher();
    }

    private void loadDisplayVoucher() {
        // Lấy category từ Intent
        String category = getIntent().getStringExtra("category");

        // Get Firebase reference to "Voucher" node
        databaseReference = FirebaseDatabase.getInstance().getReference("Voucher");

        // Lắng nghe sự thay đổi của dữ liệu voucher
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                voucherList.clear(); // Xóa dữ liệu cũ trước khi thêm mới

                // Lặp qua tất cả các voucher trong Firebase
                for (DataSnapshot voucherSnapshot : snapshot.getChildren()) {
                    Voucher voucher = voucherSnapshot.getValue(Voucher.class);
                    if (voucher != null) {
                        // Kiểm tra xem category của voucher có khớp với category nhận được từ Intent không
                        //||
                        if (voucher.getCategory().equals(category)||voucher.getCategory().equals("Ship")||voucher.getCategory().equals("All")) {
                            if(voucher.getRemaining_quantity()>0)
                            {
                                voucherList.add(voucher); // Thêm voucher vào danh sách nếu category trùng khớp
                            }

                        }
                    }
                }

                // Thông báo adapter đã có dữ liệu mới
                displayVoucherAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DisplayVoucherActivity.this, "Failed to load vouchers.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
