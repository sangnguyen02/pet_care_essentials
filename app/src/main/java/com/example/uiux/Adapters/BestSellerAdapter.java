package com.example.uiux.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uiux.Activities.SupplyDetailActivity;
import com.example.uiux.Activities.User.Profile.EditAddressActivity;
import com.example.uiux.Model.Supplies;
import com.example.uiux.Model.Supplies_Review;
import com.example.uiux.R;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;

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

     class BestSellerViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBestSeller, imgStar;
        TextView tvTitle, tvPrice, tvRating;

        public BestSellerViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBestSeller = itemView.findViewById(R.id.imgv_best_seller);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvRating = itemView.findViewById(R.id.tv_rating);
//            imgStar = itemView.findViewById(R.id.imgv_star);

            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                Supplies supplies = suppliesList.get(position);
                Intent intent = new Intent(context, SupplyDetailActivity.class);
                intent.putExtra("supply_id", supplies.getSupplies_id());
                context.startActivity(intent);
            });
        }

         public void bind(Supplies supplies) {
             tvTitle.setText(supplies.getName() != null ? supplies.getName() : "N/A");

             // DecimalFormat to remove decimals and trailing zeros
             DecimalFormat df = new DecimalFormat("#");

             // Parse the sell price as a double
             double sellPrice = Double.valueOf(Objects.requireNonNull(supplies.getSell_price()));

             // Set the formatted price
             tvPrice.setText(df.format(sellPrice));

             // Set rating
             tvRating.setText("5");

             // Get the list of image URLs
             List<String> imageUrls = supplies.getImageUrls();

             if (imageUrls != null && !imageUrls.isEmpty()) {
                 // Load the first image if the list is not empty
                 if (imageUrls.size() > 0) {
                     Glide.with(itemView.getContext())
                             .load(imageUrls.get(0))
                             .placeholder(R.drawable.banner1)
                             .error(R.drawable.guest)
                             .into(imgBestSeller);
                 }
             } else {
                 // Set placeholder image if there are no images available
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
