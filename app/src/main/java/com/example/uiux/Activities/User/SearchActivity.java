package com.example.uiux.Activities.User;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiux.Adapters.SearchSuppliesAdapter;
import com.example.uiux.Model.Supplies;
import com.example.uiux.Model.SuppliesDiffCallback;
import com.example.uiux.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    SearchView main_search_view;
    ImageView img_back_search;
    RecyclerView rcv_search_results;
    TextView tv_no_results;
    private Handler handler;
    private Runnable runnable;
    private List<Supplies> suppliesList = new ArrayList<>(); // Khai báo suppliesList
    private SearchSuppliesAdapter searchSuppliesAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
        setContentView(R.layout.activity_search);
        handler = new Handler(getMainLooper());
        initWidget();

    }

    void initWidget() {
        img_back_search = findViewById(R.id.img_back_search);
        main_search_view = findViewById(R.id.main_search_view);
        tv_no_results = findViewById(R.id.tv_no_results);
        rcv_search_results = findViewById(R.id.rcv_search_results);
        rcv_search_results.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        main_search_view.requestFocus();

        img_back_search.setOnClickListener(view -> {
            finish();
            overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
        });

        main_search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchSuppliesByName(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    // clear result
                    updateRecyclerView(new ArrayList<>());
                    findViewById(R.id.tv_no_results).setVisibility(View.GONE);
                } else {
                    if (runnable != null) {
                        handler.removeCallbacks(runnable);
                    }
                    runnable = () -> searchSuppliesByName(newText);
                    // use debounce mechanism, search only after the user stops typing for a certain period.
                    handler.postDelayed(runnable, 500);
                }
                return false;
            }
        });


    }

    public void searchSuppliesByName(String query) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Supplies");
        databaseReference.orderByChild("name")
                .startAt(query)
                .endAt(query + "\uf8ff") // tìm kiếm theo kiểu chữ cái, từ 'query' đến tất cả các từ bắt đầu với 'query'
                .limitToFirst(20)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Supplies> newSuppliesList = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Supplies supply = snapshot.getValue(Supplies.class);
                            newSuppliesList.add(supply);
                        }

                        if (newSuppliesList.isEmpty()) {
                            findViewById(R.id.tv_no_results).setVisibility(View.VISIBLE);
                        } else {
                            findViewById(R.id.tv_no_results).setVisibility(View.GONE);
                        }
                        updateRecyclerView(newSuppliesList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Xử lý khi có lỗi
                    }
                });
    }

    public void updateRecyclerView(List<Supplies> newSuppliesList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new SuppliesDiffCallback(suppliesList, newSuppliesList));
        suppliesList.clear();
        suppliesList.addAll(newSuppliesList);
        if (searchSuppliesAdapter == null) {
            searchSuppliesAdapter = new SearchSuppliesAdapter(suppliesList, this);
            rcv_search_results.setAdapter(searchSuppliesAdapter);
        } else {
            diffResult.dispatchUpdatesTo(searchSuppliesAdapter);
        }
    }
}