package com.example.uiux.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uiux.Activities.Admin.Order.ApproveReturnOrderActivity;
import com.example.uiux.Activities.Admin.Order.DisplayReturnActivity;
import com.example.uiux.Activities.Admin.Supplies.EditSuppliesActivity;
import com.example.uiux.Activities.User.CancelOrder.UserCancelOrderActivity;
import com.example.uiux.Model.AccountWallet;
import com.example.uiux.Model.InfoReturnOrder;
import com.example.uiux.Model.Order;
import com.example.uiux.Model.Supplies;
import com.example.uiux.Model.WalletHistory;
import com.example.uiux.R;
import com.example.uiux.Utils.OnTotalPriceRetrievedListener;
import com.example.uiux.Utils.OrderStatus;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ReturnOrderApdapter  extends RecyclerView.Adapter<ReturnOrderApdapter.ReturnOrderViewHolder>{
    private List<InfoReturnOrder> infoReturnOrderList;
    private Context context;

    public ReturnOrderApdapter(List<InfoReturnOrder> infoReturnOrderList, Context context) {
        this.infoReturnOrderList = infoReturnOrderList;
        this.context = context;
    }

    @NonNull
    @Override
    public ReturnOrderApdapter.ReturnOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_return_order, parent, false);
        return new ReturnOrderApdapter.ReturnOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReturnOrderApdapter.ReturnOrderViewHolder holder, int position) {
        InfoReturnOrder infoReturnOrder = infoReturnOrderList.get(position);
        holder.bind(infoReturnOrder);
    }

    @Override
    public int getItemCount() {
        return infoReturnOrderList.size();
    }
    class ReturnOrderViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView img1;
        private TextView userName,userPhone,userAddress, requestDate;

        public ReturnOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            img1 = itemView.findViewById(R.id.img_order);
            userName=itemView.findViewById(R.id.tv_return_name);
            userPhone=itemView.findViewById(R.id.tv_return_phone);
