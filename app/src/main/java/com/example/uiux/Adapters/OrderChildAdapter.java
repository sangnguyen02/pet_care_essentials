package com.example.uiux.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uiux.Activities.User.Review.SupplyReviewActivity;
import com.example.uiux.Model.CartItem;
import com.example.uiux.Model.Supplies_Detail;
import com.example.uiux.R;
import com.example.uiux.Utils.CurrencyFormatter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrderChildAdapter extends RecyclerView.Adapter<OrderChildAdapter.OrderChildViewHolder> {

    private List<CartItem> cartPaymentItemList;
    private Context context;
    private int orderStatus;
    //private List<CartItem> selectedItems = new ArrayList<>();

    public OrderChildAdapter(Context context, List<CartItem> cartPaymentItemList, int orderStatus) {
        this.context = context;
        this.cartPaymentItemList = cartPaymentItemList;
        this.orderStatus = orderStatus;
    }

    public OrderChildAdapter(Context context, List<CartItem> cartPaymentItemList) {
        this.context = context;
        this.cartPaymentItemList = cartPaymentItemList;
    }

    @NonNull
    @Override
    public OrderChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_child, parent, false);
        return new OrderChildViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderChildViewHolder holder, int position) {
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

        if(String.valueOf(orderStatus).equals("3")) {
            holder.btn_review_supply.setVisibility(View.VISIBLE);
            holder.btn_buy_again.setVisibility(View.VISIBLE);

            holder.btn_review_supply.setOnClickListener(view -> {
                Intent goToReview = new Intent(holder.itemView.getContext(), SupplyReviewActivity.class);
                goToReview.putExtra("supplyId", cartItem.getSupply_id());
                holder.itemView.getContext().startActivity(goToReview);
            });
        }

    }

    @Override
    public int getItemCount() {
        return cartPaymentItemList.size();
    }

    public static class OrderChildViewHolder extends RecyclerView.ViewHolder {
        ImageView img_supply;
        TextView tvSupplyTitle, tvSupplySize, tvSupplyPrice, tvSupplyQuantity, tvSupplyTotalPrice;
        MaterialButton btn_review_supply, btn_buy_again;
        String accountId;
        SharedPreferences preferences;

        public OrderChildViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            tvSupplyTitle = itemView.findViewById(R.id.tv_order_child_item_title);
            tvSupplySize = itemView.findViewById(R.id.tv_order_child_item_size);
            tvSupplyPrice = itemView.findViewById(R.id.tv_order_child_item_price);
            tvSupplyQuantity = itemView.findViewById(R.id.tv_order_child_item_quantity);
            tvSupplyTotalPrice = itemView.findViewById(R.id.tv_order_child_item_total_price);
            img_supply = itemView.findViewById(R.id.img_order_child_item);
            btn_review_supply = itemView.findViewById(R.id.btn_review_supply);
            btn_buy_again = itemView.findViewById(R.id.btn_buy_again);

            preferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        }
    }
}