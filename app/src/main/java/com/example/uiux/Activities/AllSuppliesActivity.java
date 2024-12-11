package com.example.uiux.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

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

import com.example.uiux.Adapters.AllSuppliesAdapter;
import com.example.uiux.Adapters.BestSellerAdapter;
import com.example.uiux.Adapters.ChipAdapter;
import com.example.uiux.Model.Supplies;
import com.example.uiux.Model.Supplies_Review;
import com.example.uiux.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AllSuppliesActivity extends AppCompatActivity {
    RecyclerView rcv_chip, rcv_all_supplies;
    ImageView img_back_all_supply;
    ChipAdapter chipAdapter;
    AllSuppliesAdapter allSuppliesAdapter;
    List<String> chipList;
    List<Supplies> suppliesList;
    List<Supplies_Review> reviewList;

    private boolean isLoading = false;
    private String lastLoadedKey = null;

    ProgressBar progressBar_all_supplies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
        setContentView(R.layout.activity_all_supplies);

        initWidget();

//        String selectedType = getIntent().getStringExtra("selectedType");
//        if (selectedType != null) {
//            Log.e("Selected Type", selectedType);
//            // Chọn chip tương ứng và lọc danh sách
//            loadInitialSupplies(() -> selectChip(selectedType.trim()));
//        } else {
//            loadInitialSupplies(null);
//        }
//        String selectedCategory = getIntent().getStringExtra("selectedCategory");
//        if (selectedCategory != null) {
//            Log.e("Selected Category", selectedCategory);
//            loadInitialSupplies(() -> filterSuppliesByCategory(selectedCategory.trim()));
//        } else {
//            loadInitialSupplies(null);
//        }
        loadInitialSupplies(() -> {
            String selectedType = getIntent().getStringExtra("selectedType");
            if (selectedType != null) {
                Log.e("Selected Type", selectedType);
                selectChip(selectedType.trim());
            }

            String selectedCategory = getIntent().getStringExtra("selectedCategory");
            if (selectedCategory != null) {
                int position = chipList.indexOf(selectedCategory);
                if (position != -1) { // Kiểm tra nếu tìm thấy vị trí
                    chipAdapter.setSelectedPosition(position);
                    rcv_chip.smoothScrollToPosition(position);
                    filterSuppliesByCategory(selectedCategory.trim());
                    Log.e("Selected Category", selectedCategory);
                } else {
                    Log.e("Selected Category", "Category not found in chipList");
                }
//                chipAdapter.setSelectedPosition(position);
//                rcv_chip.smoothScrollToPosition(position);
//                Log.e("Selected Category", selectedCategory);
//                filterSuppliesByCategory(selectedCategory.trim());
            }
        });
    }

    void initWidget() {
        progressBar_all_supplies = findViewById(R.id.progressBar_all_supplies);
        img_back_all_supply = findViewById(R.id.img_back_all_supply);
        img_back_all_supply.setOnClickListener(view -> finish());
        rcv_chip = findViewById(R.id.rcv_chip);
        rcv_all_supplies = findViewById(R.id.rcv_all_supplies);

        chipList = new ArrayList<>();
        suppliesList = new ArrayList<>();
        reviewList = new ArrayList<>();

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

        rcv_all_supplies.setLayoutManager(new GridLayoutManager(this, 3));
        rcv_all_supplies.setHasFixedSize(true); // Tối ưu hóa RecyclerView nếu item cố định kích thước
        allSuppliesAdapter = new AllSuppliesAdapter(suppliesList, reviewList, this);
        rcv_all_supplies.setAdapter(allSuppliesAdapter);

        rcv_all_supplies.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

                Log.d("ScrollDebug", "totalItemCount: " + totalItemCount + ", lastVisibleItemPosition: " + lastVisibleItemPosition);

                if (!isLoading && totalItemCount <= (lastVisibleItemPosition + 3)) {
                    Log.d("ScrollDebug", "Trigger loadMoreSupplies");
                    loadMoreSupplies();
                }
            }
        });

        loadInitialSupplies(null);
    }

    private void loadInitialSupplies(Runnable callback) {
        isLoading = true;
        progressBar_all_supplies.setVisibility(View.VISIBLE);
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
                allSuppliesAdapter.notifyDataSetChanged();
                isLoading = false;

                progressBar_all_supplies.setVisibility(View.GONE);

                // Gọi callback nếu có
                if (callback != null) {
                    callback.run();
                } else {
                    allSuppliesAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                isLoading = false;
                progressBar_all_supplies.setVisibility(View.GONE);
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
                    allSuppliesAdapter.notifyItemRangeInserted(previousSize, newSupplies.size());
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
        allSuppliesAdapter.updateList(filteredList);
    }

//    private void selectChip(String type) {
//        for (int i = 0; i < chipList.size(); i++) {
//            Log.e("ChipList", "Chip at " + i + ": " + chipList.get(i));
//            if (chipList.get(i).equals(type)) {
//                rcv_chip.smoothScrollToPosition(i);
//                ChipAdapter.ChipViewHolder viewHolder = (ChipAdapter.ChipViewHolder)
//                        rcv_chip.findViewHolderForAdapterPosition(i);
//                if (viewHolder != null) {
//                    viewHolder.getChip().performClick(); // Sử dụng getter để lấy chip
//                    Log.e("SelectChip", "Chip found and clicked at position: " + i);
//                } else {
//                    Log.e("SelectChip", "ViewHolder is null at position: " + i);
//                }
//                break;
//            }
//        }
//    }
    private void selectChip(String type) {
        int position = chipList.indexOf(type);
        if (position != -1) {
            chipAdapter.setSelectedPosition(position);
            rcv_chip.smoothScrollToPosition(position);
            filterSuppliesByChip(type);
            Log.e("SelectChip", "Chip selected at position: " + position);
        } else {
            Log.e("SelectChip", "Type not found in chipList");
        }
    }

    private void filterSuppliesByCategory(String category) {
        List<Supplies> filteredList = new ArrayList<>();

        for (Supplies supply : suppliesList) {
            if (supply.getCategory() != null && supply.getCategory().equalsIgnoreCase(category)) {
                filteredList.add(supply);
            }
        }

        // Cập nhật adapter với danh sách đã lọc
//        allSuppliesAdapter = new AllSuppliesAdapter(filteredList, reviewList, getApplicationContext());
//        rcv_all_supplies.setAdapter(allSuppliesAdapter);
//        allSuppliesAdapter.notifyDataSetChanged();
        allSuppliesAdapter.updateList(filteredList);
    }
}
