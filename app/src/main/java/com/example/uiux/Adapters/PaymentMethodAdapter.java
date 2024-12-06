package com.example.uiux.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiux.Model.PaymentMethod;
import com.example.uiux.R;
import com.google.android.material.radiobutton.MaterialRadioButton;

import java.util.List;

public class PaymentMethodAdapter extends RecyclerView.Adapter<PaymentMethodAdapter.PaymentMethodViewHolder> {
    private Context context;
    private List<PaymentMethod> paymentMethods;
    private int selectedPosition = 0;

    public PaymentMethodAdapter(Context context, List<PaymentMethod> paymentMethods) {
        this.context = context;
        this.paymentMethods = paymentMethods;
    }

    @NonNull
    @Override
    public PaymentMethodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_payment_method, parent, false);
        return new PaymentMethodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentMethodViewHolder holder, int position) {
        PaymentMethod paymentMethod = paymentMethods.get(position);

        holder.textView.setText(paymentMethod.getTitle());
        holder.imageView.setImageResource(paymentMethod.getIconResId());

        holder.radioButton.setChecked(position == selectedPosition);

        // Handle clicks
        holder.radioButton.setOnClickListener(v -> {
            int previousSelectedPosition = selectedPosition;
            selectedPosition = holder.getBindingAdapterPosition();

            // Notify changes only for the affected items
            notifyItemChanged(previousSelectedPosition);
            notifyItemChanged(selectedPosition);
        });
    }

    @Override
    public int getItemCount() {
        return paymentMethods.size();
    }

    // ViewHolder class
    public static class PaymentMethodViewHolder extends RecyclerView.ViewHolder {
        MaterialRadioButton radioButton;
        ImageView imageView;
        TextView textView;

        public PaymentMethodViewHolder(@NonNull View itemView) {
            super(itemView);
            radioButton = itemView.findViewById(R.id.radio_payment_method);
            imageView = itemView.findViewById(R.id.img_payment_method);
            textView = itemView.findViewById(R.id.payment_method_title);
        }
    }

    // Method to get the selected payment method
    public PaymentMethod getSelectedPaymentMethod() {
        return selectedPosition >= 0 ? paymentMethods.get(selectedPosition) : null;
    }


}
