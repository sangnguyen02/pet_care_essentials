package com.example.uiux.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uiux.R;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class SupplyImageListAdapter extends RecyclerView.Adapter<SupplyImageListAdapter.SupplyImageListViewHolder> {

    private List<String> imgUrls;
    private Context context;
    private OnImageClickListener imageClickListener;
    private int selectedPosition = -1;

    public SupplyImageListAdapter(List<String> imgUrls, Context context) {
        this.imgUrls = imgUrls;
        this.context = context;
    }

    // Define the interface
    public interface OnImageClickListener {
        void onImageClick(String imageUrl);
    }

    // Set the image click listener
    public void setImageClickListener(OnImageClickListener imageClickListener) {
        this.imageClickListener = imageClickListener; // Assign the listener to the non-static field
    }

    @NonNull
    @Override
    public SupplyImageListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_supply_img_list, parent, false);
        return new SupplyImageListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SupplyImageListAdapter.SupplyImageListViewHolder holder, int position) {
        String supplyImgUrl = imgUrls.get(position);
        holder.bind(supplyImgUrl, position);
    }

    @Override
    public int getItemCount() {
        return imgUrls.size();
    }

    public class SupplyImageListViewHolder extends RecyclerView.ViewHolder {
        ImageView img_supply_detail;
        MaterialCardView mcv_img_child;

        public SupplyImageListViewHolder(@NonNull View itemView) {
            super(itemView);
            img_supply_detail = itemView.findViewById(R.id.img_child);
            mcv_img_child = itemView.findViewById(R.id.mcv_img_child);
        }

        public void bind(String suppliesImgUrl, int position) {
            if (suppliesImgUrl != null && !suppliesImgUrl.isEmpty()) {
                // Load image using Glide
                Glide.with(itemView.getContext())
                        .load(suppliesImgUrl)
                        .error(R.drawable.guest)
                        .into(img_supply_detail);

                if (position == selectedPosition) {
                    mcv_img_child.setStrokeColor(ContextCompat.getColor(context, R.color.tabLayout_background));
                } else {
                    mcv_img_child.setStrokeColor(ContextCompat.getColor(context, R.color.light_grey));
                }
            } else {
                img_supply_detail.setImageResource(R.drawable.product_sample);
                mcv_img_child.setStrokeColor(ContextCompat.getColor(context, R.color.light_grey));
            }



            // Set OnClickListener to handle image click
            itemView.setOnClickListener(view -> {
                // Update selected position
                int previousPosition = selectedPosition;
                selectedPosition = position;

                // Notify the adapter to refresh the previously selected and currently selected items
                notifyItemChanged(previousPosition);
                notifyItemChanged(selectedPosition);

                if (imageClickListener != null) {
                    imageClickListener.onImageClick(suppliesImgUrl);
                }
            });
        }
    }
}
