package com.example.uiux.Adapters;
import com.bumptech.glide.Glide;
import com.example.uiux.Model.Banner;
import com.example.uiux.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {
    private List<Banner> mlistBanner;

    public BannerAdapter(List<Banner> mlistBanner) {
        this.mlistBanner = mlistBanner;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner, parent, false);

        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {

        Banner banner = mlistBanner.get(position);
        if(banner == null) {
            return;
        }

        Glide.with(holder.itemView.getContext())
                .load(banner.getImageUrl())
                .into(holder.imgBanner);
    }

    @Override
    public int getItemCount() {
        if(mlistBanner != null) {
            return mlistBanner.size();
        }

        return 0;
    }

    public static class BannerViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgBanner;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBanner = itemView.findViewById(R.id.img_banner);
        }
    }
}
