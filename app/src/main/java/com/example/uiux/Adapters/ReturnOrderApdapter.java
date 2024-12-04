package com.example.uiux.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uiux.Activities.Admin.Order.DisplayReturnActivity;
import com.example.uiux.Activities.Admin.Supplies.EditSuppliesActivity;
import com.example.uiux.Model.InfoReturnOrder;
import com.example.uiux.Model.Supplies;
import com.example.uiux.R;

import java.util.List;

public class ReturnOrderApdapter  extends RecyclerView.Adapter<ReturnOrderApdapter.ReturnOrderViewHolder>{
    private List<InfoReturnOrder> infoReturnOrderList;
    private Context context;

    public ReturnOrderApdapter(List<InfoReturnOrder> infoReturnOrderList, Context context) {
        this.infoReturnOrderList = infoReturnOrderList;
        this.context = context;
    }

    @NonNull
    @Override
    public ReturnOrderApdapter.ReturnOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_return_order, parent, false);
        return new ReturnOrderApdapter.ReturnOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReturnOrderApdapter.ReturnOrderViewHolder holder, int position) {
        InfoReturnOrder infoReturnOrder = infoReturnOrderList.get(position);
        holder.bind(infoReturnOrder);

    }

    @Override
    public int getItemCount() {
        return infoReturnOrderList.size();
    }
    class ReturnOrderViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView img1;
        private TextView userName,userPhone,userAddress, requestDate;

        public ReturnOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            img1 = itemView.findViewById(R.id.img1);
            userName=itemView.findViewById(R.id.tv_return_name);
            userPhone=itemView.findViewById(R.id.tv_return_phone);
            userAddress=itemView.findViewById(R.id.tv_return_address);
            requestDate=itemView.findViewById(R.id.tv_return_date);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    InfoReturnOrder infoReturnOrder = infoReturnOrderList.get(position);
                    Intent intent = new Intent(context, DisplayReturnActivity.class);
                    intent.putExtra("info_return_id", infoReturnOrder.getInfo_return_id());
                    intent.putExtra("order_id", infoReturnOrder.getOrder_id());
                    context.startActivity(intent);
                }
            });
        }
        public void bind(InfoReturnOrder InfoReturnOrder)
        {
            userName.setText(InfoReturnOrder.getName() != null ? InfoReturnOrder.getName() : "N/A");
            userPhone.setText(InfoReturnOrder.getPhone_number() != null ? InfoReturnOrder.getPhone_number() : "N/A");
            userAddress.setText(InfoReturnOrder.getAddress() != null ? InfoReturnOrder.getAddress() : "N/A");
            requestDate.setText(InfoReturnOrder.getRequest_date() != null ? InfoReturnOrder.getRequest_date() : "N/A");

            List<String> imageUrls = InfoReturnOrder.getImageUrls();
            if (imageUrls != null && !imageUrls.isEmpty()) {
                // Load the first image
                if (imageUrls.size() > 0) {
                    Glide.with(itemView.getContext())
                            .load(imageUrls.get(0))
                            .placeholder(R.drawable.product_sample)
                            .error(R.drawable.product_sample)
                            .into(img1);
                }

            } else {
                // Set placeholders if no images are available
                img1.setImageResource(R.drawable.product_sample);
            }
        }
    }
}
