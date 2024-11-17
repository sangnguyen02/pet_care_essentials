package com.example.uiux.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uiux.Model.CartItem;
import com.example.uiux.Model.Supplies_Detail;
import com.example.uiux.R;
import com.example.uiux.Utils.CurrencyFormatter;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CartPaymentAdapter extends RecyclerView.Adapter<CartPaymentAdapter.CartPaymentViewHolder> {

    private List<CartItem> cartPaymentItemList;
    private Context context;
    //private List<CartItem> selectedItems = new ArrayList<>();

    public CartPaymentAdapter(Context context, List<CartItem> cartPaymentItemList) {
        this.context = context;
        this.cartPaymentItemList = cartPaymentItemList;
    }

    @NonNull
    @Override
    public CartPaymentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_payment, parent, false);
        return new CartPaymentViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull CartPaymentViewHolder holder, int position) {
        CartItem cartItem = cartPaymentItemList.get(position);
        holder.tvSupplyTitle.setText(cartItem.getSupply_title());
        holder.tvSupplySize.setText(cartItem.getSupply_size());
        holder.tvSupplyPrice.setText(CurrencyFormatter.formatCurrency(cartItem.getSupply_price(), context.getString(R.string.currency_vn)));
        holder.tvSupplyQuantity.setText(String.valueOf(cartItem.getQuantity()));
        holder.tvSupplyTotalPrice.setText(CurrencyFormatter.formatCurrency(cartItem.getTotalPrice(), context.getString(R.string.currency_vn)));

        String imageUrls = cartItem.getImageUrl();
        if (imageUrls != null && !imageUrls.isEmpty()) {
            Glide.with(context)
                    .load(imageUrls)
                    .error(R.drawable.product_sample)
                    .into(holder.img_supply);
        } else {
            holder.img_supply.setImageResource(R.drawable.product_sample);
        }

    }

    @Override
    public int getItemCount() {
        return cartPaymentItemList.size();
    }

    public static class CartPaymentViewHolder extends RecyclerView.ViewHolder {
        ImageView img_supply;
        TextView tvSupplyTitle, tvSupplySize, tvSupplyPrice, tvSupplyQuantity, tvSupplyTotalPrice;
        String accountId;
        SharedPreferences preferences;

        public CartPaymentViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            tvSupplyTitle = itemView.findViewById(R.id.tv_cart_payment_item_title);
            tvSupplySize = itemView.findViewById(R.id.tv_cart_payment_item_size);
            tvSupplyPrice = itemView.findViewById(R.id.tv_cart_payment_item_price);
            tvSupplyQuantity = itemView.findViewById(R.id.tv_cart_payment_item_quantity);
            tvSupplyTotalPrice = itemView.findViewById(R.id.tv_cart_payment_item_total_price);
            img_supply = itemView.findViewById(R.id.img_cart_payment_item);

            preferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        }
    }
}