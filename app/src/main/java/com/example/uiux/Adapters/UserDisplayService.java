package com.example.uiux.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiux.Activities.User.Profile.EditAddressActivity;
import com.example.uiux.Activities.User.Service.CancelServiceActivity;
import com.example.uiux.Model.Account_Address;
import com.example.uiux.Model.ServiceOrder;
import com.example.uiux.R;

import java.util.List;

public class UserDisplayService extends RecyclerView.Adapter<UserDisplayService.UserDisplayServiceViewHolder> {

    private Context context;
    private List<ServiceOrder> serviceOrderList;

    // Constructor
    public UserDisplayService(Context context, List<ServiceOrder> serviceOrderList) {
        this.context = context;
        this.serviceOrderList = serviceOrderList;
    }

    @NonNull
    @Override
    public UserDisplayServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_service, parent, false);
        return new UserDisplayServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserDisplayServiceViewHolder holder, int position) {
        // Get current ServiceOrder item
        ServiceOrder serviceOrder = serviceOrderList.get(position);

        // Bind data to the views
        holder.tvServiceName.setText(serviceOrder.getService_name());
        holder.tvServiceType.setText(serviceOrder.getType());
        holder.tvOrderDate.setText(serviceOrder.getOrder_date());
        holder.tvTotalPrice.setText(String.valueOf(serviceOrder.getTotal_price()));
        holder.tvBranchName.setText(serviceOrder.getBranch_name());
        holder.tvBranchAddress.setText(serviceOrder.getBranch_address());
        holder.tvTime.setText(serviceOrder.getTime());
    }

    @Override
    public int getItemCount() {
        return serviceOrderList.size();
    }

    // ViewHolder class
    public  class UserDisplayServiceViewHolder extends RecyclerView.ViewHolder {
        TextView tvServiceName, tvServiceType, tvOrderDate, tvTotalPrice, tvBranchName, tvBranchAddress, tvTime;

        public UserDisplayServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize views
            tvServiceName = itemView.findViewById(R.id.tv_service_name);
            tvServiceType = itemView.findViewById(R.id.tv_service_type);
            tvOrderDate = itemView.findViewById(R.id.tv_order_date);
            tvTotalPrice = itemView.findViewById(R.id.tv_total_price);
            tvBranchName = itemView.findViewById(R.id.tv_branch_name);
            tvBranchAddress = itemView.findViewById(R.id.tv_branch_address);
            tvTime = itemView.findViewById(R.id.tv_time);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    ServiceOrder serviceOrder = serviceOrderList.get(position);
                    Intent intent = new Intent(context, CancelServiceActivity.class);
                    intent.putExtra("orderServiceId",serviceOrder.getService_order_id());
                    intent.putExtra("service_id", serviceOrder.getService_id());
                    intent.putExtra("service_name", serviceOrder.getService_name());
                    context.startActivity(intent);
                }
            });
        }
    }
}
