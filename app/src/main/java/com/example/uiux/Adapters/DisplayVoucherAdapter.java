package com.example.uiux.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiux.Activities.Admin.Voucher.EditVoucherActivity;
import com.example.uiux.Model.Voucher;
import com.example.uiux.R;

import java.util.List;

public class DisplayVoucherAdapter extends RecyclerView.Adapter<DisplayVoucherAdapter.DisplayVoucherViewHolder> {

    private List<Voucher> voucherList;
    private Context context;


    public DisplayVoucherAdapter(List<Voucher> voucherList, Context context) {
        this.voucherList = voucherList;
        this.context = context;

    }

    @NonNull
    @Override
    public DisplayVoucherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate item layout
        View view = LayoutInflater.from(context).inflate(R.layout.item_display_voucher, parent, false);
        return new DisplayVoucherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DisplayVoucherViewHolder holder, int position) {
        Voucher voucher = voucherList.get(position);
        holder.bind(voucher);
    }

    @Override
    public int getItemCount() {
        return voucherList.size();
    }

    class DisplayVoucherViewHolder extends RecyclerView.ViewHolder {
        private TextView voucherCode, category, description, validity,minvalue;

        public DisplayVoucherViewHolder(@NonNull View itemView) {
            super(itemView);
            voucherCode = itemView.findViewById(R.id.tv_voucher_code);
            category = itemView.findViewById(R.id.tv_voucher_category);
            description = itemView.findViewById(R.id.tv_voucher_description);
            validity = itemView.findViewById(R.id.tv_voucher_validity);
            minvalue= itemView.findViewById(R.id.tv_voucher_minimum);

            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                Voucher voucher = voucherList.get(position);

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("selected_voucher", voucher.getVoucher_id());
                    ((Activity) context).setResult(Activity.RECEIVER_EXPORTED, resultIntent);
                    Log.e("Finish","Finish");
                    ((Activity) context).finish();

            });
        }

        public void bind(Voucher voucher) {
            // Bind data to UI elements
            voucherCode.setText("Mã: " + voucher.getCode());
            category.setText("Danh mục: " + voucher.getCategory());
            description.setText("Giảm " + voucher.getDiscount_percent() + "% - Tối đa " +
                    voucher.getMax_discount_amount() + "đ");
            validity.setText("HSD: " + voucher.getStart_date() + " đến " + voucher.getEnd_date());
            minvalue.setText("Đơn tối thiểu: "+voucher.getMinimum_order_value());

        }
    }
}
