package com.example.uiux.Activities.User.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiux.Adapters.AddressAdapter;
import com.example.uiux.Model.Account_Address;
import com.example.uiux.R;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UpdateAddressActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AddressAdapter addressAdapter;
    private ImageView imgv_back_add_address;
    private MaterialCardView mcv_add_address;
    private List<Account_Address> addressList = new ArrayList<>();
    private DatabaseReference databaseReference;
    private String accountId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.transparent));
        setContentView(R.layout.activity_update_address);

        initWidget();


        imgv_back_add_address.setOnClickListener(view -> finish());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        addressAdapter = new AddressAdapter(addressList, this);
        recyclerView.setAdapter(addressAdapter);
        Intent intent= getIntent();
        accountId = intent.getStringExtra("account_id");
        databaseReference = FirebaseDatabase.getInstance().getReference("Account_Address");

        loadAddresses();

        mcv_add_address.setOnClickListener(view -> {
            Intent gotoAdd=new Intent(UpdateAddressActivity.this,AddressActivity.class);
            gotoAdd.putExtra("account_id",accountId);
            startActivity(gotoAdd);
        });

    }

    void initWidget() {
        imgv_back_add_address = findViewById(R.id.img_back_my_address);
        recyclerView = findViewById(R.id.rcv_my_address);
        mcv_add_address = findViewById(R.id.mcv_add_address);
    }
    private void loadAddresses() {
        databaseReference.orderByChild("account_id").equalTo(accountId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                addressList.clear();
                for (DataSnapshot addressSnapshot : snapshot.getChildren()) {
                    Account_Address address = addressSnapshot.getValue(Account_Address.class);
                    addressList.add(address);
                }
                addressAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateAddressActivity.this, "Failed to load addresses.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}