//            userAddress=itemView.findViewById(R.id.tv_return_address);
            requestDate=itemView.findViewById(R.id.tv_return_date);
            SharedPreferences preferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            String accountId = preferences.getString("accountID", null);
            Log.e("accountID fom ReturnAdapter",accountId);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    InfoReturnOrder infoReturnOrder = infoReturnOrderList.get(position);

                    // Tạo một AlertDialog với 2 lựa chọn: Edit và Delete
                    new AlertDialog.Builder(context)
                            .setItems(new CharSequence[]{"Edit", "Complete"}, (dialog, which) -> {
                                if (which == 0) {
                                    Intent intent = new Intent(context, ApproveReturnOrderActivity.class);
                                    intent.putExtra("info_return_id", infoReturnOrder.getInfo_return_id());
                                    intent.putExtra("order_id", infoReturnOrder.getOrder_id());
                                    context.startActivity(intent);

                                } else if (which == 1) {
                                    // Nếu người dùng chọn "Xóa"
                                    new AlertDialog.Builder(context)
                                            .setTitle("Confirm to receive return supplies")
                                            .setMessage("Are you sure  to receive return supplies?")
                                            .setPositiveButton("Yes", (confirmDialog, confirmWhich) -> {
                                                deteRequestFromDatabase(infoReturnOrder.getInfo_return_id());
                                                UpdateOrderStatus(OrderStatus.RETURNED,infoReturnOrder.getOrder_id());

                                                Refund(accountId,infoReturnOrder.getOrder_id());

                                                infoReturnOrderList.remove(position);
                                                notifyItemRemoved(position);
                                                notifyItemRangeChanged(position, infoReturnOrderList.size());
                                                Toast.makeText(context, "Đã trả hàng thành công", Toast.LENGTH_SHORT).show();
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
        public void bind(InfoReturnOrder InfoReturnOrder) {
            userName.setText(InfoReturnOrder.getName() != null ? InfoReturnOrder.getName() : "N/A");
            userPhone.setText(InfoReturnOrder.getPhone_number() != null ? InfoReturnOrder.getPhone_number() : "N/A");
//            userAddress.setText(InfoReturnOrder.getAddress() != null ? InfoReturnOrder.getAddress() : "N/A");
            requestDate.setText(InfoReturnOrder.getRequest_date() != null ? InfoReturnOrder.getRequest_date() : "N/A");

            List<String> imageUrls = InfoReturnOrder.getImageUrls();
            if (imageUrls != null && !imageUrls.isEmpty() && imageUrls.get(0) != null && !imageUrls.get(0).isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(imageUrls.get(0))
                        .placeholder(R.drawable.product_sample)
                        .error(R.drawable.product_sample)
                        .into(img1);
            } else {
                img1.setImageResource(R.drawable.product_sample);
            }
        }


        private void deteRequestFromDatabase(String Id)
        {
            FirebaseDatabase.getInstance().getReference("Return Order").child(Id).removeValue();
        }
        private void Refund(String accountId, String order_id) {
            DatabaseReference accountWalletRef = FirebaseDatabase.getInstance().getReference("Account Wallet");
            accountWalletRef
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    AccountWallet accountWallet = snapshot.getValue(AccountWallet.class);
                                    if (accountWallet != null&& Objects.equals(accountWallet.getAccount_id(), accountId)) {
                                        double currentBalance = accountWallet.getBalance();
                                        String wallet_id = accountWallet.getWallet_id();

                                        // Lấy tổng giá trị đơn hàng và cập nhật số dư
                                        getTotalPrice(order_id, totalPrice -> {
                                            double newBalance = currentBalance + totalPrice;
                                            accountWalletRef.child(wallet_id).child("balance").setValue(newBalance)
                                                    .addOnCompleteListener(task -> {
                                                        if (task.isSuccessful()) {
                                                            CreateHistory(wallet_id, order_id);
                                                        } else {
                                                            Log.e("Refund", "Failed to update balance.");
                                                        }
                                                    });
                                        });
                                    }
                                }
                            } else {
                                Log.e("Refund", "Account wallet not found.");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e("Refund", "Database error: " + databaseError.getMessage());
                        }
                    });
        }


        private void CreateHistory(String wallet_id, String order_id) {
            DatabaseReference walletHistoryRef = FirebaseDatabase.getInstance().getReference("Wallet History");
            String currentDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(new Date());
            String walletHistoryId = walletHistoryRef.push().getKey();

            // Lấy giá trị tổng tiền từ đơn hàng
            getTotalPrice(order_id, totalPrice -> {
                // Tạo đối tượng WalletHistory với số tiền đã lấy được
                WalletHistory walletHistory = new WalletHistory(
                        walletHistoryId,    // ID của lịch sử giao dịch
                        wallet_id,          // wallet_id của người dùng
                        String.valueOf(totalPrice),  // Số tiền hoàn tiền
                        "+",                // Trạng thái giao dịch, "+" cho hoàn tiền
                        currentDate         // Ngày giờ giao dịch
                );

                // Lưu WalletHistory vào Firebase
                walletHistoryRef.child(walletHistoryId).setValue(walletHistory)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("CreateHistory", "Wallet history created successfully.");
                            } else {
                                Log.e("CreateHistory", "Failed to create wallet history: " + task.getException());
                            }
                        });
            });
        }

        private void getTotalPrice(String order_id, OnTotalPriceRetrievedListener listener) {
            if (order_id == null || order_id.isEmpty()) {
                listener.onTotalPriceRetrieved(0);
                return;
            }

            DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("Order").child(order_id);
            orderRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().exists()) {
                    Order order = task.getResult().getValue(Order.class);
                    if (order != null) {
                        listener.onTotalPriceRetrieved(order.getTotal_price());
                        return;
                    }
                }
                listener.onTotalPriceRetrieved(0); // Trả về giá trị 0 nếu không thành công
            }).addOnFailureListener(e -> {
                listener.onTotalPriceRetrieved(0); // Trả về giá trị 0 nếu lỗi
            });
        }

        // Interface để xử lý callback


        private void UpdateOrderStatus(int status,String order_id) {
            if (order_id == null || order_id.isEmpty()) {
                Log.e("UpdateOrderStatus", "Order ID is null or empty.");
                return;
            }

            DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("Order");
            // Cập nhật trạng thái trong Firebase
            orderRef.child(order_id).child("status").setValue(status).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Trạng thái đã được cập nhật
                    String statusMessage = (status == OrderStatus.RETURN_PRODUCT_WAITING) ? "Yêu cầu trả hàng đã được phê duyệt." : "Yêu cầu trả hàng đã bị từ chối.";
                    Log.e("Message from ReturnAdapter",statusMessage);
                } else {
                    // Cập nhật thất bại
                    Log.e("UpdateOrderStatus", "Failed to update order status: " + task.getException());

                }
            });
        }

    }
}
