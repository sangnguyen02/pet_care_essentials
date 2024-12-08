package com.example.uiux.Adapters;

import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uiux.Model.Category;
import com.example.uiux.R;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<Category> mListCategory;
    private OnCategoryClickListener listener;

    public CategoryAdapter(List<Category> mListCategory, OnCategoryClickListener listener) {
        this.mListCategory = mListCategory;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = mListCategory.get(position);
        if (category == null) {
            return;
        }

        Glide.with(holder.itemView.getContext())
                .load(category.getImageUrl()).fitCenter().override(64,64)
                .into(holder.imgCategory);

        holder.mcv_category.setOnClickListener(view -> {
            if (listener != null) {
                listener.onCategoryClick(category.getName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mListCategory != null ? mListCategory.size() : 0;
    }
    public interface OnCategoryClickListener {
        void onCategoryClick(String categoryName);
    }


    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView mcv_category;
        private ImageView imgCategory;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            mcv_category = itemView.findViewById(R.id.mcv_category_item);
            imgCategory = itemView.findViewById(R.id.img_category);

        }
    }
}
