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
    public void onBindViewHolder(@NonNull BestSellerAdapter.BestSellerViewHolder holder, int position) {
        Supplies supplies = suppliesList.get(position);
        holder.bind(supplies);
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

        public void bind(Supplies supplies) {
            tvTitle.setText(supplies.getName() != null ? supplies.getName() : "N/A");
            tvPrice.setText(String.valueOf(supplies.getSell_price()));
            tvRating.setText("5");

            List<String> imageUrls = supplies.getImageUrls();
            if (imageUrls != null && !imageUrls.isEmpty()) {
                // Load the first image
                if (imageUrls.size() > 0) {
                    Glide.with(itemView.getContext())
                            .load(imageUrls.get(0))
                            .placeholder(R.drawable.banner1)
                            .error(R.drawable.guest)
                            .into(imgBestSeller);
                }


            } else {
                // Set placeholders if no images are available
                imgBestSeller.setImageResource(R.drawable.product_sample);
            }
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
