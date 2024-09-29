package com.example.uiux.Activities.Admin.Type;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiux.Activities.Admin.Category.UpdateCategoryActivity;
import com.example.uiux.Adapters.CategoryAdminAdapter;
import com.example.uiux.Adapters.TypeAdapter;
import com.example.uiux.Model.Category;
import com.example.uiux.Model.Type;
import com.example.uiux.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UpdateTypeActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TypeAdapter typeAdapter;
    private List<Type> typeList = new ArrayList<>();
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_type);
        recyclerView = findViewById(R.id.recyclerViewType);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        typeAdapter = new TypeAdapter(typeList, this);
        recyclerView.setAdapter(typeAdapter);
        loadType();
    }

    private void loadType() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Type");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                typeList.clear();
                for (DataSnapshot typeSnapshot : snapshot.getChildren()) {
                    Type type = typeSnapshot.getValue(Type.class);
                    typeList.add(type);
                    Log.e("Type add",type.getType());
                }
                typeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateTypeActivity.this, "Failed to load type.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}