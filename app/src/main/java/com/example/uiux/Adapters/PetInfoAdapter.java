package com.example.uiux.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uiux.Activities.Admin.Supplies.EditSuppliesActivity;
import com.example.uiux.Activities.User.Pet.EditPetInfoActivity;
import com.example.uiux.Model.Pet;
import com.example.uiux.Model.Supplies;
import com.example.uiux.R;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class PetInfoAdapter  extends RecyclerView.Adapter<PetInfoAdapter.PetInfoViewHolder>{
    private List<Pet> petList;
    private Context context;

    @NonNull
    @Override
    public PetInfoAdapter.PetInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pet_info, parent, false);
        return new  PetInfoAdapter.PetInfoViewHolder(view);
    }
    public PetInfoAdapter(List<Pet> petList, Context context) {
        this.petList = petList;
        this.context = context;
    }
    @Override
    public void onBindViewHolder(@NonNull PetInfoAdapter.PetInfoViewHolder holder, int position) {
        Pet pet = petList.get(position);
        holder.bind(pet);

    }

    @Override
    public int getItemCount() {
        return petList.size();
    }
    class PetInfoViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView petImage;
        private TextView petName ,petAge,petGender, petType, petColor,petBreed;
        String accountId;
        SharedPreferences preferences;
        public PetInfoViewHolder(@NonNull View itemView)
        {
            super(itemView);
            petImage=itemView.findViewById(R.id.img_pet);
            petName=itemView.findViewById(R.id.pet_name);
//            petAge=itemView.findViewById(R.id.pet_age);
//            petGender=itemView.findViewById(R.id.pet_gender);
//            petType=itemView.findViewById(R.id.pet_type);
//            petColor=itemView.findViewById(R.id.pet_color);
//            petBreed=itemView.findViewById(R.id.pet_breed);
            preferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            accountId = preferences.getString("accountID", null);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Pet pet = petList.get(position);
                    // Tạo một AlertDialog với 2 lựa chọn: Edit và Delete
                    new AlertDialog.Builder(context)
                            .setItems(new CharSequence[]{"Edit", "Delete"}, (dialog, which) -> {
                                if (which == 0) {
                                    Intent intent = new Intent(context, EditPetInfoActivity.class);
                                    intent.putExtra("pet_id", pet.getPet_id());
                                    context.startActivity(intent);

                                } else if (which == 1) {
                                    // Nếu người dùng chọn "Xóa"
                                    new AlertDialog.Builder(context)
                                            .setTitle("Confirm")
                                            .setMessage("Are you sure")
                                            .setPositiveButton("Yes", (confirmDialog, confirmWhich) -> {
                                                deletePetInfoFromDatabase(pet.getPet_id());
                                                petList.remove(position);
                                                notifyItemRemoved(position);
                                                notifyItemRangeChanged(position, petList.size());
                                                Toast.makeText(context, "Delete successfully", Toast.LENGTH_SHORT).show();
                                            })
                                            .setNegativeButton("No", (confirmDialog, confirmWhich) -> {
                                                // Nếu người dùng chọn "Không", đóng dialog
                                                confirmDialog.dismiss();
                                            })
                                            .show();
                                }
                            })
                            .show();
                }
            });



        }

        public void bind(Pet pet) {
            petName.setText(pet.getPet_name() != null ? pet.getPet_name() : "N/A");
//            petAge.setText(String.valueOf(pet.getAge()));
//            petGender.setText(String.valueOf(pet.getGender()));
//            petType.setText(pet.getPet_type() != null ? pet.getPet_type() : "N/A");
//            petColor.setText(pet.getColor() != null ? pet.getColor() : "N/A");
//            petBreed.setText(pet.getPet_breed() != null ? pet.getPet_breed() : "N/A");


            List<String> imageUrls = pet.getImageUrls();
            if (imageUrls != null && !imageUrls.isEmpty()) {
                // Load the first image
                if (imageUrls.size() > 0) {
                    Glide.with(itemView.getContext())
                            .load(imageUrls.get(0))
                            .placeholder(R.drawable.product_sample)
                            .error(R.drawable.product_sample)
                            .into(petImage);
                }

            } else {
                // Set placeholders if no images are available
                petImage.setImageResource(R.drawable.product_sample);
            }
        }

        private void deletePetInfoFromDatabase(String pet_id) {
            FirebaseDatabase.getInstance().getReference("Pet").child(accountId).child(pet_id).removeValue();
        }
    }
}
