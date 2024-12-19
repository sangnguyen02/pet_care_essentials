package com.example.uiux.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uiux.Activities.Admin.Category.EditCategoryActivity;
import com.example.uiux.Activities.Admin.Supplies.EditSuppliesActivity;
import com.example.uiux.Model.Category;
import com.example.uiux.Model.Supplies;
import com.example.uiux.Model.Type;
import com.example.uiux.R;
import com.example.uiux.Utils.CurrencyFormatter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SuppliesAdapter  extends RecyclerView.Adapter<SuppliesAdapter.SuppliesViewHolder>{
    private List<Supplies> suppliesList;
    private Context context;
    public SuppliesAdapter(List<Supplies> suppliesList, Context context) {
        this.suppliesList = suppliesList;
        this.context = context;
    }

    @NonNull
    @Override
    public SuppliesAdapter.SuppliesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_supplies, parent, false);
        return new SuppliesAdapter.SuppliesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuppliesAdapter.SuppliesViewHolder holder, int position) {
        Supplies supplies = suppliesList.get(position);
        holder.bind(supplies);
    }

    public void updateList(List<Supplies> newList) {
        this.suppliesList = newList;
        notifyDataSetChanged();  // Notify RecyclerView of data change
    }

    @Override
    public int getItemCount() {
        return suppliesList.size();
    }
    class SuppliesViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView img1;
        private TextView suppName,suppSellPrice,suppQuantity, suppCate, suppType, suppStatus;

        public SuppliesViewHolder(@NonNull View itemView) {
            super(itemView);
            suppName = itemView.findViewById(R.id.tv_supply_name);
            suppSellPrice = itemView.findViewById(R.id.tv_supply_sell_price);
            suppQuantity=itemView.findViewById(R.id.tv_supply_quantity);
            suppCate = itemView.findViewById(R.id.tv_supply_category);
            suppType = itemView.findViewById(R.id.tv_supply_type);
            suppStatus = itemView.findViewById(R.id.tv_supply_status);
            img1 = itemView.findViewById(R.id.img1);



            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                Supplies supp = suppliesList.get(position);

                // Tạo một AlertDialog với 2 lựa chọn: Edit và Delete
                new AlertDialog.Builder(context)
                        .setItems(new CharSequence[]{"Edit"}, (dialog, which) -> {
                            if (which == 0) {
                                Intent intent = new Intent(context, EditSuppliesActivity.class);
                                intent.putExtra("supplies_id", supp.getSupplies_id());
                                context.startActivity(intent);
                            }

//                            } else if (which == 1) {
//                                // Nếu người dùng chọn "Xóa"
//                                new AlertDialog.Builder(context)
//                                        .setTitle("Confirm")
//                                        .setMessage("Are you sure")
//                                        .setPositiveButton("Yes", (confirmDialog, confirmWhich) -> {
//                                            deleteSuppliesFromDatabase(supp.getSupplies_id());
//                                            suppliesList.remove(position);
//                                            notifyItemRemoved(position);
//                                            notifyItemRangeChanged(position, suppliesList.size());
//                                            Toast.makeText(context, "Product deleted successfully", Toast.LENGTH_SHORT).show();
//                                        })
//                                        .setNegativeButton("No", (confirmDialog, confirmWhich) -> {
//                                            // Nếu người dùng chọn "Không", đóng dialog
//                                            confirmDialog.dismiss();
//                                        })
//                                        .show();
//                            }
                        })
                        .show();
            });
        }
        public void bind(Supplies supplies) {
            suppName.setText(supplies.getName() != null ? supplies.getName() : "N/A");
            suppSellPrice.setText(CurrencyFormatter.formatCurrency(supplies.getSell_price(), itemView.getContext().getString(R.string.currency_vn)));
            suppQuantity.setText(String.valueOf(supplies.getQuantity()));
            suppCate.setText(supplies.getCategory() != null ? supplies.getCategory() : "N/A");
            suppType.setText(supplies.getType() != null ? supplies.getType() : "N/A");
//            suppStatus.setText(String.valueOf(supplies.getStatus()));
            int statusIndex = supplies.getStatus(); // Giả sử status trả về chỉ mục
            String[] statusArray = context.getResources().getStringArray(R.array.suplies_status);

            if (statusIndex >= 0 && statusIndex < statusArray.length) {
                suppStatus.setText(statusArray[statusIndex]);
            } else {
                suppStatus.setText("Unknown"); // Trạng thái không hợp lệ
            }

            List<String> imageUrls = supplies.getImageUrls();
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

        private void deleteSuppliesFromDatabase(String supplies_Id) {
            FirebaseDatabase.getInstance().getReference("Supplies").child(supplies_Id).removeValue();
        }

    }
}
