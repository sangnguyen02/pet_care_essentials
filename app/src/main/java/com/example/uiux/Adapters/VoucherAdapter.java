package com.example.uiux.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uiux.Activities.Admin.Supplies.EditSuppliesActivity;
import com.example.uiux.Activities.Admin.Voucher.EditVoucherActivity;
import com.example.uiux.Model.Discount;
import com.example.uiux.Model.Supplies;
import com.example.uiux.Model.Voucher;
import com.example.uiux.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class VoucherAdapter  extends  RecyclerView.Adapter<VoucherAdapter.VoucherViewHolder>{
    private List<Voucher> voucherList;
    private Context context;
    public VoucherAdapter(List<Voucher> voucherList, Context context) {
        this.voucherList = voucherList;
        this.context = context;
    }
    @NonNull
    @Override
    public VoucherAdapter.VoucherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_voucher, parent, false);
        return new VoucherAdapter.VoucherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VoucherAdapter.VoucherViewHolder holder, int position) {
        Voucher voucher = voucherList.get(position);
        holder.bind(voucher);

    }

    @Override
    public int getItemCount() {
        return voucherList.size();
    }
    class  VoucherViewHolder extends RecyclerView.ViewHolder
    {
        private TextView category;
        private TextView code;
        private TextView remaining_quantity;
        private TextView discount_percent;
        private TextView max_discount_amount;
        private TextView voucher_quantity;
        private TextView minimun_order_value;
        private TextView status;
        private TextView start_date;
        private TextView end_date;

        public VoucherViewHolder(@NonNull View itemView) {
            super(itemView);

            category=itemView.findViewById(R.id.tv_category);
            code=itemView.findViewById(R.id.tv_code);
            remaining_quantity=itemView.findViewById(R.id.tv_remaining_quantity);
            discount_percent=itemView.findViewById(R.id.tv_discount_percent);
            max_discount_amount=itemView.findViewById(R.id.tv_max_discount_amount);
            voucher_quantity=itemView.findViewById(R.id.tv_discount_amount);
            minimun_order_value=itemView.findViewById(R.id.tv_minimum_order_value);
            status=itemView.findViewById(R.id.tv_status);
            start_date=itemView.findViewById(R.id.tv_start_date);
            end_date=itemView.findViewById(R.id.tv_end_date);

            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                Voucher voucher = voucherList.get(position);

                Intent intent = new Intent(context, EditVoucherActivity.class);
                intent.putExtra("voucher_id", voucher.getVoucher_id());
                context.startActivity(intent);
            });
        }

        public void bind(Voucher voucher) {
            category.setText(voucher.getCategory());
            code.setText(voucher.getCode());
            remaining_quantity.setText(String.valueOf(voucher.getRemaining_quantity()));
            discount_percent.setText(String.valueOf(voucher.getDiscount_percent()));
            max_discount_amount.setText(String.valueOf(voucher.getMax_discount_amount()));
            voucher_quantity.setText(String.valueOf(voucher.getDiscount_amount()));
            minimun_order_value.setText(String.valueOf(voucher.getMinimum_order_value()));
            status.setText(String.valueOf(voucher.getStatus()));
            start_date.setText(voucher.getStart_date());
            end_date.setText(voucher.getEnd_date());

        }
    }
}
