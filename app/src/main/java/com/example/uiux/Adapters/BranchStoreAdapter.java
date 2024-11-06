package com.example.uiux.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uiux.Activities.Admin.Branch.EditBranchStoreActivity;
import com.example.uiux.Model.BranchStore;
import com.example.uiux.R;
import com.google.android.material.badge.ExperimentalBadgeUtils;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class BranchStoreAdapter extends RecyclerView.Adapter<BranchStoreAdapter.BranchStoreViewHolder>
{
    private List<BranchStore> branchStoreList;
    private Context context;
    public BranchStoreAdapter(List<BranchStore> branchStoreList, Context context) {
        this.branchStoreList = branchStoreList;
        this.context = context;
    }

    @NonNull
    @Override
    public BranchStoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_branch_store, parent, false);
        return new BranchStoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BranchStoreViewHolder holder, int position) {
        BranchStore branchStore = branchStoreList.get(position);
        holder.bind(branchStore);

    }

    @Override
    public int getItemCount() {
        return branchStoreList.size();
    }

    class BranchStoreViewHolder extends RecyclerView.ViewHolder {
        TextView txtAddress, txtName,txtStatus;

        public BranchStoreViewHolder(@NonNull View itemView) {
            super(itemView);

            txtAddress = itemView.findViewById(R.id.txtAddress);
            txtName = itemView.findViewById(R.id.txtName);
            txtStatus=itemView.findViewById(R.id.txtStatus);
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                BranchStore branchStore = branchStoreList.get(position);
                // Tạo một AlertDialog với 2 lựa chọn: Edit và Delete
                new AlertDialog.Builder(context)
                        .setItems(new CharSequence[]{"Edit", "Delete"}, (dialog, which) -> {
                            if (which == 0) {
                                // Nếu người dùng chọn "Sửa"
                               // String[] Province = branchStore.getProvince().split("\\+");
                               // String[] District = branchStore.getDistrict().split("\\+");
                               // String[] Ward = branchStore.getWard().split("\\+");
                                Intent intent = new Intent(context, EditBranchStoreActivity.class);
                                intent.putExtra("branchStore_id", branchStore.getBranch_Store_id());
//                                intent.putExtra("province_id", Province[0]);
//                                intent.putExtra("district_id", District[0]);
//                                intent.putExtra("ward_id", Ward[0]);
                                intent.putExtra("store_name", branchStore.getBranch_name());
                                intent.putExtra("store_status", branchStore.getStatus());
                                context.startActivity(intent);

                            } else if (which == 1) {
                                // Nếu người dùng chọn "Xóa"
                                new AlertDialog.Builder(context)
                                        .setTitle("Confirm")
                                        .setMessage("Are you sure")
                                        .setPositiveButton("Yes", (confirmDialog, confirmWhich) -> {
                                            // Thực hiện xóa địa chỉ khỏi cơ sở dữ liệu
                                            deleteBranchStoreFromDatabase(branchStore.getBranch_Store_id());

                                            // Xóa địa chỉ khỏi danh sách và cập nhật giao diện
                                            branchStoreList.remove(position); // Loại bỏ địa chỉ khỏi danh sách
                                            notifyItemRemoved(position);  // Thông báo cho RecyclerView item bị xóa
                                            notifyItemRangeChanged(position, branchStoreList.size()); // Cập nhật range các item khác

                                            // Thông báo người dùng
                                            Toast.makeText(context, "Delete BranchStore successfully", Toast.LENGTH_SHORT).show();
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

        @OptIn(markerClass = ExperimentalBadgeUtils.class)
        public void bind(BranchStore branchStore) {


            txtAddress.setText(branchStore.getAddress_details());

            txtName.setText(branchStore.getBranch_name());
            txtStatus.setText(String.valueOf(branchStore.getStatus()));

        }
    }
    private void deleteBranchStoreFromDatabase(String branchStore_Id) {
        // Ví dụ: Firebase hoặc một cơ sở dữ liệu khác
        FirebaseDatabase.getInstance().getReference("Branch Store").child(branchStore_Id).removeValue();

    }

}
