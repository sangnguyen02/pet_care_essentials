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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SuppliesImportActivity extends AppCompatActivity {
    private Spinner spinnerSupplies;
    private TextInputEditText edtQuantity, edtRemainingQuantity, edtImportPrice, edtImportDate;
    private Button btnSubmit;

    private ArrayAdapter<String> adapterSupplies;
    private ArrayList<String> suppliesList;
    private DatabaseReference databaseReference;

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
                Log.e("Selected ", selectedSupply);
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
                    // Cập nhật ngày vào EditText
                    edtImportDate.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear);
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
                            Log.e("Remaining Quantity ", String.valueOf(remainingQuantity));
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
        String selectedSupply = spinnerSupplies.getSelectedItem().toString();
        String quantityStr = edtQuantity.getText().toString();
        String remainingQuantityStr = edtRemainingQuantity.getText().toString();
        String importPriceStr = edtImportPrice.getText().toString();
        String importDate = edtImportDate.getText().toString();

        if (quantityStr.isEmpty() || remainingQuantityStr.isEmpty() || importPriceStr.isEmpty() || importDate.isEmpty()) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity = Integer.parseInt(quantityStr);
        int remainingQuantity = Integer.parseInt(remainingQuantityStr);

        // Kiểm tra xem số lượng nhập vào có hợp lệ không
        if (quantity > remainingQuantity) {
            Toast.makeText(this, "Quantity cannot exceed remaining quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lưu dữ liệu nhập vào Firebase
        DatabaseReference importsRef = FirebaseDatabase.getInstance().getReference("Supplies_Imports");
        String importId = importsRef.push().getKey(); // Tạo ID cho import mới

        Supplies_Import suppliesImport = new Supplies_Import(
                importId,
                selectedSupply,
                quantity,
                remainingQuantity,
                Double.parseDouble(importPriceStr),
                Calendar.getInstance().getTime() // Lấy ngày hiện tại làm ngày import
        );

        importsRef.child(importId).setValue(suppliesImport)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(SuppliesImportActivity.this, "Product imported successfully", Toast.LENGTH_SHORT).show();
                    updateSupplyQuantity(selectedSupply, remainingQuantity + quantity); // Cập nhật số lượng sản phẩm
                })
                .addOnFailureListener(e -> Toast.makeText(SuppliesImportActivity.this, "Failed to import product", Toast.LENGTH_SHORT).show());
    }

    private void updateSupplyQuantity(String selectedSupply, int newQuantity) {
        databaseReference.orderByChild("name").equalTo(selectedSupply).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        dataSnapshot.getRef().child("quantity").setValue(newQuantity)
                                .addOnSuccessListener(aVoid -> Log.e("Firebase", "Supply quantity updated successfully"))
                                .addOnFailureListener(e -> Log.e("Firebase", "Failed to update supply quantity"));
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
                    if (supplies != null && supplies.getStatus() == 0) {
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
