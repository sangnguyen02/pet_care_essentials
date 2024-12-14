package com.example.uiux.Activities.Admin.Voucher;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
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
import com.example.uiux.Activities.Admin.Supplies.UpdateSuppliesActivity;
import com.example.uiux.Adapters.SuppliesAdapter;
import com.example.uiux.Adapters.VoucherAdapter;
import com.example.uiux.Model.Supplies;
import com.example.uiux.Model.Voucher;
import com.example.uiux.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UpdateVoucherActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageView imgv_back_add_supplies, imgv_add_supplies;
    private VoucherAdapter voucherAdapter;
    private List<Voucher> voucherList = new ArrayList<>();
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
        setContentView(R.layout.activity_update_voucher);
        imgv_back_add_supplies = findViewById(R.id.img_back_my_vouncher);
        imgv_add_supplies = findViewById(R.id.imgv_add_vouncher);
//
        imgv_back_add_supplies.setOnClickListener(view -> {
            finish();
        });

        imgv_add_supplies.setOnClickListener(view -> {
            Intent intent = new Intent(UpdateVoucherActivity.this, VoucherActivity.class);
            startActivity(intent);
        });

        recyclerView = findViewById(R.id.recyclerViewSupplies);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        voucherAdapter = new VoucherAdapter(voucherList, this);
        recyclerView.setAdapter(voucherAdapter);
        loadVoucher();

    }

    private void loadVoucher() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Voucher");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                voucherList.clear();
                for (DataSnapshot suppSnapshot : snapshot.getChildren()) {
                    Voucher supplies = suppSnapshot.getValue(Voucher.class);
                    voucherList.add(supplies);
                    //Log.e("Supplies add",supplies.getName());
                }
                voucherAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateVoucherActivity.this, "Failed to load category.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}