package com.example.uiux.Activities.Admin.Supplies;

import android.content.Intent;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiux.Activities.Admin.Category.UpdateCategoryActivity;
import com.example.uiux.Adapters.CategoryAdminAdapter;
import com.example.uiux.Adapters.ChipAdapter;
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
    private RecyclerView recyclerView, rcv_chip;
    private ImageView imgv_back_add_supplies, imgv_add_supplies;
    private ProgressBar progressBar_all_supplies_admin;
    private SuppliesAdapter suppliesAdapter;
    private ChipAdapter chipAdapter;
    private List<Supplies> suppliesList = new ArrayList<>();
    List<String> chipList = new ArrayList<>();
    private DatabaseReference databaseReference;
    private String lastLoadedKey = null;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
        setContentView(R.layout.activity_update_supplies);

        initWidget();


    }

    @Override
    protected void onResume() {
        super.onResume();
        loadInitialSupplies(null);
    }

    void initWidget() {
        imgv_back_add_supplies = findViewById(R.id.img_back_my_supplies);
        progressBar_all_supplies_admin = findViewById(R.id.progressBar_all_supplies_admin);
        imgv_add_supplies = findViewById(R.id.imgv_add_supplies);
        imgv_back_add_supplies.setOnClickListener(view -> {
            finish();
        });
        imgv_add_supplies.setOnClickListener(view -> {
            Intent intent = new Intent(UpdateSuppliesActivity.this, SuppliesActivity.class);
            startActivity(intent);
        });


        rcv_chip = findViewById(R.id.rcv_chip_admin_supply);


        chipList.add("All");
        chipList.add("Dog");
        chipList.add("Cat");
        chipList.add("Food");
        chipList.add("Healthy");
        chipList.add("Toy");
        chipList.add("Necklace");
        chipList.add("Bag");
        chipList.add("Wash");
        chipList.add("Clothes");
        chipList.add("Tray");

        rcv_chip.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        chipAdapter = new ChipAdapter(chipList, this, chipText -> filterSuppliesByChip(chipText));
        rcv_chip.setAdapter(chipAdapter);

        recyclerView = findViewById(R.id.recyclerViewSupplies);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        suppliesAdapter = new SuppliesAdapter(suppliesList, this);
        recyclerView.setAdapter(suppliesAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

                Log.d("ScrollDebug", "totalItemCount: " + totalItemCount + ", lastVisibleItemPosition: " + lastVisibleItemPosition);

                if (!isLoading && totalItemCount <= (lastVisibleItemPosition + 3)) {
                    Log.d("ScrollDebug", "Trigger loadMoreSupplies");
                    loadMoreSupplies();
                }
            }
        });

//        loadSupplies();
        loadInitialSupplies(null);
    }

//    private void loadSupplies() {
//        databaseReference = FirebaseDatabase.getInstance().getReference("Supplies");
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                suppliesList.clear();
//                for (DataSnapshot suppSnapshot : snapshot.getChildren()) {
//                    Supplies supplies = suppSnapshot.getValue(Supplies.class);
//                    suppliesList.add(supplies);
//                    //Log.e("Supplies add",supplies.getName());
//                }
//                suppliesAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(UpdateSuppliesActivity.this, "Failed to load category.", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    private void loadInitialSupplies(Runnable callback) {
        isLoading = true;
        progressBar_all_supplies_admin.setVisibility(View.VISIBLE);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Supplies");
        ref.limitToFirst(30).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                suppliesList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Supplies supply = snapshot.getValue(Supplies.class);
                    suppliesList.add(supply);
                    lastLoadedKey = snapshot.getKey(); // Cập nhật lastLoadedKey
                }
                suppliesAdapter.notifyDataSetChanged();
                isLoading = false;

                progressBar_all_supplies_admin.setVisibility(View.GONE);

                // Gọi callback nếu có
                if (callback != null) {
                    callback.run();
                } else {
                    suppliesAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                isLoading = false;
                progressBar_all_supplies_admin.setVisibility(View.GONE);
            }
        });
    }

    private void loadMoreSupplies() {
        if (lastLoadedKey == null) return;
        isLoading = true;

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Supplies");
        ref.orderByKey().startAfter(lastLoadedKey).limitToFirst(10).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Supplies> newSupplies = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Supplies supply = snapshot.getValue(Supplies.class);
                    newSupplies.add(supply);
                    lastLoadedKey = snapshot.getKey();
                }

                if (newSupplies.isEmpty()) {
                    Log.d("LoadMore", "No more supplies to load.");
                } else {
                    int previousSize = suppliesList.size();
                    suppliesList.addAll(newSupplies);
                    suppliesAdapter.notifyItemRangeInserted(previousSize, newSupplies.size());
                }

                isLoading = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                isLoading = false;
            }
        });
    }

    private void filterSuppliesByChip(String chipText) {
        List<Supplies> filteredList = new ArrayList<>();

        if ("All".equals(chipText)) {
            filteredList = suppliesList;
        } else {
            for (Supplies supply : suppliesList) {
                if (chipText.equals(supply.getType()) ||
                        (supply.getCategory() != null ) && chipText.equalsIgnoreCase(supply.getCategory()))
                {
                    filteredList.add(supply);
                }
            }
        }

//        allSuppliesAdapter = new AllSuppliesAdapter(filteredList, reviewList, getApplicationContext());
//        rcv_all_supplies.setAdapter(allSuppliesAdapter);
        suppliesAdapter.updateList(filteredList);
    }
}