package com.example.uiux.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiux.Model.DeliveryMethod;
import com.example.uiux.R;
import com.example.uiux.Utils.CurrencyFormatter;
import com.google.android.material.radiobutton.MaterialRadioButton;

import java.util.List;

public class DeliveryMethodAdapter extends RecyclerView.Adapter<DeliveryMethodAdapter.DeliveryMethodViewHolder> {
    private Context context;
    private List<DeliveryMethod> deliveryMethods;
    private int selectedPosition = 0;
    private OnDeliveryMethodSelectedListener listener;

    public DeliveryMethodAdapter(Context context, List<DeliveryMethod> deliveryMethods) {
        this.context = context;
        this.deliveryMethods = deliveryMethods;
    }

    public interface OnDeliveryMethodSelectedListener {
        void onDeliveryMethodSelected(double cost);
    }

    public void setOnDeliveryMethodSelectedListener(OnDeliveryMethodSelectedListener listener) {
        this.listener = listener;
    }


    @NonNull
    @Override
    public DeliveryMethodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_delivery_method, parent, false);
        return new DeliveryMethodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeliveryMethodViewHolder holder, int position) {
        DeliveryMethod deliveryMethod = deliveryMethods.get(position);

        holder.delivery_method_title.setText(deliveryMethod.getTitle());
        holder.delivery_method_cost.setText(CurrencyFormatter.formatCurrency(deliveryMethod.getCost(), context.getString(R.string.currency_vn)));

        holder.radio_delivery_method.setChecked(position == selectedPosition);

        // Handle clicks
        holder.radio_delivery_method.setOnClickListener(v -> {
            int previousSelectedPosition = selectedPosition;
            selectedPosition = holder.getBindingAdapterPosition();

            // Notify changes only for the affected items
            notifyItemChanged(previousSelectedPosition);
            notifyItemChanged(selectedPosition);

            if (listener != null) {
                listener.onDeliveryMethodSelected(deliveryMethod.getCost());
            }
        });
    }

    @Override
    public int getItemCount() {
        return deliveryMethods.size();
    }

    // ViewHolder class
    public static class DeliveryMethodViewHolder extends RecyclerView.ViewHolder {
        MaterialRadioButton radio_delivery_method;
        TextView delivery_method_title, delivery_method_cost;

        public DeliveryMethodViewHolder(@NonNull View itemView) {
            super(itemView);
            radio_delivery_method = itemView.findViewById(R.id.radio_delivery_method);
            delivery_method_cost = itemView.findViewById(R.id.delivery_method_cost);
            delivery_method_title = itemView.findViewById(R.id.delivery_method_title);
        }
    }

    // Method to get the selected payment method
    public DeliveryMethod getSelectedPaymentMethod() {
        return selectedPosition >= 0 ? deliveryMethods.get(selectedPosition) : null;
    }
}
