package com.example.uiux.Activities.Admin.Discount;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.uiux.Model.Category;
import com.example.uiux.Model.Discount;
import com.example.uiux.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class DiscountActivity extends AppCompatActivity {

    private TextInputEditText discount_percent, start_date, end_date;
    private Spinner category;
    private MaterialButton submitBtn;
    private ProgressDialog progressDialog;
    private DatabaseReference discountDatabase;
    private Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
        setContentView(R.layout.activity_discount);

        // Initialize UI elements
        discount_percent = findViewById(R.id.edt_edit_percent);
        start_date = findViewById(R.id.edt_edit_startDate);
        end_date = findViewById(R.id.edt_edit_endDate);
        category = findViewById(R.id.spinnerCategory);
        submitBtn = findViewById(R.id.saveBtn);
        discountDatabase = FirebaseDatabase.getInstance().getReference("Discount");

        FetchSpinnerCategory();
        start_date.setOnClickListener(v -> showDatePickerDialog(start_date));
        end_date.setOnClickListener(v -> showDatePickerDialog(end_date));

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");

        submitBtn.setOnClickListener(view -> {
            if (!Objects.requireNonNull(discount_percent.getText()).toString().isEmpty()
                    && !Objects.requireNonNull(start_date.getText()).toString().isEmpty()
                    && !Objects.requireNonNull(end_date.getText()).toString().isEmpty()) {
                uploadDiscount();
            } else {
                Toast.makeText(DiscountActivity.this, "Please select correct", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadDiscount() {
        String startDateStr = start_date.getText().toString();
        Date today = new Date();  // Ngày hiện tại
        Date startDate;

        try {
            startDate = dateFormat.parse(startDateStr);
            // Kiểm tra nếu ngày bắt đầu phải lớn hơn hôm nay
            if (startDate == null || !startDate.after(today)) {
                Toast.makeText(DiscountActivity.this, "Ngày bắt đầu phải lớn hơn hôm nay.", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(DiscountActivity.this, "Định dạng ngày không hợp lệ.", Toast.LENGTH_SHORT).show();
            return;
        }

        String discount_id = discountDatabase.push().getKey();

        Discount discount = new Discount();
        discount.setDiscount_id(discount_id);
        discount.setDiscount_percent(Double.parseDouble(discount_percent.getText().toString()));
        discount.setStart_date(startDateStr);
        discount.setEnd_date(end_date.getText().toString());
        discount.setStatus(0);
        discount.setCategory(category.getSelectedItem().toString());

        progressDialog.show();
        discountDatabase.child(discount_id).setValue(discount).addOnCompleteListener(task -> {
            progressDialog.dismiss();
            if (task.isSuccessful()) {
                Toast.makeText(DiscountActivity.this, "Service added successfully!", Toast.LENGTH_SHORT).show();
                clearInputFields();
            } else {
                Toast.makeText(DiscountActivity.this, "Failed to add Service: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
        discount_percent.setText("");
        start_date.setText("");
        end_date.setText("");
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
                ArrayAdapter<String> adapter = new ArrayAdapter<>(DiscountActivity.this, android.R.layout.simple_spinner_item, cateList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                category.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

}
