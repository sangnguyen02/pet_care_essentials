package com.example.uiux.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiux.Model.Supplies_Detail;
import com.example.uiux.R;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class SupplyDetailOptionAdapter extends RecyclerView.Adapter<SupplyDetailOptionAdapter.SupplyOptionsViewHolder>  {
    private List<Supplies_Detail> suppliesDetailList;
    private Context context;
    private int selectedPosition = -1;

    public interface OnSupplyOptionClickListener {
        void onSupplyOptionClick(double costPrice);
    }

    private OnSupplyOptionClickListener listener;

    public SupplyDetailOptionAdapter(List<Supplies_Detail> suppliesDetailList, Context context, OnSupplyOptionClickListener listener) {
        this.suppliesDetailList = suppliesDetailList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SupplyOptionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_supply_detail_option, parent, false);
        return new SupplyOptionsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SupplyOptionsViewHolder holder, int position) {
        Supplies_Detail suppliesDetail = suppliesDetailList.get(position);
        holder.bind(suppliesDetail, position);
    }

    @Override
    public int getItemCount() {
        return suppliesDetailList.size();
    }


    public class SupplyOptionsViewHolder extends RecyclerView.ViewHolder {
        TextView tvOptionName;
        MaterialCardView mcv_option;

        public SupplyOptionsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOptionName = itemView.findViewById(R.id.tv_option_name);
            mcv_option = itemView.findViewById(R.id.mcv_option);
        }

        public void bind(Supplies_Detail suppliesDetail, int position) {
            tvOptionName.setText(suppliesDetail.getSize());

            if (position == selectedPosition) {
                mcv_option.setStrokeColor(ContextCompat.getColor(context, R.color.tabLayout_background));
                tvOptionName.setTextColor(ContextCompat.getColor(context, R.color.tabLayout_background));
            } else {
                mcv_option.setStrokeColor(ContextCompat.getColor(context, R.color.light_grey));
                tvOptionName.setTextColor(ContextCompat.getColor(context, R.color.black));
            }

            itemView.setOnClickListener(view -> {
                // Update selected position
                int previousPosition = selectedPosition;
                selectedPosition = position;

                if (listener != null) {
                    listener.onSupplyOptionClick(suppliesDetail.getCost_price());
                }

                // Notify the adapter to refresh the previously selected and currently selected items
                notifyItemChanged(previousPosition);
                notifyItemChanged(selectedPosition);

            });
        }
    }
}
