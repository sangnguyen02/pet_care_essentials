package com.example.uiux.Activities.Admin.Supplies;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.uiux.Model.Supplies;
import com.example.uiux.Model.Supplies_Import;
import com.example.uiux.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SuppliesImportActivity extends AppCompatActivity {
    private Spinner spinnerSupplies;
    private TextInputEditText edtQuantity, edtRemainingQuantity, edtImportPrice, edtImportDate;
    private Button btnSubmit;

    private ArrayAdapter<String> adapterSupplies;
    private ArrayList<String> suppliesList;
    private DatabaseReference databaseReference;
    private  String suppliesId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_supplies_import);

        spinnerSupplies = findViewById(R.id.spinner_supplies);
        edtQuantity = findViewById(R.id.edt_quantity);
        edtRemainingQuantity = findViewById(R.id.edt_remaining_quantity);
        edtImportPrice = findViewById(R.id.edt_import_price);
        edtImportDate = findViewById(R.id.edt_import_date);
        btnSubmit = findViewById(R.id.btn_submit);

        FetchSpinnerSupplies();

        // Lắng nghe sự kiện chọn sản phẩm từ Spinner
        spinnerSupplies.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedSupply = suppliesList.get(position);
                loadRemainingQuantity(selectedSupply);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Xử lý khi không có sản phẩm nào được chọn (nếu cần)
            }
        });

        edtImportDate.setOnClickListener(v -> showDatePickerDialog());
        btnSubmit.setOnClickListener(v -> submitProductImport());
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


    private void loadRemainingQuantity(String selectedSupply) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Supplies");
        databaseReference.orderByChild("name").equalTo(selectedSupply).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Supplies supply = dataSnapshot.getValue(Supplies.class);
                        if (supply != null) {
                            int remainingQuantity = supply.getQuantity(); // Lấy số lượng còn lại
                            suppliesId=supply.getSupplies_id();
                            edtRemainingQuantity.setText(String.valueOf(remainingQuantity)); // Hiển thị số lượng còn lại
                        }
                    }
                } else {
                    Log.e("Firebase", "Product not found in database.");
                    edtRemainingQuantity.setText("0"); // Không có sản phẩm, hiển thị 0
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SuppliesImportActivity.this, "Failed to load remaining quantity", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitProductImport() {
        DecimalFormat df = new DecimalFormat("0");
        String selectedSupply = spinnerSupplies.getSelectedItem().toString();
        String quantityStr = edtQuantity.getText().toString();
        String remainingQuantityStr = edtRemainingQuantity.getText().toString();
        String importPriceStr = edtImportPrice.getText().toString();
        String importDateStr = edtImportDate.getText().toString();
        double importPrice = Double.parseDouble(importPriceStr);
        String formattedImportPrice = df.format(importPrice); // Format the import price

// Convert formatted price back to Double for storage
        double formattedPriceValue = Double.valueOf(formattedImportPrice);

        if (quantityStr.isEmpty() || remainingQuantityStr.isEmpty() || importPriceStr.isEmpty() || importDateStr.isEmpty()) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity = Integer.parseInt(quantityStr);
        int remainingQuantity = Integer.parseInt(remainingQuantityStr);



        // Lưu dữ liệu nhập vào Firebase
        DatabaseReference importsRef = FirebaseDatabase.getInstance().getReference("Supplies_Imports");
        String importId = importsRef.push().getKey(); // Tạo ID cho import mới

        Supplies_Import suppliesImport = new Supplies_Import(
                importId,
                suppliesId,
                selectedSupply,
                quantity,
                remainingQuantity,
                formattedPriceValue,
                importDateStr // Lưu ngày đã được format
        );

        importsRef.child(importId).setValue(suppliesImport)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(SuppliesImportActivity.this, "Product imported successfully", Toast.LENGTH_SHORT).show();
                    updateSupplyQuantityAndCostPrice(selectedSupply, remainingQuantity + quantity, formattedPriceValue); // Cập nhật số lượng và giá cost price
                })
                .addOnFailureListener(e -> Toast.makeText(SuppliesImportActivity.this, "Failed to import product", Toast.LENGTH_SHORT).show());

    }


    private void updateSupplyQuantityAndCostPrice(String selectedSupply, int newQuantity, double importPrice) {
        databaseReference.orderByChild("name").equalTo(selectedSupply).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("quantity", newQuantity);
                        updates.put("cost_price", importPrice); // Cập nhật giá cost price với giá import price

                        dataSnapshot.getRef().updateChildren(updates)
                                .addOnSuccessListener(aVoid -> Log.e("Firebase", "Supply quantity and cost price updated successfully"))
                                .addOnFailureListener(e -> Log.e("Firebase", "Failed to update supply quantity or cost price"));
                    }
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
                List<String> suppList = new ArrayList<>(); // Danh sách chứa các "type" từ Firebase
                final Map<String, String> suppMap = new HashMap<>(); // Lưu type_id và type
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Supplies supplies = snapshot.getValue(Supplies.class);
                    if (supplies != null && supplies.getStatus() != 0 ) {
                        suppList.add(supplies.getName());
                        suppMap.put(supplies.getName(), supplies.getSupplies_id());
                    }
                }

                suppliesList.addAll(suppList); // Thêm danh sách sản phẩm vào suppliesList
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
