package com.example.uiux.Activities.Admin.Voucher;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uiux.Model.Category;
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
import java.util.List;
import java.util.Locale;

public class EditVoucherActivity extends AppCompatActivity {
    private Spinner category_spinner;
    private Spinner status_spinner;
    private TextInputEditText voucher_code;
    private TextInputEditText quantity_txt;
    private TextInputEditText percent_txt;
    private TextInputEditText max_discount_amount_txt;
    private TextInputEditText discount_quantity_txt;
    private TextInputEditText minimum_order_value_txt;
    private TextInputEditText start_date;
    private TextInputEditText end_date;
    private MaterialButton save_btn;
    private Calendar calendar = Calendar.getInstance();
    private ProgressDialog progressDialog;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    private DatabaseReference voucherRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_voucher);
        category_spinner = findViewById(R.id.category_spinner);
        status_spinner = findViewById(R.id.status_spinner);
        voucher_code = findViewById(R.id.voucher_code);
        quantity_txt = findViewById(R.id.quantity_txt);
        percent_txt = findViewById(R.id.percent_txt);
        max_discount_amount_txt = findViewById(R.id.max_discount_amount_txt);
        discount_quantity_txt = findViewById(R.id.discount_quantity_txt);
        minimum_order_value_txt = findViewById(R.id.minimum_order_value_txt);
        start_date = findViewById(R.id.start_date);
        end_date = findViewById(R.id.end_date);
        save_btn=findViewById(R.id.saveBtn);
        voucherRef = FirebaseDatabase.getInstance().getReference("Voucher");

        FetchSpinnerCategory();
        FetchSpinnerStatus();
        start_date.setOnClickListener(v -> showDatePickerDialog(start_date));
        end_date.setOnClickListener(v -> showDatePickerDialog(end_date));
        LoadVoucherData();
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveVoucherData();
            }
        });
    }

    private void LoadVoucherData() {
        String voucherId = getIntent().getStringExtra("voucher_id");  // lấy ID voucher từ Intent (nếu có)
        if (voucherId == null) {
            return;
        }

        progressDialog = ProgressDialog.show(this, "Loading", "Please wait...", true);
        voucherRef.child(voucherId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String category = snapshot.child("category").getValue(String.class);
                    String code = snapshot.child("code").getValue(String.class);
                    int remainingQuantity = snapshot.child("remaining_quantity").getValue(Integer.class);
                    int discountPercent = snapshot.child("discount_percent").getValue(Integer.class);
                    double maxDiscountAmount = snapshot.child("max_discount_amount").getValue(Double.class);
                    int discountAmount = snapshot.child("discount_amount").getValue(Integer.class);
                    double minOrderValue = snapshot.child("minimum_order_value").getValue(Double.class);
                    int status = snapshot.child("status").getValue(Integer.class);
                    String startDate = snapshot.child("start_date").getValue(String.class);
                    String endDate = snapshot.child("end_date").getValue(String.class);

                    // Set values to UI elements
                    voucher_code.setText(code);
                    quantity_txt.setText(String.valueOf(remainingQuantity));
                    percent_txt.setText(String.valueOf(discountPercent));
                    max_discount_amount_txt.setText(String.valueOf(maxDiscountAmount));
                    discount_quantity_txt.setText(String.valueOf(discountAmount));
                    minimum_order_value_txt.setText(String.valueOf(minOrderValue));
                    start_date.setText(startDate);
                    end_date.setText(endDate);

//                    // Set the category spinner
//                    ArrayAdapter<String> categoryAdapter = (ArrayAdapter<String>) category_spinner.getAdapter();
//                    int categoryPosition = categoryAdapter.getPosition(category);
//                    category_spinner.setSelection(categoryPosition);

                    // Set the status spinner
                    status_spinner.setSelection(status);
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                // Handle error, e.g., show a message
            }
        });
    }

    private void SaveVoucherData() {
        String voucherId = getIntent().getStringExtra("voucher_id");  // lấy ID voucher từ Intent (nếu có)
        if (voucherId == null) {
            return;
        }

        progressDialog = ProgressDialog.show(this, "Saving", "Please wait...", true);

        String category = category_spinner.getSelectedItem().toString();
        String code = voucher_code.getText().toString();
        int remainingQuantity = Integer.parseInt(quantity_txt.getText().toString());
        int discountPercent = Integer.parseInt(percent_txt.getText().toString());
        double maxDiscountAmount = Double.parseDouble(max_discount_amount_txt.getText().toString());
        int discountAmount = Integer.parseInt(discount_quantity_txt.getText().toString());
        double minOrderValue = Double.parseDouble(minimum_order_value_txt.getText().toString());
        int status = status_spinner.getSelectedItemPosition();
        String startDate = start_date.getText().toString();
        String endDate = end_date.getText().toString();

        // Create a map to update data in Firebase
        voucherRef.child(voucherId).child("category").setValue(category);
        voucherRef.child(voucherId).child("code").setValue(code);
        voucherRef.child(voucherId).child("remaining_quantity").setValue(remainingQuantity);
        voucherRef.child(voucherId).child("discount_percent").setValue(discountPercent);
        voucherRef.child(voucherId).child("max_discount_amount").setValue(maxDiscountAmount);
        voucherRef.child(voucherId).child("discount_amount").setValue(discountAmount);
        voucherRef.child(voucherId).child("minimum_order_value").setValue(minOrderValue);
        voucherRef.child(voucherId).child("status").setValue(status);
        voucherRef.child(voucherId).child("start_date").setValue(startDate);
        voucherRef.child(voucherId).child("end_date").setValue(endDate)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(EditVoucherActivity.this, "Voucher added successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        // Handle error
                    }
                });
    }


    private void FetchSpinnerCategory() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Category");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> cateList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Category category = snapshot.getValue(Category.class);
                    if (category != null && category.getStatus() == 0) {
                        cateList.add(category.getName());
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(EditVoucherActivity.this, android.R.layout.simple_spinner_item, cateList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                category_spinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }
    private  void FetchSpinnerStatus()
    {
        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(this,
                R.array.discount_status, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        status_spinner.setAdapter(statusAdapter);
    }
    private void showDatePickerDialog(TextInputEditText txt_edit) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            txt_edit.setText(dateFormat.format(calendar.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
}