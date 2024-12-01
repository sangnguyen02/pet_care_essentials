package com.example.uiux.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiux.Activities.User.Review.SupplyReviewActivity;
import com.example.uiux.Model.Order;
import com.example.uiux.R;
import com.example.uiux.Utils.CurrencyFormatter;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<Order> orders;
    private Context context;

    public OrderAdapter(List<Order> orders, Context context) {
        this.orders = orders;
        this.context = context;
    }

    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        Log.e("ORDER", "Binding order: " + order.getOrder_id());
        OrderChildAdapter orderChildAdapter = new OrderChildAdapter(holder.itemView.getContext(), order.getCart_items_ordered(), order.getStatus());
        holder.rcv_order_child.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext(), RecyclerView.VERTICAL, false));
        holder.rcv_order_child.setAdapter(orderChildAdapter);

        holder.label_total_items.setText("Total items: " + String.valueOf(getItemCount()));
        holder.tv_order_item_total_price.setText(CurrencyFormatter.formatCurrency(order.getTotal_price(), context.getString(R.string.currency_vn)));

    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        RecyclerView rcv_order_child;
        TextView label_total_items, tv_order_item_total_price;

        //MaterialButton btn_review_supply, btn_buy_again;

        public OrderViewHolder(View itemView) {
            super(itemView);
            rcv_order_child = itemView.findViewById(R.id.rcv_order_child);
            label_total_items = itemView.findViewById(R.id.label_total_items);
            tv_order_item_total_price = itemView.findViewById(R.id.tv_order_item_total_price);
//            btn_review_supply = itemView.findViewById(R.id.btn_review_supply);
//            btn_buy_again = itemView.findViewById(R.id.btn_buy_again);
        }
    }
}

