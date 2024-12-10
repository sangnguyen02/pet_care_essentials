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
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiux.Activities.Admin.Branch.EditBranchStoreActivity;
import com.example.uiux.Activities.OrderDetailActivity;
import com.example.uiux.Activities.User.Order.EditOrderActivity;
import com.example.uiux.Model.BranchStore;
import com.example.uiux.Model.Order;
import com.example.uiux.R;
import com.example.uiux.Utils.CurrencyFormatter;
import com.example.uiux.Utils.OrderStatus;
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_orders, parent, false);
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
        TextView tv_order_total_price, tv_buyer_info , tv_buyer_address, tv_order_date, tv_order_status;
        CardView cv_order_status;

        public OrderAdminViewHolder(@NonNull View itemView) {
            super(itemView);

//            txtSoluong=itemView.findViewById(R.id.textViewQuantity);
            tv_order_total_price=itemView.findViewById(R.id.tv_order_total_price);
            tv_buyer_info=itemView.findViewById(R.id.tv_buyer_info);
            tv_buyer_address=itemView.findViewById(R.id.tv_buyer_address);

            tv_order_date=itemView.findViewById(R.id.tv_order_date);
            cv_order_status = itemView.findViewById(R.id.cv_order_status);
            tv_order_status=itemView.findViewById(R.id.tv_order_status);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                Order order = orderList.get(position);
                // Tạo một AlertDialog với 2 lựa chọn: Edit và Delete
                new AlertDialog.Builder(context)
                        .setItems(new CharSequence[]{"Review"}, (dialog, which) -> {
                            if (which == 0) {
                                Intent intent = new Intent(context, OrderDetailActivity.class);
                                intent.putExtra("order_id", order.getOrder_id());
                                context.startActivity(intent);

                            }
                        })
                        .show();
            });

        }
        @OptIn(markerClass = ExperimentalBadgeUtils.class)
        public void bind(Order order) {
            tv_order_total_price.setText("Total amount: " + CurrencyFormatter.formatCurrency(order.getTotal_price(), itemView.getContext().getString(R.string.currency_vn)));
            tv_buyer_info.setText("Buyer: " + order.getName_customer() + " | " + order.getPhone_number());
            tv_buyer_address.setText("Address " + order.getAddress());
            tv_order_date.setText("Date: " + order.getDate_order());
//            String[] statusArray = context.getResources().getStringArray(R.array.order_status);
//
//            // Lấy giá trị status từ Firebase và ánh xạ
//            int statusIndex = order.getStatus(); // `status` là số lưu trên Firebase
//            if (statusIndex >= 0 && statusIndex < statusArray.length) {
//                tv_order_status.setText(statusArray[statusIndex]); // Ánh xạ thành chuỗi
//            } else {
//                tv_order_status.setText("Trạng thái không hợp lệ"); // Xử lý lỗi
//            }
            int statusIndex = order.getStatus(); // Giá trị trạng thái từ Firebase
            String statusName = OrderStatus.getStatusName(statusIndex); // Lấy tên trạng thái từ OrderStatus
            tv_order_status.setText(statusName);
            cv_order_status.setCardBackgroundColor(getStatusColor(statusIndex));
        }

        private void deleteOrderFromDatabase(String order_Id) {
            // Ví dụ: Firebase hoặc một cơ sở dữ liệu khác
            FirebaseDatabase.getInstance().getReference("Order").child(order_Id).removeValue();
        }

        private int getStatusColor(int statusIndex) {
            switch (statusIndex) {
                case OrderStatus.PENDING:
                    return ContextCompat.getColor(context, R.color.status_pending); // Màu chờ
                case OrderStatus.PREPARING:
                    return ContextCompat.getColor(context, R.color.status_preparing); // Màu chuẩn bị
                case OrderStatus.SHIPPING:
                    return ContextCompat.getColor(context, R.color.status_shipping); // Màu giao hàng
                case OrderStatus.DELIVERED:
                    return ContextCompat.getColor(context, R.color.status_delivered); // Màu giao thành công
                case OrderStatus.CANCELED:
                    return ContextCompat.getColor(context, R.color.status_canceled); // Màu hủy
                case OrderStatus.RETURNED:
                    return ContextCompat.getColor(context, R.color.status_returned); // Màu trả hàng
                default:
                    return ContextCompat.getColor(context, R.color.status_unknown); // Màu không xác định
            }
        }
    }
}
