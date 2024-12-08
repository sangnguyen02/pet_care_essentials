package com.example.uiux.Fragments.User;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.uiux.Activities.AllSuppliesActivity;
import com.example.uiux.Activities.User.Service.BookServiceActivity;
import com.example.uiux.Adapters.CategoryAdapter;
import com.example.uiux.Adapters.ServiceUserAdapter;
import com.example.uiux.Model.Category;
import com.example.uiux.Model.Service;
import com.example.uiux.R;
import com.example.uiux.Utils.CurrencyFormatter;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CaringFragment extends Fragment implements ServiceUserAdapter.OnServiceUserClickListener {
    View rootView;
    RecyclerView rcv_our_services;
    ServiceUserAdapter serviceUserAdapter;
    List<Service> mListService;
    EditText edt_service_description;
    TextView tv_service_book_price;
    MaterialButton btn_book_service;
    Service selectedService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_caring, container, false);
        if (getActivity() != null) {
            getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.white));
        }

        initWidget();

        loadServices();

        btn_book_service.setOnClickListener(view -> {
            Intent goToBook = new Intent(rootView.getContext(), BookServiceActivity.class);
            goToBook.putExtra("serviceId", selectedService.getService_id());
            rootView.getContext().startActivity(goToBook);
        });

        return rootView;

    }

    void initWidget() {
        mListService = new ArrayList<>();
        edt_service_description = rootView.findViewById(R.id.edt_service_description);
        tv_service_book_price = rootView.findViewById(R.id.tv_service_book_price);
        btn_book_service = rootView.findViewById(R.id.btn_book_service);
        rcv_our_services = rootView.findViewById(R.id.rcv_our_services);
        rcv_our_services.setLayoutManager(new LinearLayoutManager(rootView.getContext(), LinearLayoutManager.HORIZONTAL, false));

    }

    void loadServices() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Service");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (mListService != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Service service = snapshot.getValue(Service.class);
                        mListService.add(service);
                    }
                }
                serviceUserAdapter = new ServiceUserAdapter(mListService, rootView.getContext(), CaringFragment.this);
                rcv_our_services.setAdapter(serviceUserAdapter);

                if (!mListService.isEmpty()) {
                    selectedService = mListService.get(0);
                    edt_service_description.setText(mListService.get(0).getDescription());
                    tv_service_book_price.setText(CurrencyFormatter.formatCurrency(mListService.get(0).getSell_price(), rootView.getContext().getString(R.string.currency_vn)));
                    serviceUserAdapter.selectedPosition = 0;
                    serviceUserAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onServiceSelected(Service service) {
        if (service != null) {
            selectedService = service;
            edt_service_description.setText(service.getDescription());
            tv_service_book_price.setText(CurrencyFormatter.formatCurrency(service.getSell_price(), rootView.getContext().getString(R.string.currency_vn)));
        }
    }
}