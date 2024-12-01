package com.example.uiux.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiux.R;
import com.google.android.material.chip.Chip;

import java.util.List;

public class BalanceAdapter extends RecyclerView.Adapter<BalanceAdapter.BalanceViewHolder> {
    private List<String> balanceList;
    private Context context;

    public BalanceAdapter(List<String> balanceList, Context context) {
        this.balanceList = balanceList;
        this.context = context;
    }

    @NonNull
    @Override
    public BalanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_balance, parent, false);
        return new BalanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BalanceViewHolder holder, int position) {
        String balanceText = balanceList.get(position);
        holder.tv_item_balance.setText(balanceText);
    }

    @Override
    public int getItemCount() {
        return balanceList.size();
    }

    public static class BalanceViewHolder extends RecyclerView.ViewHolder {
        TextView tv_item_balance;

        public BalanceViewHolder(View itemView) {
            super(itemView);
            tv_item_balance = itemView.findViewById(R.id.tv_item_balance);
        }
    }
}

