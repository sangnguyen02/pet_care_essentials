package com.example.uiux.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uiux.Activities.Admin.Category.EditCategoryActivity;
import com.example.uiux.Model.Category;
import com.example.uiux.R;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class CategoryAdminAdapter extends RecyclerView.Adapter<CategoryAdminAdapter.CategoryAdminViewHolder> {
    private List<Category> categoryList;
    private Context context;

    public CategoryAdminAdapter(List<Category> categoryList, Context context) {
        this.categoryList = categoryList;
        this.context = context;
    }

    @NonNull
    @Override
    public CategoryAdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category_admin, parent, false);
        return new CategoryAdminViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdminViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.bind(category);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    class CategoryAdminViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtStatus;
        Button btnEdit, btnDelete;
        ImageView image;

        public CategoryAdminViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            btnEdit = itemView.findViewById(R.id.btnEditCategory);
            btnDelete = itemView.findViewById(R.id.btndeleteCategory);
            image = itemView.findViewById(R.id.img_icon);

            btnEdit.setOnClickListener(view -> {
                int position = getAdapterPosition();
                Category category = categoryList.get(position);
                Intent intent = new Intent(context, EditCategoryActivity.class);
                intent.putExtra("category_id", category.getCategory_id());
                intent.putExtra("category_name", category.getName());
                intent.putExtra("category_image", category.getImageUrl());
                intent.putExtra("category_status", category.getStatus());
                context.startActivity(intent);
            });

            btnDelete.setOnClickListener(view -> {
                int position = getAdapterPosition();
                Category category = categoryList.get(position);
                deleteCategoryFromDatabase(category.getCategory_id());
                categoryList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, categoryList.size());
                Toast.makeText(context, "Đã xóa danh mục thành công", Toast.LENGTH_SHORT).show();
            });
        }

        public void bind(Category category) {
            txtName.setText(category.getName());
            if(category.getStatus()==0)
            {
                txtStatus.setText("Active");
            }
            else {
                txtStatus.setText("Inactive");

            }

            Glide.with(itemView.getContext())
                    .load(category.getImageUrl())
                    .placeholder(R.drawable.banner1)
                    .error(R.drawable.guest)
                    .into(image);
        }

        private void deleteCategoryFromDatabase(String categoryId) {
            FirebaseDatabase.getInstance().getReference("Category").child(categoryId).removeValue();
        }
    }
}
