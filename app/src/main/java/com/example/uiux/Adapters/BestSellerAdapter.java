package com.example.uiux.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uiux.Model.Supplies;
import com.example.uiux.Model.Supplies_Review;
import com.example.uiux.R;

import java.util.List;

public class BestSellerAdapter extends RecyclerView.Adapter<BestSellerAdapter.BestSellerViewHolder> {

    private List<Supplies> suppliesList;
    private List<Supplies_Review> reviewsList;
    private Context context;

    public BestSellerAdapter(List<Supplies> suppliesList, List<Supplies_Review> reviewsList, Context context) {
        this.suppliesList = suppliesList;
        this.reviewsList = reviewsList;
        this.context = context;
    }

    @NonNull
    @Override
    public BestSellerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_best_seller, parent, false);
        return new BestSellerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BestSellerViewHolder holder, int position) {
        Supplies supplies = suppliesList.get(position);

        // Load image from drawable
        int imageResId = R.drawable.product_sample; // Default drawable resource ID
        // You can map the supplies ID to drawable resources if needed
        switch (supplies.getSupplies_id()) {
            case "supply1":
                imageResId = R.drawable.product_sample;
                break;
            case "supply2":
                imageResId = R.drawable.product_sample;
                break;
            // Add more cases as needed
            default:
                imageResId = R.drawable.product_sample;
                break;
        }

        Glide.with(context)
                .load(imageResId)
                .placeholder(R.drawable.product_sample) // Placeholder image while loading
                .error(R.drawable.product_sample) // Error image if loading fails
                .into(holder.imgBestSeller);

        holder.tvTitle.setText(supplies.getName());
        holder.tvPrice.setText(String.format("₫ %.2f", supplies.getSell_price()));
        // Set rating
        Supplies_Review review = findReviewForSupply(supplies.getSupplies_id());
        if (review != null) {
            holder.tvRating.setText(String.valueOf(review.getRating()));
        } else {
            holder.tvRating.setText("No rating");
        }

        // Set star image resource
//        holder.imgStar.setImageResource(R.drawable.star);

    }

    @Override
    public int getItemCount() {
        return suppliesList.size();
    }

    public static class BestSellerViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBestSeller, imgStar;
        TextView tvTitle, tvPrice, tvRating;

        public BestSellerViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBestSeller = itemView.findViewById(R.id.imgv_best_seller);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvRating = itemView.findViewById(R.id.tv_rating);
//            imgStar = itemView.findViewById(R.id.imgv_star);
        }
    }

    // Method to find review for a given supply
    private Supplies_Review findReviewForSupply(String suppliesId) {
        for (Supplies_Review review : reviewsList) {
            if (review.getSupplies_id().equals(suppliesId)) {
                return review;
            }
        }
        return null;
    }
}
