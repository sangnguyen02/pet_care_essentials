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
import com.example.uiux.Model.Supplies;
import com.example.uiux.Model.Supplies_Review;
import com.example.uiux.R;
import com.example.uiux.Utils.CurrencyFormatter;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AllSuppliesAdapter extends RecyclerView.Adapter<AllSuppliesAdapter.AllSuppliesViewHolder> {

    private List<Supplies> suppliesList;
    private List<Supplies_Review> reviewsList;
    private Context context;

    public AllSuppliesAdapter(List<Supplies> suppliesList, List<Supplies_Review> reviewsList, Context context) {
        this.suppliesList = suppliesList;
        this.reviewsList = reviewsList;
        this.context = context;
    }

    @NonNull
    @Override
    public AllSuppliesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_all_supplies, parent, false);
        return new AllSuppliesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllSuppliesAdapter.AllSuppliesViewHolder holder, int position) {
        Supplies supplies = suppliesList.get(position);
        holder.bind(supplies);
    }

    @Override
    public int getItemCount() {
        return suppliesList.size();
    }

     class AllSuppliesViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAllSupplies;
        TextView tvTitle, tvPrice, tvRating;

        public AllSuppliesViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAllSupplies = itemView.findViewById(R.id.imgv_all_supplies);
            tvTitle = itemView.findViewById(R.id.tv_all_supplies_title);
            tvPrice = itemView.findViewById(R.id.tv_all_supplies_price);
            tvRating = itemView.findViewById(R.id.tv_all_supplies_rating);

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

             DecimalFormat df = new DecimalFormat("#");
             double sellPrice = Double.valueOf(Objects.requireNonNull(supplies.getSell_price()));
             tvPrice.setText(CurrencyFormatter.formatCurrency(sellPrice, context.getString(R.string.currency_vn)));

             float averageRating = calculateAverageRating(supplies.getSupplies_id());
             if (averageRating > 0) {
                 tvRating.setText(String.format(Locale.US, "%.1f", averageRating));
             } else {
                 tvRating.setText("N/A");
             }

             List<String> imageUrls = supplies.getImageUrls();
             if (imageUrls != null && !imageUrls.isEmpty()) {
                 Glide.with(itemView.getContext())
                         .load(imageUrls.get(0))
                         .placeholder(R.drawable.banner1)
                         .error(R.drawable.guest)
                         .into(imgAllSupplies);
             } else {
                 imgAllSupplies.setImageResource(R.drawable.product_sample);
             }
         }


     }

    private float calculateAverageRating(String suppliesId) {
        float totalRating = 0;
        int reviewCount = 0;

        for (Supplies_Review review : reviewsList) {
            if (review.getSupplies_id().equals(suppliesId)) {
                totalRating += review.getRating();
                reviewCount++;
            }
        }

        return reviewCount > 0 ? totalRating / reviewCount : 0;
    }


}
