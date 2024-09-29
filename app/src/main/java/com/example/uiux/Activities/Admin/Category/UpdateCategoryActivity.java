package com.example.uiux.Activities.Admin.Category;

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

import com.example.uiux.Activities.User.Profile.AddressActivity;
import com.example.uiux.Activities.User.Profile.UpdateAddressActivity;
import com.example.uiux.Adapters.AddressAdapter;
import com.example.uiux.Adapters.CategoryAdminAdapter;
import com.example.uiux.Model.Account_Address;
import com.example.uiux.Model.Category;
import com.example.uiux.R;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UpdateCategoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CategoryAdminAdapter categoryAdminAdapter;
    private ImageView imgv_back_add_categories;
    private MaterialCardView mcv_add_categories;
    private List<Category> categoryList = new ArrayList<>();
    private DatabaseReference databaseReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.transparent));
        setContentView(R.layout.activity_update_category);

        initWidget();

        imgv_back_add_categories.setOnClickListener(view -> finish());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        categoryAdminAdapter = new CategoryAdminAdapter(categoryList, this);
        recyclerView.setAdapter(categoryAdminAdapter);
        loadCategory();

        mcv_add_categories.setOnClickListener(view -> {
            Intent gotoAdd=new Intent(UpdateCategoryActivity.this, CategoryActivity.class);
            startActivity(gotoAdd);
        });

    }

    void initWidget() {
        imgv_back_add_categories = findViewById(R.id.img_back_my_categories);
        recyclerView = findViewById(R.id.rcv_my_categories);
        mcv_add_categories = findViewById(R.id.mcv_add_categories);
    }

    private void loadCategory() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Category");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    Category category = categorySnapshot.getValue(Category.class);
                    categoryList.add(category);
                }
                categoryAdminAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateCategoryActivity.this, "Failed to load category.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}