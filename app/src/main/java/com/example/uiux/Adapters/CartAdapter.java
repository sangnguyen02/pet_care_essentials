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
import com.example.uiux.Model.CartItem;
import com.example.uiux.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItemList;
    private Context context;
    private List<CartItem> selectedItems = new ArrayList<>();

    public CartAdapter(Context context, List<CartItem> cartItemList) {
        this.context = context;
        this.cartItemList = cartItemList;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItemList.get(position);
        holder.tvSupplyTitle.setText(cartItem.getSupply_title());
        holder.tvSupplySize.setText(cartItem.getSupply_size());
        holder.tvSupplyPrice.setText(String.valueOf(cartItem.getSupply_price()));
        holder.tvTotalPrice.setText(String.valueOf(cartItem.getTotalPrice()));

        // Set up checkbox listener to track selected items
        holder.checkBoxSelectItem.setOnCheckedChangeListener(null);
        holder.checkBoxSelectItem.setChecked(selectedItems.contains(cartItem));
        holder.checkBoxSelectItem.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedItems.add(cartItem);
            } else {
                selectedItems.remove(cartItem);
            }
        });

        holder.mcv_cart_item.setOnClickListener(v -> {
            boolean newState = !holder.checkBoxSelectItem.isChecked();
            holder.checkBoxSelectItem.setChecked(newState);
        });

        String imageUrls = cartItem.getImageUrl();
        if (imageUrls != null && !imageUrls.isEmpty()) {
            Glide.with(context)
                    .load(imageUrls)
                    .error(R.drawable.guest)
                    .into(holder.img_supply);
        } else {
            holder.img_supply.setImageResource(R.drawable.product_sample);
        }


    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public List<CartItem> getSelectedItems() {
        return selectedItems;
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView mcv_cart_item, mcv_cart_minus, mcv_cart_plus;
        ImageView img_supply;
        TextView tvSupplyTitle, tvSupplySize, tvSupplyPrice, tvTotalPrice, tvSupplyQuantity;
        MaterialCheckBox checkBoxSelectItem;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            mcv_cart_item = itemView.findViewById(R.id.mcv_cart_item);
            mcv_cart_plus = itemView.findViewById(R.id.mcv_cart_plus);
            mcv_cart_minus = itemView.findViewById(R.id.mcv_cart_minus);
            tvSupplyTitle = itemView.findViewById(R.id.tv_cart_item_title);
            tvSupplySize = itemView.findViewById(R.id.tv_cart_item_size);
            tvSupplyPrice = itemView.findViewById(R.id.tv_cart_item_price);
            tvTotalPrice = itemView.findViewById(R.id.tv_cart_total_price);
            tvSupplyQuantity = itemView.findViewById(R.id.tv_cart_item_quantity);
            checkBoxSelectItem = itemView.findViewById(R.id.checkbox_cart_item);
            img_supply = itemView.findViewById(R.id.img_cart_item);


        }
    }
}

