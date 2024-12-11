package com.example.uiux.Activities.Admin.Order;

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
import com.example.uiux.Adapters.ReturnOrderApdapter;
import com.example.uiux.Adapters.SuppliesAdapter;
import com.example.uiux.Model.InfoReturnOrder;
import com.example.uiux.Model.Supplies;
import com.example.uiux.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DisplayReturnActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ReturnOrderApdapter returnOrderApdapter;
    private List<InfoReturnOrder> infoReturnOrderList = new ArrayList<>();
    private DatabaseReference databaseReference;
    private ImageView img_back_order_return;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
        setContentView(R.layout.activity_display_return);

        img_back_order_return = findViewById(R.id.img_back_order_return);
        img_back_order_return.setOnClickListener(view -> finish());
        recyclerView = findViewById(R.id.recyclerViewRequests);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        returnOrderApdapter = new ReturnOrderApdapter(infoReturnOrderList, this);
        recyclerView.setAdapter(returnOrderApdapter);
        loadRequests();
    }

    private void loadRequests() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Return Order");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                infoReturnOrderList.clear();
                for (DataSnapshot suppSnapshot : snapshot.getChildren()) {
                    InfoReturnOrder infoReturnOrder = suppSnapshot.getValue(InfoReturnOrder.class);
                    Log.e("infoReturnOrder", infoReturnOrder.getInfo_return_id());
                    infoReturnOrderList.add(infoReturnOrder);

                }
                returnOrderApdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DisplayReturnActivity.this, "Failed to load request.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}