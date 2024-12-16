package com.example.uiux.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiux.Activities.Admin.Services.DisplayServiceBookingActivity;
import com.example.uiux.Activities.User.Service.CancelServiceActivity;
import com.example.uiux.Model.ServiceOrder;
import com.example.uiux.R;
import com.example.uiux.Utils.CurrencyFormatter;

import java.util.List;

public class BookingServiceAdminAdapter extends RecyclerView.Adapter<BookingServiceAdminAdapter.ServiceAdminViewHolder>{
    private Context context;
    private List<ServiceOrder> serviceOrderList;
    public BookingServiceAdminAdapter(Context context, List<ServiceOrder> serviceOrderList) {
        this.context = context;
        this.serviceOrderList = serviceOrderList;
    }

    @NonNull
    @Override
    public BookingServiceAdminAdapter.ServiceAdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_service, parent, false);
        return new BookingServiceAdminAdapter.ServiceAdminViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingServiceAdminAdapter.ServiceAdminViewHolder holder, int position) {
        ServiceOrder serviceOrder = serviceOrderList.get(position);

        // Bind data to the views
        // Bind data from ServiceOrder to the views in the ViewHolder
        holder.tvServiceName.setText(serviceOrder.getService_name());
        holder.tvServiceType.setText(serviceOrder.getType());
        holder.tvOrderDate.setText(serviceOrder.getOrder_date());
        holder.tvTotalPrice.setText(CurrencyFormatter.formatCurrency(serviceOrder.getTotal_price(), holder.itemView.getContext().getString(R.string.currency_vn)));
        holder.tvBranchName.setText(serviceOrder.getBranch_name());
        holder.tvBranchAddress.setText(serviceOrder.getBranch_address());
        holder.tvTime.setText(serviceOrder.getTime());
        holder.tvPhone.setText(serviceOrder.getPhone_number());
        holder.tvMail.setText(serviceOrder.getEmail());
        holder.tvName.setText(serviceOrder.getName());



    }

    @Override
    public int getItemCount() {
        return serviceOrderList.size();
    }
    public class ServiceAdminViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvServiceName, tvServiceType, tvOrderDate, tvTotalPrice, tvBranchName, tvBranchAddress, tvTime,tvPhone,tvMail,tvName;

        public ServiceAdminViewHolder(@NonNull View itemView) {
            super(itemView);
            tvServiceName = itemView.findViewById(R.id.tv_service_name);
            tvServiceType = itemView.findViewById(R.id.tv_service_type);
            tvOrderDate = itemView.findViewById(R.id.tv_order_date);
            tvTotalPrice = itemView.findViewById(R.id.tv_total_price);
            tvBranchName = itemView.findViewById(R.id.tv_branch_name);
            tvBranchAddress = itemView.findViewById(R.id.tv_branch_address);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvPhone = itemView.findViewById(R.id.tv_phone);
            tvMail = itemView.findViewById(R.id.tv_email);
            tvName = itemView.findViewById(R.id.tv_username);
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    int position = getAdapterPosition();
//                    ServiceOrder serviceOrder = serviceOrderList.get(position);
//                    Intent intent = new Intent(context, DisplayServiceBookingActivity.class);
//                    intent.putExtra("orderServiceId",serviceOrder.getService_order_id());
//                    intent.putExtra("service_id", serviceOrder.getService_id());
//                    intent.putExtra("service_name", serviceOrder.getService_name());
//                    context.startActivity(intent);
//                }
//            });

        }
    }
}
