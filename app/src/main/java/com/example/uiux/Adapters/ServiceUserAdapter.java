package com.example.uiux.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uiux.Model.Service;
import com.example.uiux.R;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class ServiceUserAdapter extends RecyclerView.Adapter<ServiceUserAdapter.ServiceUserViewHolder> {
    private List<Service> mListService;
    private Context context;
    private OnServiceUserClickListener listener;

    public int selectedPosition = -1; // -1: Không có item nào được chọn ban đầu


    public ServiceUserAdapter(List<Service> mListService, OnServiceUserClickListener listener) {
        this.mListService = mListService;
        this.listener = listener;
    }

    public ServiceUserAdapter(List<Service> mListService, Context context, OnServiceUserClickListener listener) {
        this.mListService = mListService;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ServiceUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_services_user, parent, false);
        return new ServiceUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceUserViewHolder holder, int position) {
        Service service = mListService.get(position);
        if (service == null) {
            return;
        }

        List<String> imageUrls = service.getImageUrls();
        if (imageUrls != null && !imageUrls.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(imageUrls.get(0))
                    .placeholder(R.drawable.product_sample)
                    .error(R.drawable.product_sample)
                    .into(holder.img_service_user);
        } else {
            holder.img_service_user.setImageResource(R.drawable.product_sample);
        }

        holder.tv_item_service_title.setText(service.getName());

        if (selectedPosition == position) {
            holder.mcv_services_user.setStrokeColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.gradient));
            holder.mcv_services_user.setStrokeWidth(5);
        } else {
            holder.mcv_services_user.setStrokeColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.light_bg));
        }

        holder.mcv_services_user.setOnClickListener(view -> {
            if (listener != null) {
                listener.onServiceSelected(service);
            }
            selectedPosition = position;
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return mListService != null ? mListService.size() : 0;
    }
    public interface OnServiceUserClickListener {
        void onServiceSelected(Service service);
    }


    public static class ServiceUserViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView mcv_services_user;
        private ImageView img_service_user;
        private TextView tv_item_service_title;

        public ServiceUserViewHolder(@NonNull View itemView) {
            super(itemView);
            mcv_services_user = itemView.findViewById(R.id.mcv_services_user);
            img_service_user = itemView.findViewById(R.id.img_service_user);
            tv_item_service_title = itemView.findViewById(R.id.tv_item_service_title);

        }
    }
}
