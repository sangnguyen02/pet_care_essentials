package com.example.uiux.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uiux.Activities.User.CartActivity;
import com.example.uiux.Model.CartItem;
import com.example.uiux.Model.Supplies_Detail;
import com.example.uiux.R;
import com.example.uiux.Utils.CurrencyFormatter;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItemList;
    private Context context;
    private List<CartItem> selectedItems = new ArrayList<>();

    public CartAdapter(Context context, List<CartItem> cartItemList) {
        this.context = context;
        this.cartItemList = cartItemList;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItemList.get(position);
        holder.loadStockQuantity(cartItem.getSupply_id(), holder);
        holder.tvSupplyTitle.setText(cartItem.getSupply_title());
        holder.tvSupplySize.setText(cartItem.getSupply_size());
        holder.tvSupplyPrice.setText(CurrencyFormatter.formatCurrency(cartItem.getSupply_price(), context.getString(R.string.currency_vn)));
//        holder.tvSupplyPrice.setText(String.valueOf(cartItem.getSupply_price()));
        holder.tvSupplyQuantity.setText(String.valueOf(cartItem.getQuantity()));

        // Set up checkbox listener to track selected items
        holder.checkBoxSelectItem.setOnCheckedChangeListener(null);
        holder.checkBoxSelectItem.setChecked(isItemSelected(cartItem));
        holder.checkBoxSelectItem.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedItems.add(cartItem);
                notifyItemChanged(position);
            } else {
                selectedItems.remove(cartItem);
                notifyItemChanged(position);
            }
            // Cập nhật tổng tiền mỗi khi có sự thay đổi
            ((CartActivity) context).updateTotalAmount();
        });



        String imageUrls = cartItem.getImageUrl();
        if (imageUrls != null && !imageUrls.isEmpty()) {
            Glide.with(context)
                    .load(imageUrls)
                    .error(R.drawable.guest)
                    .into(holder.img_supply);
        } else {
            holder.img_supply.setImageResource(R.drawable.product_sample);
        }

        holder.mcv_cart_item.setOnClickListener(view -> {
            // Handle item click if needed
        });

        holder.mcv_cart_minus.setOnClickListener(view -> {
            holder.updateQuantity(-1, cartItem);
            notifyItemChanged(position);
        });

        holder.mcv_cart_plus.setOnClickListener(view -> {
            holder.updateQuantity(1, cartItem);
            notifyItemChanged(position);
        });
    }

    // Hàm kiểm tra xem item có được chọn hay không
    private boolean isItemSelected(CartItem item) {
        for (CartItem selectedItem : selectedItems) {
            if (selectedItem.getCombinedKey().equals(item.getCombinedKey())) {  // So sánh dựa trên ID hoặc thuộc tính duy nhất
                return true;
            }
        }
        return false;
    }

    public void selectAllItems(boolean isChecked) {
        selectedItems.clear();

//        if (isChecked) {
//            selectedItems.addAll(cartItemList);
//        }
//        ((CartActivity) context).updateTotalAmount();
//
//
//        notifyDataSetChanged(); // Refresh the RecyclerView
        if (isChecked) {
            selectedItems.addAll(cartItemList);
        }

        // Cập nhật lại các checkbox của từng item
        for (int i = 0; i < cartItemList.size(); i++) {
            notifyItemChanged(i);
        }

        // Cập nhật tổng tiền
        ((CartActivity) context).updateTotalAmount();
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public List<CartItem> getSelectedItems() {

//        return selectedItems;
        List<CartItem> items = new ArrayList<>();
        for (CartItem item : cartItemList) {
            if (isItemSelected(item)) {
                items.add(item);
            }
        }
        return items;
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView mcv_cart_item, mcv_cart_minus, mcv_cart_plus;
        ImageView img_supply;
        TextView tvSupplyTitle, tvSupplySize, tvSupplyPrice, tvSupplyQuantity;
        MaterialCheckBox checkBoxSelectItem;
        int sizeQuantity;
        String accountId;
        SharedPreferences preferences;

        public CartViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            mcv_cart_item = itemView.findViewById(R.id.mcv_cart_item);
            mcv_cart_plus = itemView.findViewById(R.id.mcv_cart_plus);
            mcv_cart_minus = itemView.findViewById(R.id.mcv_cart_minus);
            tvSupplyTitle = itemView.findViewById(R.id.tv_cart_item_title);
            tvSupplySize = itemView.findViewById(R.id.tv_cart_item_size);
            tvSupplyPrice = itemView.findViewById(R.id.tv_cart_item_price);
            tvSupplyQuantity = itemView.findViewById(R.id.tv_cart_item_quantity);
            checkBoxSelectItem = itemView.findViewById(R.id.checkbox_cart_item);
            img_supply = itemView.findViewById(R.id.img_cart_item);

            preferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        }

        private void updateQuantity(int change, CartItem cartItem) {
            int newQuantity = cartItem.getQuantity() + change;
            Log.e("New Quantity", String.valueOf(newQuantity));


            if (newQuantity < 0) {
                newQuantity = 0;
            } else if (newQuantity > sizeQuantity) {
                newQuantity = sizeQuantity;
            }


            // Cập nhật tổng giá trị
            if (newQuantity > 0) {
                int pricePerUnit = (int) cartItem.getSupply_price(); // lấy giá một đơn vị
                int totalPrice = pricePerUnit * newQuantity;


                cartItem.setQuantity(newQuantity);
                tvSupplyQuantity.setText(String.valueOf(newQuantity));
                // Cập nhật Firebase
                accountId = preferences.getString("accountID", null);
                DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("Cart")
                        .child(accountId).child(cartItem.getCombinedKey());
                cartRef.child("quantity").setValue(newQuantity);
                cartRef.child("totalPrice").setValue(totalPrice);

                // Cập nhật tổng tiền ở Activity

            }
            ((CartActivity) itemView.getContext()).updateTotalAmount();
            //updateStockQuantity(cartItem.getSupply_id(), cartItem.getSupply_size(), currentQuantity, newQuantity);
        }

        private void updateStockQuantity(String suppliesImportId, String size, int currentQuantity, int newQuantity) {
            // Tính toán sự thay đổi số lượng
            int quantityChange = currentQuantity - newQuantity;

            // Nếu có thay đổi về số lượng, cập nhật trong Firebase
            if (quantityChange != 0) {
                DatabaseReference stockRef = FirebaseDatabase.getInstance().getReference("Supplies_Imports").child(suppliesImportId);
                stockRef.child("suppliesDetail").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int totalQuantity = 0; // Để tính tổng số lượng cho bảng Supplies

                        for (DataSnapshot detailSnapshot : snapshot.getChildren()) {
                            // Lấy chi tiết size của sản phẩm
                            Supplies_Detail suppliesDetail = detailSnapshot.getValue(Supplies_Detail.class);

                            if (suppliesDetail != null) {
                                // Nếu size trùng khớp, cập nhật số lượng cho size đó
                                if (suppliesDetail.getSize().equals(size)) {
                                    int stockQuantity = suppliesDetail.getQuantity();
                                    int updatedStockQuantity = stockQuantity + quantityChange;
                                    detailSnapshot.getRef().child("quantity").setValue(updatedStockQuantity);
                                }

                                // Tính tổng số lượng cho tất cả các size
                                totalQuantity += suppliesDetail.getQuantity();
                            }
                        }

                        // Cập nhật lại tổng số lượng trong bảng Supplies
                        updateTotalSupplyQuantity(suppliesImportId, totalQuantity);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Xử lý lỗi nếu cần
                    }
                });
            }
        }

        private void updateTotalSupplyQuantity(String suppliesId, int totalQuantity) {
            DatabaseReference supplyRef = FirebaseDatabase.getInstance().getReference("Supplies").child(suppliesId);

            supplyRef.child("quantity").setValue(totalQuantity);
        }


        private void loadStockQuantity(String supplyId, CartViewHolder holder) {
            DatabaseReference detailRef = FirebaseDatabase.getInstance().getReference("Supplies_Imports").child(supplyId);
            detailRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot optionSnapshot : snapshot.child("suppliesDetail").getChildren()) {
                        String size = optionSnapshot.child("size").getValue(String.class);
                        if (size != null && size.contentEquals(tvSupplySize.getText())) {
                            sizeQuantity = optionSnapshot.child("quantity").getValue(Integer.class);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Xử lý lỗi nếu cần
                }
            });
        }
    }
}