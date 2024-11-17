package com.example.uiux.Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uiux.Activities.User.Pet.PetInfoActivity;
import com.example.uiux.Model.Pet;
import com.example.uiux.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PetProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_PET = 0;
    private static final int VIEW_TYPE_ADD_BUTTON = 1;
    private List<Pet> petList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Pet pet);
    }

    public PetProfileAdapter(List<Pet> petList, OnItemClickListener listener) {
        this.petList = petList;
        this.listener = listener;
    }

//    @NonNull
//    @Override
//    public PetProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pet_info, parent, false);
//        return new PetProfileViewHolder(view);
//    }

    @Override
    public int getItemViewType(int position) {
        return petList.get(position).getPet_id().equals("add_button") ? VIEW_TYPE_ADD_BUTTON : VIEW_TYPE_PET;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ADD_BUTTON) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_pet_button, parent, false);
            return new AddButtonViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pet_info, parent, false);
            return new PetProfileViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_ADD_BUTTON) {
            ((AddButtonViewHolder) holder).bind();
        } else {
            Pet pet = petList.get(position);
            ((PetProfileViewHolder) holder).bind(pet);
            holder.itemView.setOnClickListener(v -> listener.onItemClick(pet));
        }
    }

    @Override
    public int getItemCount() {
        return petList.size();
    }

    public static class PetProfileViewHolder extends RecyclerView.ViewHolder {
        CircleImageView petImage;
        TextView petName;

        public PetProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            petImage = itemView.findViewById(R.id.img_pet);
            petName = itemView.findViewById(R.id.pet_name);
        }

        public void bind(Pet pet) {
            petName.setText(pet.getPet_name() != null ? pet.getPet_name() : "N/A");

            List<String> imageUrls = pet.getImageUrls();
            if (imageUrls != null && !imageUrls.isEmpty()) {
                // Load the first image
                if (imageUrls.size() > 0) {
                    Glide.with(itemView.getContext())
                            .load(imageUrls.get(0))
                            .placeholder(R.drawable.pet_sample)
                            .error(R.drawable.pet_sample)
                            .into(petImage);
                }

            } else {
                // Set placeholders if no images are available
                petImage.setImageResource(R.drawable.pet_sample);
            }
        }
    }

    public class AddButtonViewHolder extends RecyclerView.ViewHolder {
        public AddButtonViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(itemView.getContext(), PetInfoActivity.class);
                itemView.getContext().startActivity(intent);
            });
        }

        public void bind() {
            // Không cần binding cho nút thêm
        }
    }
}

