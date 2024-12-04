package com.example.uiux.Activities.Admin.Services;

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
import com.example.uiux.Adapters.ServiceAdapter;
import com.example.uiux.Adapters.SuppliesAdapter;
import com.example.uiux.Model.Service;
import com.example.uiux.Model.Supplies;
import com.example.uiux.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UpdateServiceActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ImageView imgv_back_add_service, imgv_add_service;
    private ServiceAdapter serviceAdapter;
    private List<Service> serviceList = new ArrayList<>();
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
        setContentView(R.layout.activity_update_service);
        imgv_back_add_service = findViewById(R.id.img_back_my_service);
        imgv_add_service = findViewById(R.id.imgv_add_service);

        imgv_back_add_service.setOnClickListener(view -> {
            finish();
        });

        imgv_add_service.setOnClickListener(view -> {
            Intent intent = new Intent(UpdateServiceActivity.this, ServiceActivity.class);
            startActivity(intent);
        });

        recyclerView = findViewById(R.id.recyclerViewServices);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        serviceAdapter = new ServiceAdapter(serviceList, this);
        recyclerView.setAdapter(serviceAdapter);
        loadService();

    }
    private void loadService() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Service");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                serviceList.clear();
                for (DataSnapshot suppSnapshot : snapshot.getChildren()) {
                    Service service = suppSnapshot.getValue(Service.class);
                    serviceList.add(service);
                    //Log.e("Supplies add",supplies.getName());
                }
                serviceAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateServiceActivity.this, "Failed to load category.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}