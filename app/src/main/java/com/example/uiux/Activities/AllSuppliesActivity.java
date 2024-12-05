package com.example.uiux.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

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

//public class AllSuppliesActivity extends AppCompatActivity {
//    RecyclerView rcv_chip, rcv_all_supplies;
//    ImageView img_back_all_supply;
//    ChipAdapter chipAdapter;
//    AllSuppliesAdapter allSuppliesAdapter;
//    List<String> chipList;
//    List<Supplies> suppliesList;
//    List<Supplies_Review> reviewList;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
//        setContentView(R.layout.activity_all_supplies);
//
//        initWidget();
//
//    }
//
//    void initWidget() {
//        img_back_all_supply = findViewById(R.id.img_back_all_supply);
//        img_back_all_supply.setOnClickListener(view -> finish());
//        rcv_chip = findViewById(R.id.rcv_chip);
//        rcv_all_supplies = findViewById(R.id.rcv_all_supplies);
//
//        chipList = new ArrayList<>();
//        suppliesList = new ArrayList<>();
//        reviewList = new ArrayList<>();
//
//        chipList.add("All");
////        chipList.add("Best Sellers");
////        chipList.add("New Arrivals");
////        chipList.add("Trending");
////        chipList.add("Popular");
//        chipList.add("Dog");
//        chipList.add("Cat");
//        chipList.add("Bird");
//        chipList.add("Hamster");
//        chipList.add("Turtle");
//
//        rcv_chip.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
//
//        chipAdapter = new ChipAdapter(chipList, this, chipText -> {
//            filterSuppliesByChip(chipText);
//        });
//        rcv_chip.setAdapter(chipAdapter);
//
//        rcv_all_supplies.setLayoutManager(new GridLayoutManager(this, 3));
//        getAllSuppliesItems();
//        //allSuppliesAdapter = new AllSuppliesAdapter(suppliesList, reviewList, this);
//        //rcv_all_supplies.setAdapter(allSuppliesAdapter);
//
//    }
//
//    private void getAllSuppliesItems() {
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Supplies");
//        ref.limitToFirst(10).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                suppliesList = new ArrayList<>();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Supplies supply = snapshot.getValue(Supplies.class);
//                    suppliesList.add(supply);
//                }
//
//                // Lấy dữ liệu đánh giá
//                DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("Supplies_Review");
//                reviewsRef.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        List<Supplies_Review> reviewList = new ArrayList<>();
//                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                            Supplies_Review review = snapshot.getValue(Supplies_Review.class);
//                            reviewList.add(review);
//                        }
//
//                        // Cập nhật Adapter với danh sách sản phẩm và đánh giá
//                        //progressBar_bestSeller.setVisibility(View.GONE);
//                        allSuppliesAdapter = new AllSuppliesAdapter(suppliesList, reviewList, getApplicationContext());
//                        rcv_all_supplies.setAdapter(allSuppliesAdapter);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                        // Handle possible errors
//                    }
//                });
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Handle possible errors
//            }
//        });
//    }
//
//    private void filterSuppliesByChip(String chipText) {
//        List<Supplies> filteredList = new ArrayList<>();
//
//        if ("All".equals(chipText)) {
//            filteredList = suppliesList;
//        } else {
//            for (Supplies supply : suppliesList) {
//                if (chipText.equals(supply.getType())) {
//                    filteredList.add(supply);
//                }
//            }
//        }
//
//        // Cập nhật adapter
//        allSuppliesAdapter = new AllSuppliesAdapter(filteredList, reviewList, getApplicationContext());
//        rcv_all_supplies.setAdapter(allSuppliesAdapter);
//    }
//}


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
        setContentView(R.layout.activity_all_supplies);

        initWidget();

        String selectedType = getIntent().getStringExtra("selectedType");
        if (selectedType != null) {
            Log.e("Selected Type", selectedType);
            // Chọn chip tương ứng và lọc danh sách
            selectChip(selectedType.trim());
        }
    }

    void initWidget() {
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
        chipList.add("Bird");
        chipList.add("Hamster");
        chipList.add("Turtle");

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

                if (!isLoading && totalItemCount <= (lastVisibleItemPosition + 3)) {
                    loadMoreSupplies();
                }
            }
        });

        loadInitialSupplies();
    }

    private void loadInitialSupplies() {
        isLoading = true;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Supplies");
        ref.limitToFirst(10).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                suppliesList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Supplies supply = snapshot.getValue(Supplies.class);
                    suppliesList.add(supply);
                    lastLoadedKey = snapshot.getKey();
                }
                allSuppliesAdapter.notifyDataSetChanged();
                isLoading = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                isLoading = false;
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

                int previousSize = suppliesList.size();
                suppliesList.addAll(newSupplies);
                allSuppliesAdapter.notifyItemRangeInserted(previousSize, newSupplies.size());
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
                if (chipText.equals(supply.getType())) {
                    filteredList.add(supply);
                }
            }
        }

        allSuppliesAdapter = new AllSuppliesAdapter(filteredList, reviewList, getApplicationContext());
        rcv_all_supplies.setAdapter(allSuppliesAdapter);
    }

    private void selectChip(String type) {
        for (int i = 0; i < chipList.size(); i++) {
            Log.e("ChipList", "Chip at " + i + ": " + chipList.get(i));
            if (chipList.get(i).equals(type)) {
                rcv_chip.smoothScrollToPosition(i);
                ChipAdapter.ChipViewHolder viewHolder = (ChipAdapter.ChipViewHolder)
                        rcv_chip.findViewHolderForAdapterPosition(i);
                if (viewHolder != null) {
                    viewHolder.getChip().performClick(); // Sử dụng getter để lấy chip
                    Log.e("SelectChip", "Chip found and clicked at position: " + i);
                } else {
                    Log.e("SelectChip", "ViewHolder is null at position: " + i);
                }
                break;
            }
        }
    }

}
