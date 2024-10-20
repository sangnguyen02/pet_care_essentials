package com.example.uiux.Activities.Admin.Supplies;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.view.LayoutInflater;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.uiux.Model.Supplies;
import com.example.uiux.Model.Supplies_Import;
import com.example.uiux.Model.Supplies_Detail;
import com.example.uiux.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class SuppliesImportActivity extends AppCompatActivity {
    private Spinner spinnerSupplies;
    private TextInputEditText edtImportDate;
    private ImageView img_back_import_supply;
    private MaterialButton btnSubmit, btnAddSize;
    private LinearLayout sizeQuantityContainer;
    private ArrayList<String> suppliesList;
    private Map<String, String> suppliesMap;
    private ArrayList<View> sizeQuantityViews = new ArrayList<>();
    private DatabaseReference databaseReference;
    private  String suppliesId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_supplies_import);
        img_back_import_supply = findViewById(R.id.img_back_import_supply);
        spinnerSupplies = findViewById(R.id.spinner_supplies);
        edtImportDate = findViewById(R.id.edt_import_date);
        btnSubmit = findViewById(R.id.btn_submit);
        btnAddSize = findViewById(R.id.btn_add_size);
        sizeQuantityContainer = findViewById(R.id.size_quantity_container);
        suppliesMap = new HashMap<>();
        btnAddSize.setOnClickListener(v -> addSizeQuantityView());
        img_back_import_supply.setOnClickListener(view -> finish());

        FetchSpinnerSupplies();
        spinnerSupplies.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedSupply = suppliesList.get(position);
                String selectedSupplyId = suppliesMap.get(selectedSupply); // Lấy ID từ suppliesMap
                Log.e("Selected Supply: ", selectedSupply + " | ID: " + selectedSupplyId);
                loadSupplyDetails(selectedSupplyId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Xử lý khi không có sản phẩm nào được chọn (nếu cần)
            }
        });

        edtImportDate.setOnClickListener(v -> showDatePickerDialog());
        btnSubmit.setOnClickListener(v -> {
            submitProductImport();
        });
    }

    private void addSizeQuantityView() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View sizeQuantityView = inflater.inflate(R.layout.size_quantity_row, sizeQuantityContainer, false);
        sizeQuantityContainer.addView(sizeQuantityView);
        sizeQuantityViews.add(sizeQuantityView);
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Tạo Calendar để cập nhật thời gian hiện tại
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(selectedYear, selectedMonth, selectedDay);

                    // Định dạng ngày với SimpleDateFormat
                    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
                    String formattedDate = dateFormat.format(selectedDate.getTime());

                    // Hiển thị ngày đã chọn lên EditText
                    edtImportDate.setText(formattedDate);
                },
                year, month, day);

        datePickerDialog.show();
    }

    private void loadSupplyDetails(String selectedSupplyId) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Supplies_Imports");
        databaseReference.child(selectedSupplyId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sizeQuantityContainer.removeAllViews();
                sizeQuantityViews.clear();

                if (snapshot.exists()) {
                    Supplies_Import suppliesImport = snapshot.getValue(Supplies_Import.class);
                    if (suppliesImport != null) {
                        List<Supplies_Detail> sizes = suppliesImport.getSuppliesDetail();
                        for (Supplies_Detail size : sizes) {
                            LayoutInflater inflater = LayoutInflater.from(SuppliesImportActivity.this);
                            View sizeQuantityView = inflater.inflate(R.layout.size_quantity_row, sizeQuantityContainer, false);

                            TextInputEditText edtSize = sizeQuantityView.findViewById(R.id.edt_size_row);
                            TextInputEditText edtQuantity = sizeQuantityView.findViewById(R.id.edt_quantity_row);
                            TextInputEditText edtImportPrice = sizeQuantityView.findViewById(R.id.edt_import_price_row);
                            TextInputEditText edtCostPrice = sizeQuantityView.findViewById(R.id.edt_cost_price_row);

                            edtSize.setText(size.getSize());
                            edtQuantity.setText(String.valueOf(size.getQuantity()));
                            edtImportPrice.setText(String.valueOf(size.getImport_price()));
                            edtCostPrice.setText(String.valueOf(size.getCost_price()));

                            sizeQuantityView.setTag(size.getId());

                            sizeQuantityContainer.addView(sizeQuantityView);
                            sizeQuantityViews.add(sizeQuantityView);
                        }
                    } else {
                        Log.e("Firebase", "No size details found for the selected supply.");
                    }
                } else {
                    Log.e("Firebase", "No supply details found for the selected product.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SuppliesImportActivity.this, "Failed to load supply details", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void submitProductImport() {
        String selectedSupply = spinnerSupplies.getSelectedItem().toString();
        String selectedSupplyId = suppliesMap.get(selectedSupply);
        String importDateStr = Objects.requireNonNull(edtImportDate.getText()).toString();
        final int[] totalQuantity = {0};
        double lowestCostPrice = Double.MAX_VALUE;
        List<Supplies_Detail> sizeList = new ArrayList<>();

        for (View view : sizeQuantityViews) {
            TextInputEditText edtSize = view.findViewById(R.id.edt_size_row);
            TextInputEditText edtQuantity = view.findViewById(R.id.edt_quantity_row);
            TextInputEditText edtImportPrice = view.findViewById(R.id.edt_import_price_row);
            TextInputEditText edtCostPrice = view.findViewById(R.id.edt_cost_price_row);




            String size = edtSize.getText().toString();
            String quantityStr = edtQuantity.getText().toString();
            String importPriceStr = edtImportPrice.getText().toString();
            String costPriceStr = edtCostPrice.getText().toString();


            if (size.isEmpty() || quantityStr.isEmpty() || importPriceStr.isEmpty() || costPriceStr.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields for each size", Toast.LENGTH_SHORT).show();
                return;
            }

            int quantity = Integer.parseInt(quantityStr);
            totalQuantity[0] += quantity;
            double importPrice = Double.parseDouble(importPriceStr);
            double costPrice = Double.parseDouble(costPriceStr);

            if(costPrice < lowestCostPrice) {
                lowestCostPrice = costPrice;
            }

            String sizeId = (String) view.getTag();
            Supplies_Detail suppliesSize = new Supplies_Detail(sizeId, size, quantity, importPrice, costPrice);
            sizeList.add(suppliesSize);
        }

        DatabaseReference importsRef = FirebaseDatabase.getInstance().getReference("Supplies_Imports");

        if(selectedSupplyId != null) {
            Supplies_Import suppliesImport = new Supplies_Import(
                    selectedSupplyId,
                    selectedSupply,
                    importDateStr,
                    sizeList
            );

            double finalLowestCostPrice = lowestCostPrice;
            importsRef.child(selectedSupplyId).setValue(suppliesImport)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(SuppliesImportActivity.this, "Product imported successfully", Toast.LENGTH_SHORT).show();
                        updateSupplyQuantity(selectedSupplyId, totalQuantity[0], finalLowestCostPrice);
                    })
                    .addOnFailureListener(e -> Toast.makeText(SuppliesImportActivity.this, "Failed to import product", Toast.LENGTH_SHORT).show());
        }

    }


    private void updateSupplyQuantity(String selectedSupplyId, int newQuantity, double sellPrice) {
        DatabaseReference suppliesRef = FirebaseDatabase.getInstance().getReference("Supplies");
        suppliesRef.child(selectedSupplyId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    snapshot.getRef().child("quantity").setValue(newQuantity)
                            .addOnSuccessListener(aVoid -> Log.e("Firebase", "Supply quantity updated successfully"))
                            .addOnFailureListener(e -> Log.e("Firebase", "Failed to update supply quantity"));
                    snapshot.getRef().child("sell_price").setValue(sellPrice)
                            .addOnSuccessListener(aVoid -> Log.e("Firebase", "Supply sell_price updated successfully"))
                            .addOnFailureListener(e -> Log.e("Firebase", "Failed to update supply sell_price"));
                } else {
                    Log.e("Firebase", "Supply not found.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to load supplies for update");
            }
        });
    }



    private void FetchSpinnerSupplies() {
        suppliesList = new ArrayList<>(); // Khởi tạo suppliesList
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Supplies");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> suppList = new ArrayList<>();
                suppliesMap.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Supplies supplies = snapshot.getValue(Supplies.class);
                    if (supplies != null && supplies.getStatus() != 0 ) {
                        suppList.add(supplies.getName());
                        suppliesMap.put(supplies.getName(), supplies.getSupplies_id());
                    }
                }

                suppliesList.addAll(suppList);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(SuppliesImportActivity.this, android.R.layout.simple_spinner_item, suppList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerSupplies.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi xảy ra
                Toast.makeText(SuppliesImportActivity.this, "Failed to load supplies", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
