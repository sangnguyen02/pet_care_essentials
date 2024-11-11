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

import com.example.uiux.Activities.Admin.Discount.DiscountActivity;
import com.example.uiux.Model.Category;
import com.example.uiux.Model.Discount;
import com.example.uiux.Model.Voucher;
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
import java.util.Objects;

public class VoucherActivity extends AppCompatActivity {
    private Spinner category_spinner;
    private Spinner status_spinner;
    private TextInputEditText voucher_code;
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
        setContentView(R.layout.activity_voucher);
        // Initialize UI components
        category_spinner = findViewById(R.id.category_spinner);
        status_spinner = findViewById(R.id.status_spinner);
        voucher_code = findViewById(R.id.voucher_code);
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
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Objects.requireNonNull(voucher_code.getText()).toString().isEmpty() &&!Objects.requireNonNull(percent_txt.getText()).toString().isEmpty()&&!Objects.requireNonNull(discount_quantity_txt.getText()).toString().isEmpty()
                &&!Objects.requireNonNull(start_date.getText()).toString().isEmpty()&&!Objects.requireNonNull(end_date.getText()).toString().isEmpty())
                {
                    UploadVoucher();
                }
                else
                {
                    Toast.makeText(VoucherActivity.this, "Please select correct", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void UploadVoucher()
    {
        String voucher_id = voucherRef.push().getKey();

        Voucher voucher = new Voucher();
        voucher.setVoucher_id(voucher_id);
        voucher.setCode(voucher_code.getText().toString());
        voucher.setCategory(category_spinner.getSelectedItem().toString());
        voucher.setDiscount_amount(Integer.parseInt(discount_quantity_txt.getText().toString()));
        voucher.setRemaining_quantity(Integer.parseInt(discount_quantity_txt.getText().toString()));
        voucher.setDiscount_percent(Integer.parseInt(percent_txt.getText().toString()));
        voucher.setMax_discount_amount(Double.parseDouble(max_discount_amount_txt.getText().toString()));
        voucher.setMinimum_order_value(Double.parseDouble(minimum_order_value_txt.getText().toString()));
        voucher.setStatus(status_spinner.getSelectedItemPosition());
        voucher.setStart_date(start_date.getText().toString());
        voucher.setEnd_date(end_date.getText().toString());
        progressDialog.show();
        voucherRef.child(voucher_id).setValue(voucher).addOnCompleteListener(task -> {
            progressDialog.dismiss();
            if (task.isSuccessful()) {
                Toast.makeText(VoucherActivity.this, "Service added successfully!", Toast.LENGTH_SHORT).show();
                clearInputFields();
            } else {
                Toast.makeText(VoucherActivity.this, "Failed to add Service: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
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
                ArrayAdapter<String> adapter = new ArrayAdapter<>(VoucherActivity.this, android.R.layout.simple_spinner_item, cateList);
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

    private void clearInputFields() {
        voucher_code.getText().clear();
        percent_txt = findViewById(R.id.percent_txt);
        max_discount_amount_txt .getText().clear();
        discount_quantity_txt .getText().clear();
        minimum_order_value_txt .getText().clear();
        start_date .getText().clear();
        end_date .getText().clear();
    }

}