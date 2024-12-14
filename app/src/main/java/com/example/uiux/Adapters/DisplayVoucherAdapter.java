package com.example.uiux.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

                // Kiểm tra nếu voucher chưa được chọn
                if (voucher != null) {
                    // Lưu voucher ID vào SharedPreferences
                    SharedPreferences preferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("selected_voucher", voucher.getVoucher_id()); // Lưu voucher ID
                    editor.apply(); // Áp dụng thay đổi

                    // Trả kết quả về Activity
                    Intent resultIntent = new Intent();
                    ((Activity) context).setResult(Activity.RESULT_OK, resultIntent); // Activity.RECEIVER_EXPORTED có thể không đúng
                    Log.e("Selected voucher", voucher.getVoucher_id());
                    Log.e("Finish", "Finish");

                    // Kết thúc Activity
                    ((Activity) context).finish();
                } else {
                    // Nếu không có voucher, bạn có thể xử lý ở đây (ví dụ: thông báo)
                    Log.e("Voucher", "No voucher selected");
                }
            });

        }

        public void bind(Voucher voucher) {
            // Bind data to UI elements
            voucherCode.setText("Code: " + voucher.getCode());
            category.setText("Category: " + voucher.getCategory());
            description.setText("Discount " + voucher.getDiscount_percent() + "% - Maximum " +
                    voucher.getMax_discount_amount() + "đ");
            validity.setText("From: " + voucher.getStart_date() + " to " + voucher.getEnd_date());
            minvalue.setText("Minimun Value: "+voucher.getMinimum_order_value());

        }
    }
}
