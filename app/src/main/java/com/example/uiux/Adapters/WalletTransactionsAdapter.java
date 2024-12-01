package com.example.uiux.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uiux.Activities.SupplyDetailActivity;
import com.example.uiux.Model.Supplies;
import com.example.uiux.Model.Supplies_Review;
import com.example.uiux.Model.WalletHistory;
import com.example.uiux.R;
import com.example.uiux.Utils.CurrencyFormatter;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class WalletTransactionsAdapter extends RecyclerView.Adapter<WalletTransactionsAdapter.WalletTransactionsViewHolder> {

    private List<WalletHistory> transactionList;
    private Context context;

    public WalletTransactionsAdapter(List<WalletHistory> transactionList, Context context) {
        this.transactionList = transactionList;
        this.context = context;
    }

    @NonNull
    @Override
    public WalletTransactionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wallet_transactions, parent, false);
        return new WalletTransactionsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WalletTransactionsAdapter.WalletTransactionsViewHolder holder, int position) {
        WalletHistory walletHistory = transactionList.get(position);
        holder.bind(walletHistory);
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

     class WalletTransactionsViewHolder extends RecyclerView.ViewHolder {
        ImageView img_wallet_transaction_status;
        TextView tv_wallet_transaction_message, tv_wallet_transaction_date;

        public WalletTransactionsViewHolder(@NonNull View itemView) {
            super(itemView);
            img_wallet_transaction_status = itemView.findViewById(R.id.img_wallet_transaction_status);
            tv_wallet_transaction_message = itemView.findViewById(R.id.tv_wallet_transaction_message);
            tv_wallet_transaction_date = itemView.findViewById(R.id.tv_wallet_transaction_date);
        }
         public void bind(WalletHistory walletHistory) {
             String status =  walletHistory.getStatus();
             tv_wallet_transaction_message.setText(walletHistory.getMessage() != null ? walletHistory.getMessage() : "N/A");

             tv_wallet_transaction_date.setText(walletHistory.getDate());


             if (status.equals("+")) {
                 Glide.with(itemView.getContext())
                         .load(R.drawable.cashin)
                         .placeholder(R.drawable.logo)
                         .error(R.drawable.logo)
                         .into(img_wallet_transaction_status);
             } else {
                 Glide.with(itemView.getContext())
                         .load(R.drawable.cashout)
                         .placeholder(R.drawable.logo)
                         .error(R.drawable.logo)
                         .into(img_wallet_transaction_status);
             }
         }


     }
}
