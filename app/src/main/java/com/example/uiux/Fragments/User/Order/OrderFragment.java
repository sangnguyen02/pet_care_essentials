package com.example.uiux.Fragments.User.Order;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import java.util.Objects;

public class OrderFragment extends Fragment {
    public static final String TITLE = "title";
    View rootView;
    DatabaseReference orderRef;
    SharedPreferences preferences;
    String accountId;
    RecyclerView rcv_order;
    TextView tv_empty;
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
           // Log.e("STATUS Có", String.valueOf(status));
            status = getArguments().getInt("status");
        }
        preferences =  getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        accountId = preferences.getString("accountID", null);
        orderRef = FirebaseDatabase.getInstance().getReference("Order");

        initWidget();
       // Log.e("Status from order activity", String.valueOf(status));
        loadOrderByStatus(status);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assert getArguments() != null;
        ((TextView)view.findViewById(R.id.tv_empty)).setText(R.string.empty);
    }

    public void updateStatus(int newStatus) {
        if (this.status != newStatus) {
            this.status = newStatus;
            loadOrderByStatus(newStatus);
        }
    }




    void initWidget() {
        tv_empty = rootView.findViewById(R.id.tv_empty);
        rcv_order = rootView.findViewById(R.id.rcv_order);
        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(orderList, rootView.getContext());
        rcv_order.setLayoutManager(new LinearLayoutManager(rootView.getContext(), RecyclerView.VERTICAL, false));
        rcv_order.setAdapter(orderAdapter);
    }

    void loadOrderByStatus(int status) {
        if (orderList == null) {
            orderList = new ArrayList<>();
        }
        // Reset danh sách đơn hàng
        orderList.clear();


        // Lấy dữ liệu từ Firebase theo accountId
//        orderRef.orderByChild("status").equalTo(status)
        orderRef.orderByChild("account_id").equalTo(accountId)
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    orderList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Order order = snapshot.getValue(Order.class);
                        if (order != null && order.getStatus() == status) {
                            orderList.add(order);
                        }
                    }
                    orderAdapter.notifyDataSetChanged();


                    if(orderList.isEmpty()) {
                        tv_empty.setVisibility(View.VISIBLE);
                    } else {
                        tv_empty.setVisibility(View.GONE);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("Firebase Error", databaseError.getMessage());
                }
            }
        );
    }

}