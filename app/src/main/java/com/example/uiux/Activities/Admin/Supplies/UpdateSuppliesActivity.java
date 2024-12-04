package com.example.uiux.Activities.Admin.Supplies;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import com.example.uiux.Activities.Admin.Category.UpdateCategoryActivity;
import com.example.uiux.Adapters.CategoryAdminAdapter;
import com.example.uiux.Adapters.SuppliesAdapter;
import com.example.uiux.Model.Category;
import com.example.uiux.Model.Supplies;
import com.example.uiux.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UpdateSuppliesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ImageView imgv_back_add_supplies, imgv_add_supplies;
    private SuppliesAdapter suppliesAdapter;
    private List<Supplies> suppliesList = new ArrayList<>();
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
        setContentView(R.layout.activity_update_supplies);
        imgv_back_add_supplies = findViewById(R.id.img_back_my_supplies);
        imgv_add_supplies = findViewById(R.id.imgv_add_supplies);

        imgv_back_add_supplies.setOnClickListener(view -> {
            finish();
        });

        imgv_add_supplies.setOnClickListener(view -> {
            Intent intent = new Intent(UpdateSuppliesActivity.this, SuppliesActivity.class);
            startActivity(intent);
        });

        recyclerView = findViewById(R.id.recyclerViewSupplies);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        suppliesAdapter = new SuppliesAdapter(suppliesList, this);
        recyclerView.setAdapter(suppliesAdapter);
        loadSupplies();

    }

    private void loadSupplies() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Supplies");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                suppliesList.clear();
                for (DataSnapshot suppSnapshot : snapshot.getChildren()) {
                    Supplies supplies = suppSnapshot.getValue(Supplies.class);
                    suppliesList.add(supplies);
                    //Log.e("Supplies add",supplies.getName());
                }
                suppliesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateSuppliesActivity.this, "Failed to load category.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}