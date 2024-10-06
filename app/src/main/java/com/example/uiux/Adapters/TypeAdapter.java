package com.example.uiux.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uiux.Activities.Admin.Category.EditCategoryActivity;
import com.example.uiux.Activities.Admin.Supplies.EditSuppliesActivity;
import com.example.uiux.Activities.Admin.Type.EditTypeActivity;
import com.example.uiux.Model.Category;
import com.example.uiux.Model.Type;
import com.example.uiux.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;


public class TypeAdapter extends RecyclerView.Adapter<TypeAdapter.TypeViewHolder> {
    private List<Type> mListType;
    private Context context;
    public TypeAdapter(List<Type> mListType,Context context) {
        this.mListType = mListType;
        this.context = context;
    }

    @NonNull
    @Override
    public TypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_type, parent, false);
        return new TypeAdapter.TypeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TypeViewHolder holder, int position) {
        Type type = mListType.get(position);
        holder.bind(type);

    }

    @Override
    public int getItemCount() {
        return mListType != null ? mListType.size() : 0;
    }

    public  class TypeViewHolder extends RecyclerView.ViewHolder {
       private TextView tv_type;

        public TypeViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_type = itemView.findViewById(R.id.tv_type);
            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                Type type = mListType.get(position);

                // Tạo một AlertDialog với 2 lựa chọn: Edit và Delete
                new AlertDialog.Builder(context)
                        .setItems(new CharSequence[]{"Edit", "Delete"}, (dialog, which) -> {
                            if (which == 0) {
                                Intent intent = new Intent(context, EditTypeActivity.class);
                                intent.putExtra("type_id",type.getType_id());
                                intent.putExtra("type_name",type.getType());

                                context.startActivity(intent);

                            } else if (which == 1) {
                                // Nếu người dùng chọn "Xóa"
                                new AlertDialog.Builder(context)
                                        .setTitle("Confirm")
                                        .setMessage("Are you sure")
                                        .setPositiveButton("Yes", (confirmDialog, confirmWhich) -> {
                                            deleteTypeFromDatabase(type.getType_id());
                                            mListType.remove(position);
                                            notifyItemRemoved(position);
                                            notifyItemRangeChanged(position, mListType.size());
                                            Toast.makeText(context, "Đã xóa loai thành công", Toast.LENGTH_SHORT).show();
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
        public void bind(Type type) {
            tv_type.setText(type.getType());
        }

        private void deleteTypeFromDatabase(String categoryId) {
            FirebaseDatabase.getInstance().getReference("Type").child(categoryId).removeValue();
        }
    }
}
