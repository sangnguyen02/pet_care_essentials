package com.example.uiux.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiux.Activities.User.EditAddressActivity;
import com.example.uiux.Model.Account_Address;
import com.example.uiux.R;

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
        TextView  txtAddress;
        Button btnEdit;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);

            txtAddress = itemView.findViewById(R.id.txtAddress);
            btnEdit = itemView.findViewById(R.id.btnEdit);

            btnEdit.setOnClickListener(v -> {
                int position = getAdapterPosition();
                Account_Address address = addressList.get(position);
                String[] Province=address.getProvince().split("\\+");
                String[] District=address.getProvince().split("\\+");
                String[] Ward=address.getProvince().split("\\+");
                Intent intent = new Intent(context, EditAddressActivity.class);
                intent.putExtra("address_id", address.getAccount_address_id());
                intent.putExtra("province_id",Province[0]);
                intent.putExtra("district_id",District[0]);
                intent.putExtra("ward_id",Ward[0]);
                intent.putExtra("isDefualt",address.getIs_default());
                context.startActivity(intent);
            });
        }

        public void bind(Account_Address address) {
            String[] Province=address.getProvince().split("\\+");
            String[] District=address.getProvince().split("\\+");
            String[] Ward=address.getProvince().split("\\+");

            txtAddress.setText(address.getAddress_details() + ", " + Province[1] + ", " + District[1] + ", " + Ward[1]);
        }
    }
}
