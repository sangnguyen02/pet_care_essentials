package com.example.uiux.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiux.Activities.User.Profile.EditAddressActivity;
import com.example.uiux.Model.Account_Address;
import com.example.uiux.R;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    private List<Account_Address> addressList;
    private Context context;

    public AddressAdapter(List<Account_Address> addressList, Context context) {
        this.addressList = addressList;
        this.context = context;
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
        TextView txtAddress;
        Button btnEdit, btnDelete;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);

            txtAddress = itemView.findViewById(R.id.txtAddress);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btndelete);

            // Chức năng sửa địa chỉ
            btnEdit.setOnClickListener(v -> {
                int position = getAdapterPosition();
                Account_Address address = addressList.get(position);
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
            });

            // Chức năng xóa địa chỉ
            // Chức năng xóa địa chỉ
            btnDelete.setOnClickListener(v -> {
                int position = getAdapterPosition();
                Account_Address address = addressList.get(position);

                // Thực hiện xóa trên database (nếu có)
                deleteAddressFromDatabase(address.getAccount_address_id());

                // Xóa địa chỉ khỏi danh sách và cập nhật giao diện
                addressList.remove(position); // Loại bỏ địa chỉ khỏi danh sách
                notifyItemRemoved(position);  // Thông báo cho RecyclerView item bị xóa
                notifyItemRangeChanged(position, addressList.size()); // Cập nhật range các item khác

                // Thông báo người dùng
                Toast.makeText(context, "Đã xóa địa chỉ thành công", Toast.LENGTH_SHORT).show();
            });

        }

        public void bind(Account_Address address) {
            String[] Province = address.getProvince().split("\\+");
            String[] District = address.getDistrict().split("\\+");
            String[] Ward = address.getWard().split("\\+");

            txtAddress.setText(address.getAddress_details() + ", " + Province[1] + ", " + District[1] + ", " + Ward[1]);
        }
    }

    // Hàm xóa địa chỉ trong cơ sở dữ liệu (ví dụ với Firebase)
    private void deleteAddressFromDatabase(String addressId) {
        // Ví dụ: Firebase hoặc một cơ sở dữ liệu khác
         FirebaseDatabase.getInstance().getReference("Account_Address").child(addressId).removeValue();
        // Hoặc gọi API xóa nếu cần
    }
}
