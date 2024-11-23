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
import com.example.uiux.Activities.User.Order.EditOrderActivity;
import com.example.uiux.Model.BranchStore;
import com.example.uiux.Model.Order;
import com.example.uiux.R;
import com.google.android.material.badge.ExperimentalBadgeUtils;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class OrderAdminAdapter  extends RecyclerView.Adapter<OrderAdminAdapter.OrderAdminViewHolder>{
    private List<Order> orderList;
    private Context context;
    public OrderAdminAdapter(List<Order> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderAdminAdapter.OrderAdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_admin, parent, false);
        return new OrderAdminAdapter.OrderAdminViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdminAdapter.OrderAdminViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.bind(order);

    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
    class OrderAdminViewHolder  extends RecyclerView.ViewHolder
    {
        TextView txtSoluong, txtTongTien,txtName,txtPhone,txtEmail,txtAddress,txtOrderDate,txtExpectedDeliveryDate,txtStatus,txtDeliveryDate;

        public OrderAdminViewHolder(@NonNull View itemView) {
            super(itemView);

            txtSoluong=itemView.findViewById(R.id.textViewQuantity);
            txtTongTien=itemView.findViewById(R.id.textViewTongTien);
            txtName=itemView.findViewById(R.id.textViewBuyerNameValue);
            txtPhone=itemView.findViewById(R.id.textViewPhoneValue);
            txtEmail=itemView.findViewById(R.id.textViewEmail);
            txtAddress=itemView.findViewById(R.id.textViewAddressValue);
            txtOrderDate=itemView.findViewById(R.id.textViewOrderDateValue);
            txtExpectedDeliveryDate=itemView.findViewById(R.id.textViewExpectedDeliveryDateValue);
            txtDeliveryDate=itemView.findViewById(R.id.textViewDeliveryDateValue);
            txtStatus=itemView.findViewById(R.id.textViewStatus);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                Order order = orderList.get(position);
                // Tạo một AlertDialog với 2 lựa chọn: Edit và Delete
                new AlertDialog.Builder(context)
                        .setItems(new CharSequence[]{"Edit", "Delete"}, (dialog, which) -> {
                            if (which == 0) {

                                Intent intent = new Intent(context, EditOrderActivity.class);
                                intent.putExtra("order_id", order.getOrder_id());
                                context.startActivity(intent);

                            } else if (which == 1) {
                                // Nếu người dùng chọn "Xóa"
                                new AlertDialog.Builder(context)
                                        .setTitle("Confirm")
                                        .setMessage("Are you sure")
                                        .setPositiveButton("Yes", (confirmDialog, confirmWhich) -> {
                                            // Thực hiện xóa địa chỉ khỏi cơ sở dữ liệu
                                            deleteOrderFromDatabase(order.getOrder_id());

                                            // Xóa địa chỉ khỏi danh sách và cập nhật giao diện
                                            orderList.remove(position); // Loại bỏ địa chỉ khỏi danh sách
                                            notifyItemRemoved(position);  // Thông báo cho RecyclerView item bị xóa
                                            notifyItemRangeChanged(position, orderList.size()); // Cập nhật range các item khác

                                            // Thông báo người dùng
                                            Toast.makeText(context, "Delete Order successfully", Toast.LENGTH_SHORT).show();
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
        public void bind(Order order) {
            txtSoluong.setText(String.valueOf(order.getCart_items_ordered().size())); // Assuming 'getQuantity()' returns the order quantity
            txtTongTien.setText(String.valueOf(order.getTotal_price()));
            txtName.setText(order.getName_customer());
            txtPhone.setText(order.getPhone_number());
            txtEmail.setText(order.getEmail());
            txtAddress.setText(order.getAddress());
            txtOrderDate.setText(order.getDate_order());
            txtExpectedDeliveryDate.setText(order.getExpected_delivery_date());
            txtDeliveryDate.setText(order.getDelivery_date());
            String[] statusArray = context.getResources().getStringArray(R.array.order_status);

            // Lấy giá trị status từ Firebase và ánh xạ
            int statusIndex = order.getStatus(); // `status` là số lưu trên Firebase
            if (statusIndex >= 0 && statusIndex < statusArray.length) {
                txtStatus.setText(statusArray[statusIndex]); // Ánh xạ thành chuỗi
            } else {
                txtStatus.setText("Trạng thái không hợp lệ"); // Xử lý lỗi
            }
        }

        private void deleteOrderFromDatabase(String order_Id) {
            // Ví dụ: Firebase hoặc một cơ sở dữ liệu khác
            FirebaseDatabase.getInstance().getReference("Order").child(order_Id).removeValue();
        }
    }
}
