package com.example.uiux.Activities.Admin.Discount;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiux.Activities.Admin.Category.CategoryActivity;
import com.example.uiux.Adapters.DiscountAdapter;
import com.example.uiux.Model.Discount;
import com.example.uiux.R;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UpdateDiscountActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DiscountAdapter discountAdapter;
    private ImageView img_back_my_discount, imgv_add_discount;
    private MaterialCardView mcv_add_categories;
    private List<Discount> discountList = new ArrayList<>();
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
        setContentView(R.layout.activity_update_discount);

        initWidget();

        img_back_my_discount.setOnClickListener(view -> finish());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        discountAdapter = new DiscountAdapter(discountList, this);
        recyclerView.setAdapter(discountAdapter);
        loadDiscount();

        imgv_add_discount.setOnClickListener(view -> {
            Intent gotoAdd=new Intent(UpdateDiscountActivity.this, DiscountActivity.class);
            startActivity(gotoAdd);
        });

    }

    void initWidget() {
        img_back_my_discount = findViewById(R.id.img_back_my_discount);
        recyclerView = findViewById(R.id.rcv_my_categories);
        imgv_add_discount = findViewById(R.id.imgv_add_discount);
    }

    private void loadDiscount() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Discount");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                discountList.clear();
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    Discount discount = categorySnapshot.getValue(Discount.class);
                    discountList.add(discount);
                }
                discountAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateDiscountActivity.this, "Failed to load category.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}