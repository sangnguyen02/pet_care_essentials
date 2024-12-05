package com.example.uiux.Activities.User.Pet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
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

import com.bumptech.glide.Glide;
import com.example.uiux.Activities.Admin.Supplies.SuppliesActivity;
import com.example.uiux.Activities.Admin.Supplies.UpdateSuppliesActivity;
import com.example.uiux.Activities.AllSuppliesActivity;
import com.example.uiux.Adapters.PetInfoAdapter;
import com.example.uiux.Adapters.PetProfileAdapter;
import com.example.uiux.Adapters.SuppliesAdapter;
import com.example.uiux.Model.Pet;
import com.example.uiux.Model.Supplies;
import com.example.uiux.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UpdatePetInfoActivity extends AppCompatActivity {
    private RecyclerView rcv_pet_profile;
    private ImageView imgv_back_add_pet_profile, img_pet_profile;
    private TextView tv_pet_name, tv_pet_gender, tv_pet_age, tv_pet_weight, tv_pet_type, tv_pet_breed;
    private MaterialButton btn_view_recommend_supplies;
    private PetProfileAdapter petProfileAdapter;
    private List<Pet> petList = new ArrayList<>();
    private Pet selectedPet;
    private DatabaseReference databaseReference;
    String accountId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        setContentView(R.layout.activity_update_pet_info);

        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        accountId = preferences.getString("accountID", null);

        initWidget();



    }

    void initWidget() {
        tv_pet_name = findViewById(R.id.tv_pet_name);
        tv_pet_gender = findViewById(R.id.tv_pet_gender);
        tv_pet_age = findViewById(R.id.tv_pet_age);
        tv_pet_weight = findViewById(R.id.tv_pet_weight);
        tv_pet_type = findViewById(R.id.tv_pet_type);
        tv_pet_breed = findViewById(R.id.tv_pet_breed);
        img_pet_profile = findViewById(R.id.img_pet_profile);
        imgv_back_add_pet_profile = findViewById(R.id.img_back_my_pet_profile);

        imgv_back_add_pet_profile.setOnClickListener(view -> {
            finish();
        });

        img_pet_profile.setOnClickListener(view -> {
            if (selectedPet != null) {
                Intent intent = new Intent(UpdatePetInfoActivity.this, EditPetInfoActivity.class);
                intent.putExtra("pet_id", selectedPet.getPet_id());
                startActivity(intent);
            }

        });

        btn_view_recommend_supplies = findViewById(R.id.btn_view_recommend_supplies);
        btn_view_recommend_supplies.setOnClickListener(view -> {
            if (selectedPet != null && selectedPet.getPet_type() != null) {
                Intent intent = new Intent(UpdatePetInfoActivity.this, AllSuppliesActivity.class);
                intent.putExtra("selectedType", selectedPet.getPet_type());
                startActivity(intent);
            } else {
                Toast.makeText(this, "No pet type selected!", Toast.LENGTH_SHORT).show();
            }
        });

        loadPets();

        rcv_pet_profile = findViewById(R.id.rcv_pet_profile);
        rcv_pet_profile.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        petProfileAdapter = new PetProfileAdapter(petList, pet -> {
            selectedPet = pet;
            displayPetDetails(pet);
        });
        rcv_pet_profile.setAdapter(petProfileAdapter);
    }

    private void loadPets() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Pet").child(accountId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                petList.clear();
                for (DataSnapshot suppSnapshot : snapshot.getChildren()) {
                    Pet pet = suppSnapshot.getValue(Pet.class);
                    petList.add(pet);
                }
                petList.add(new Pet("add_button"));
                petProfileAdapter.notifyDataSetChanged();

                // Display first pet's info if available
                if (!petList.isEmpty() && !petList.get(0).getPet_id().equals("add_button")) {
                    selectedPet = petList.get(0);
                    displayPetDetails(petList.get(0));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdatePetInfoActivity.this, "Failed to load Pet.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayPetDetails(Pet pet) {
        tv_pet_name.setText(pet.getPet_name() != null ? pet.getPet_name() : "N/A");
        tv_pet_gender.setText(pet.getGender() != null ? pet.getGender() : "N/A");
        tv_pet_age.setText(String.valueOf(pet.getAge()));
        tv_pet_weight.setText(String.valueOf(pet.getWeight()) + " kg");
        tv_pet_type.setText(pet.getPet_type() != null ? pet.getPet_type() : "N/A");
        tv_pet_breed.setText(pet.getPet_breed() != null ? pet.getPet_breed() : "N/A");

        // Load pet image
        if (pet.getImageUrls() != null && !pet.getImageUrls().isEmpty()) {
            Glide.with(this)
                    .load(pet.getImageUrls().get(0))
                    .placeholder(R.drawable.pet_sample)
                    .error(R.drawable.pet_sample)
                    .into(img_pet_profile);
        } else {
            img_pet_profile.setImageResource(R.drawable.pet_sample);
        }
    }
}