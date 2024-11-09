package com.example.uiux.Activities.User.Pet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiux.Activities.Admin.Supplies.SuppliesActivity;
import com.example.uiux.Activities.Admin.Supplies.UpdateSuppliesActivity;
import com.example.uiux.Adapters.PetInfoAdapter;
import com.example.uiux.Adapters.SuppliesAdapter;
import com.example.uiux.Model.Pet;
import com.example.uiux.Model.Supplies;
import com.example.uiux.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UpdatePetInfoActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ImageView imgv_back_add_supplies, imgv_add_supplies;
    private PetInfoAdapter petInfoAdapter;
    private List<Pet> petList = new ArrayList<>();
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_pet_info);
        imgv_back_add_supplies = findViewById(R.id.img_back_my_supplies);
        imgv_add_supplies = findViewById(R.id.imgv_add_supplies);

        imgv_back_add_supplies.setOnClickListener(view -> {
            finish();
        });

        imgv_add_supplies.setOnClickListener(view -> {
            Intent intent = new Intent(UpdatePetInfoActivity.this, PetInfoActivity.class);
            startActivity(intent);
        });

        recyclerView = findViewById(R.id.recyclerViewSupplies);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        petInfoAdapter = new PetInfoAdapter(petList, this);
        recyclerView.setAdapter(petInfoAdapter);
        loadPets();
    }
    private void loadPets() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Pet");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                petList.clear();
                for (DataSnapshot suppSnapshot : snapshot.getChildren()) {
                    Pet pet = suppSnapshot.getValue(Pet.class);
                    petList.add(pet);
                    //Log.e("Supplies add",supplies.getName());
                }
                petInfoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdatePetInfoActivity.this, "Failed to load Pet.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}