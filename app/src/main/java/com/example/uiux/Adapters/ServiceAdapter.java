package com.example.uiux.Adapters;

import android.app.AlertDialog;
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
import com.example.uiux.Activities.Admin.Services.EditServiceActivity;
import com.example.uiux.Activities.Admin.Supplies.EditSuppliesActivity;
import com.example.uiux.Model.Service;
import com.example.uiux.Model.Supplies;
import com.example.uiux.R;
import com.google.firebase.database.FirebaseDatabase;


import java.util.List;

public class ServiceAdapter extends  RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {
    private List<Service> serviceList;
    private Context context;
    public ServiceAdapter(List<Service> serviceList, Context context) {
        this.serviceList = serviceList;
        this.context = context;
    }

    @NonNull
    @Override
    public ServiceAdapter.ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_service, parent, false);
        return new ServiceAdapter.ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceAdapter.ServiceViewHolder holder, int position) {
        Service service = serviceList.get(position);
        holder.bind(service);
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }
    class ServiceViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView img1,img2,img3,img4;
        private TextView service_name,service_SellPrice,service_time,service_Cate, service_Type,service_Size, service_Status;
        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            service_name = itemView.findViewById(R.id.tv_service_name);
            service_SellPrice = itemView.findViewById(R.id.tv_service_sell_price);
            service_time=itemView.findViewById(R.id.tv_time_estimate);
            service_Cate = itemView.findViewById(R.id.tv_service_category);
            service_Type = itemView.findViewById(R.id.tv_service_type);
            service_Size = itemView.findViewById(R.id.tv_service_size);
            service_Status = itemView.findViewById(R.id.tv_service_status);
            img1 = itemView.findViewById(R.id.img_service);
//            img2 = itemView.findViewById(R.id.img2);
//            img3 = itemView.findViewById(R.id.img3);
//            img4 = itemView.findViewById(R.id.img4);



            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                Service service = serviceList.get(position);

                // Tạo một AlertDialog với 2 lựa chọn: Edit và Delete
                new AlertDialog.Builder(context)
                        .setItems(new CharSequence[]{"Edit", "Delete"}, (dialog, which) -> {
                            if (which == 0) {
                                Intent intent = new Intent(context, EditServiceActivity.class);
                                intent.putExtra("service_id", service.getService_id());
                                context.startActivity(intent);

                            } else if (which == 1) {
                                // Nếu người dùng chọn "Xóa"
                                new AlertDialog.Builder(context)
                                        .setTitle("Confirm")
                                        .setMessage("Are you sure")
                                        .setPositiveButton("Yes", (confirmDialog, confirmWhich) -> {
                                            deleteServiceFromDatabase(service.getService_id());
                                            serviceList.remove(position);
                                            notifyItemRemoved(position);
                                            notifyItemRangeChanged(position, serviceList.size());
                                            Toast.makeText(context, "Product deleted successfully", Toast.LENGTH_SHORT).show();
                                        })
                                        .setNegativeButton("No", (confirmDialog, confirmWhich) -> {
                                            // Nếu người dùng chọn "Không", đóng dialog
                                            confirmDialog.dismiss();
                                        })
                                        .show();
                            }
                        })
                        .show();
            });
        }
        public void bind(Service service) {
            service_name.setText(service.getName() != null ? service.getName() : "N/A");
            service_SellPrice.setText(String.valueOf(service.getSell_price()));
            service_time.setText(String.valueOf(service.getTime_estimate()));
            service_Cate.setText(service.getCategory() != null ? service.getCategory() : "N/A");
            service_Type.setText(service.getType() != null ? service.getType() : "N/A");
            service_Size.setText(service.getSize() != null ? service.getSize() : "N/A");
            service_Status.setText(String.valueOf(service.getStatus()));

            List<String> imageUrls = service.getImageUrls();
            if (imageUrls != null && !imageUrls.isEmpty() && imageUrls.get(0) != null) {
                Glide.with(itemView.getContext())
                        .load(imageUrls.get(0))
                        .placeholder(R.drawable.banner1)
                        .error(R.drawable.guest)
                        .into(img1);
            } else {
                img1.setImageResource(R.drawable.product_sample);
            }
        }


        private void deleteServiceFromDatabase(String service_Id) {
            FirebaseDatabase.getInstance().getReference("Service").child(service_Id).removeValue();
        }
    }
}
