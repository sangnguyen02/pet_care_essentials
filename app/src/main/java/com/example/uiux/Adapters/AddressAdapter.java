package com.example.uiux.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiux.Activities.User.Profile.EditAddressActivity;
import com.example.uiux.Model.Account_Address;
import com.example.uiux.R;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;
import com.google.android.material.badge.ExperimentalBadgeUtils;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    private List<Account_Address> addressList;
    private Context context;
    private boolean fromPaymentActivity;

    public AddressAdapter(List<Account_Address> addressList, Context context, boolean fromPaymentActivity) {
        this.addressList = addressList;
        this.context = context;
        this.fromPaymentActivity = fromPaymentActivity;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_address, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        Account_Address address = addressList.get(position);
        holder.bind(address);
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    class AddressViewHolder extends RecyclerView.ViewHolder {
        TextView txtAddress, txtDefault;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);

            txtAddress = itemView.findViewById(R.id.txtAddress);
            txtDefault = itemView.findViewById(R.id.txtDefault);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                Account_Address address = addressList.get(position);

                if (fromPaymentActivity) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("selected_address", address.getAccount_address_id());
                    ((Activity) context).setResult(Activity.RESULT_OK, resultIntent);
                    ((Activity) context).finish();
                } else {
                    // Tạo một AlertDialog với 2 lựa chọn: Edit và Delete
                    new AlertDialog.Builder(context)
                        .setItems(new CharSequence[]{"Edit", "Delete"}, (dialog, which) -> {
                            if (which == 0) {
                                // Nếu người dùng chọn "Sửa"
                                String[] Province = address.getProvince().split("\\+");
                                String[] District = address.getDistrict().split("\\+");
                                String[] Ward = address.getWard().split("\\+");
                                Intent intent = new Intent(context, EditAddressActivity.class);
                                intent.putExtra("address_id", address.getAccount_address_id());
                                intent.putExtra("province_id", Province[0]);
                                intent.putExtra("district_id", District[0]);
                                intent.putExtra("ward_id", Ward[0]);
                                intent.putExtra("isDefault", address.getIs_default());
                                context.startActivity(intent);

                            } else if (which == 1) {
                                // Nếu người dùng chọn "Xóa"
                                new AlertDialog.Builder(context)
                                        .setTitle("Confirm")
                                        .setMessage("Are you sure")
                                        .setPositiveButton("Yes", (confirmDialog, confirmWhich) -> {
                                            // Thực hiện xóa địa chỉ khỏi cơ sở dữ liệu
                                            deleteAddressFromDatabase(address.getAccount_address_id());

                                            // Xóa địa chỉ khỏi danh sách và cập nhật giao diện
                                            addressList.remove(position); // Loại bỏ địa chỉ khỏi danh sách
                                            notifyItemRemoved(position);  // Thông báo cho RecyclerView item bị xóa
                                            notifyItemRangeChanged(position, addressList.size()); // Cập nhật range các item khác

                                            // Thông báo người dùng
                                            Toast.makeText(context, "Delete address successfully", Toast.LENGTH_SHORT).show();
                                        })
                                        .setNegativeButton("No", (confirmDialog, confirmWhich) -> {
                                            // Nếu người dùng chọn "Không", đóng dialog
                                            confirmDialog.dismiss();
                                        })
                                        .show();
                            }
                        })
                        .show();
                }
            });

        }

        @OptIn(markerClass = ExperimentalBadgeUtils.class)
        public void bind(Account_Address address) {
            String[] Province = address.getProvince().split("\\+");
            String[] District = address.getDistrict().split("\\+");
            String[] Ward = address.getWard().split("\\+");

            txtAddress.setText(address.getAddress_details() + ", " + Ward[1] + ", " + District[1] + ", " + Province[1]);
            if (address.getIs_default()) {
                txtDefault.setVisibility(View.VISIBLE);
            } else {
                txtDefault.setVisibility(View.GONE);
            }

        }
    }

    // Hàm xóa địa chỉ trong cơ sở dữ liệu (ví dụ với Firebase)
    private void deleteAddressFromDatabase(String addressId) {
        // Ví dụ: Firebase hoặc một cơ sở dữ liệu khác
         FirebaseDatabase.getInstance().getReference("Account_Address").child(addressId).removeValue();
        // Hoặc gọi API xóa nếu cần
    }
}
