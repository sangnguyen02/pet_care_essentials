package com.example.uiux.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uiux.Model.Discount;
import com.example.uiux.Model.Service;
import com.example.uiux.R;

import java.util.List;

public class DiscountAdapter extends  RecyclerView.Adapter<DiscountAdapter.DiscountViewHolder>{

    private List<Discount> discountList;
    private Context context;
    public DiscountAdapter(List<Discount> discountList, Context context) {
        this.discountList = discountList;
        this.context = context;
    }

    @NonNull
    @Override
    public DiscountAdapter.DiscountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_discount, parent, false);
        return new DiscountAdapter.DiscountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiscountAdapter.DiscountViewHolder holder, int position) {
        Discount discount = discountList.get(position);
        holder.bind(discount);

    }

    @Override
    public int getItemCount() {
        return discountList.size();
    }
    class DiscountViewHolder extends RecyclerView.ViewHolder
    {
        private TextView category,discount_percent,status,start_date,end_date;

        public DiscountViewHolder(@NonNull View itemView) {
            super(itemView);
            category=itemView.findViewById(R.id.tv_category);
            discount_percent=itemView.findViewById(R.id.tv_percent);
            status=itemView.findViewById(R.id.tv_status);
            start_date=itemView.findViewById(R.id.tv_startDate);
            end_date=itemView.findViewById(R.id.tv_endDate);
        }

        public void bind(Discount discount) {
            category.setText(discount.getCategory() != null ? discount.getCategory() : "N/A");
            discount_percent.setText(String.valueOf(discount.getDiscount_percent()));

            start_date.setText(discount.getStart_date());
            end_date.setText(discount.getEnd_date());
            status.setText(String.valueOf(discount.getStatus()));


        }
    }
}
