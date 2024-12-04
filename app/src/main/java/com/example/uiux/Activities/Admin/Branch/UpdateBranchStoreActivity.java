package com.example.uiux.Activities.Admin.Branch;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import com.example.uiux.Activities.Admin.Supplies.UpdateSuppliesActivity;
import com.example.uiux.Activities.User.Profile.AddressActivity;
import com.example.uiux.Activities.User.Profile.UpdateAddressActivity;
import com.example.uiux.Adapters.AddressAdapter;
//import com.example.uiux.Adapters.BranchStoreAdapter;
import com.example.uiux.Adapters.BranchStoreAdapter;
import com.example.uiux.Model.Account_Address;
import com.example.uiux.Model.BranchStore;
import com.example.uiux.Model.Supplies;
import com.example.uiux.R;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UpdateBranchStoreActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private BranchStoreAdapter branchStoreAdapter;
    private ImageView imgv_back_add_store;
    private MaterialCardView mcv_add_store;
    private List<BranchStore> branchStoreList = new ArrayList<>();
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
        setContentView(R.layout.activity_update_branch_store);
        initWidget();


        imgv_back_add_store.setOnClickListener(view -> finish());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        branchStoreAdapter = new BranchStoreAdapter(branchStoreList, this);
        recyclerView.setAdapter(branchStoreAdapter);
       // databaseReference = FirebaseDatabase.getInstance().getReference("Branch Store");

        loadAddresses();

        mcv_add_store.setOnClickListener(view -> {
            Intent gotoAdd=new Intent(UpdateBranchStoreActivity.this, BranchStoreActivity.class);
//            gotoAdd.putExtra("account_id",accountId);
            startActivity(gotoAdd);
        });
    }
    void initWidget() {
        imgv_back_add_store = findViewById(R.id.img_back_my_store);
        recyclerView = findViewById(R.id.rcv_my_store);
        mcv_add_store = findViewById(R.id.mcv_add_store);
    }
    private void loadAddresses() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Branch Store");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                branchStoreList.clear();
                for (DataSnapshot suppSnapshot : snapshot.getChildren()) {
                    BranchStore branchStores = suppSnapshot.getValue(BranchStore.class);
                    branchStoreList.add(branchStores);
                    Log.e("BranchStore add",branchStores.getBranch_name());
                }
                branchStoreAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateBranchStoreActivity.this, "Failed to load Store.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}