package com.example.uiux.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiux.R;
import com.example.uiux.Utils.CurrencyFormatter;
import com.google.android.material.chip.Chip;

import java.util.List;

public class BalanceAdapter extends RecyclerView.Adapter<BalanceAdapter.BalanceViewHolder> {
    private List<String> balanceList;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(String balance);
    }

    public BalanceAdapter(List<String> balanceList, Context context) {
        this.balanceList = balanceList;
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
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
        double amount = Double.parseDouble(balanceText);
        holder.tv_item_balance.setText(CurrencyFormatter.formatCurrency(amount, holder.itemView.getContext().getString(R.string.currency_vn)));

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(balanceText);
            }
        });
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

