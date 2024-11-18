package com.example.uiux.Fragments.User.Order;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.uiux.Adapters.OrderAdapter;
import com.example.uiux.Model.Order;
import com.example.uiux.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrderFragment extends Fragment {
    public static final String TITLE = "title";
    View rootView;
    DatabaseReference orderRef;
    SharedPreferences preferences;
    String accountId;
    RecyclerView rcv_order;
    OrderAdapter orderAdapter;
    int status;
    List<Order> orderList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_order, container, false);

        if (getActivity() != null) {
            getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.white));
        }

        if (getArguments() != null) {
            Log.e("STATUS Có", String.valueOf(status));
            status = getArguments().getInt("status"); // Lấy trạng thái từ vị trí
        }
//        Log.e("STATUS Không", "Không");
        preferences =  getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        accountId = preferences.getString("accountID", null);
        orderRef = FirebaseDatabase.getInstance().getReference("Order").child(accountId);

        initWidget();

        loadOrderByStatus(status);
        Log.e("Status", String.valueOf(status));

        Log.e("Order List", orderList.toString());

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assert getArguments() != null;
        ((TextView)view.findViewById(R.id.tv_pending)).setText(getArguments().getString(TITLE));
    }


    void initWidget() {
        rcv_order = rootView.findViewById(R.id.rcv_order);
        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(orderList, rootView.getContext());
        rcv_order.setAdapter(orderAdapter);
    }

    void loadOrderByStatus(int status) {
        // Reset danh sách đơn hàng
        orderList.clear();

        // Lấy dữ liệu từ Firebase theo accountId
        orderRef.orderByChild("status").equalTo(status)  // Lọc theo trường 'status' (Giả sử có trường này trong Firebase)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Duyệt qua các đơn hàng
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Order order = snapshot.getValue(Order.class);
                            if (order != null) {
                                orderList.add(order);  // Thêm đơn hàng vào danh sách
                            }
                        }
                        // Cập nhật RecyclerView với dữ liệu mới
                        orderAdapter.notifyDataSetChanged();
                        Log.e("Order List", orderList.toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Xử lý lỗi nếu có
                        Log.e("Firebase Error", databaseError.getMessage());
                    }
                });
    }

}