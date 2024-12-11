package com.example.uiux.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uiux.Activities.User.CancelOrder.UserCancelOrderActivity;
import com.example.uiux.Activities.User.ReturnOrder.UserReturnOrderActivity;
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
//public OrderChildAdapter(Context context, List<CartItem> cartPaymentItemList) {
//    this.context = context;
//    this.cartPaymentItemList = cartPaymentItemList;
//}

public class OrderChildAdapter extends RecyclerView.Adapter<OrderChildAdapter.OrderChildViewHolder> {

    public List<CartItem> cartPaymentItemList;
    private Context context;
    private int orderStatus;
    private String order_id;  // Lưu trữ order_id

    public OrderChildAdapter(Context context, List<CartItem> cartPaymentItemList, int orderStatus, String order_id) {
        this.context = context;
        this.cartPaymentItemList = cartPaymentItemList;
        this.orderStatus = orderStatus;
        this.order_id = order_id;  // Truyền order_id khi khởi tạo adapter
    }
    public OrderChildAdapter(Context context, List<CartItem> cartPaymentItemList) {
    this.context = context;
    this.cartPaymentItemList = cartPaymentItemList;
}

    // public OrderChildAdapter(Context context, List<CartItem> cartPaymentItemList, int orderStatus, String orderId) {
    //     this.context = context;
    //     this.cartPaymentItemList = cartPaymentItemList;
    //     this.orderStatus = orderStatus;
    //     this.orderId = orderId;
    // }

    @NonNull
    @Override
    public OrderChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_child, parent, false);
        return new OrderChildViewHolder(view, context, order_id); // Truyền order_id vào ViewHolder
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

        if (String.valueOf(orderStatus).equals("3")) {
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
        MaterialButton btn_review_supply, btn_buy_again, btn_cancel,btn_return;
        SharedPreferences preferences;
        String order_id;  // Lưu trữ order_id trong ViewHolder

        // Cập nhật constructor để nhận order_id từ adapter
        public OrderChildViewHolder(@NonNull View itemView, Context context, String order_id) {
            super(itemView);
            tvSupplyTitle = itemView.findViewById(R.id.tv_order_child_item_title);
            tvSupplySize = itemView.findViewById(R.id.tv_order_child_item_size);
            tvSupplyPrice = itemView.findViewById(R.id.tv_order_child_item_price);
            tvSupplyQuantity = itemView.findViewById(R.id.tv_order_child_item_quantity);
            tvSupplyTotalPrice = itemView.findViewById(R.id.tv_order_child_item_total_price);
            img_supply = itemView.findViewById(R.id.img_order_child_item);
            btn_review_supply = itemView.findViewById(R.id.btn_review_supply);
            btn_buy_again = itemView.findViewById(R.id.btn_buy_again);
//            btn_cancel = itemView.findViewById(R.id.btn_cancel);
//            btn_return= itemView.findViewById(R.id.btn_return);

            preferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            this.order_id = order_id; // Nhận order_id từ constructor

            // Sự kiện cho nút "Cancel"
//            btn_cancel.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    // Khi nút Cancel được nhấn, mở UserCancelOrderActivity với order_id
//                    Intent intent = new Intent(context, UserCancelOrderActivity.class);
//                    intent.putExtra("order_id", order_id); // Truyền order_id vào Intent
//                    Log.e("Order_ID", order_id);  // Ghi log để kiểm tra order_id
//                    context.startActivity(intent); // Mở Activity mới
//                }
//            });
//            btn_return.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent(context, UserReturnOrderActivity.class);
//                    intent.putExtra("order_id", order_id); // Truyền order_id vào Intent
//                    Log.e("Order_ID", order_id);  // Ghi log để kiểm tra order_id
//                    context.startActivity(intent); // Mở Activity mới
//
//                }
//            });
        }
    }
}
