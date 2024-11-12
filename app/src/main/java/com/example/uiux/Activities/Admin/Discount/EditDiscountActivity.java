package com.example.uiux.Activities.Admin.Discount;

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

import com.example.uiux.Activities.Admin.Voucher.EditVoucherActivity;
import com.example.uiux.Model.Category;
import com.example.uiux.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class EditDiscountActivity extends AppCompatActivity {

    private TextInputEditText discount_percent, start_date, end_date;
    private Spinner category_spinner;
    private Spinner status_spinner;
    private MaterialButton submitBtn;
    private ProgressDialog progressDialog;
    private DatabaseReference discountDatabase;
    private Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_discount);
        discount_percent = findViewById(R.id.edt_edit_percent);
        start_date = findViewById(R.id.edt_edit_startDate);
        end_date = findViewById(R.id.edt_edit_endDate);
        category_spinner = findViewById(R.id.spinnerCategory);
        status_spinner = findViewById(R.id.spinner_status);
        submitBtn = findViewById(R.id.saveBtn);
        discountDatabase = FirebaseDatabase.getInstance().getReference("Discount");

        FetchSpinnerCategory();
        FetchSpinnerStatus();
        start_date.setOnClickListener(v -> showDatePickerDialog(start_date));
        end_date.setOnClickListener(v -> showDatePickerDialog(end_date));
        LoadDiscountData();
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveDiscountData();
            }
        });
    }

    private void SaveDiscountData() {
        String discountId = getIntent().getStringExtra("discount_id");  // lấy ID voucher từ Intent (nếu có)
        if (discountId == null) {
            return;
        }

        progressDialog = ProgressDialog.show(this, "Loading", "Please wait...", true);
        discountDatabase.child(discountId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    String category = snapshot.child("category").getValue(String.class);
                    String startDate = snapshot.child("start_date").getValue(String.class);
                    String endDate = snapshot.child("end_date").getValue(String.class);
                    int discountPercent = snapshot.child("discount_percent").getValue(Integer.class);
                    int status = status_spinner.getSelectedItemPosition();

                    discountDatabase.child(discountId).child("category").setValue(category);
                    discountDatabase.child(discountId).child("start_date").setValue(startDate);
                    discountDatabase.child(discountId).child("end_date").setValue(endDate);
                    discountDatabase.child(discountId).child("discount_percent").setValue(discountPercent);
                    discountDatabase.child(discountId).child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                Toast.makeText(EditDiscountActivity.this, "Discount edit successfully!", Toast.LENGTH_SHORT).show();
                            } else {
                                // Handle error
                            }
                        }
                    });

                }
                else
                {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void LoadDiscountData() {
        String discountId = getIntent().getStringExtra("discount_id");  // Lấy ID voucher từ Intent (nếu có)
        if (discountId == null) {
            return;
        }

        progressDialog = ProgressDialog.show(this, "Loading", "Please wait...", true);
        discountDatabase.child(discountId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String category = snapshot.child("category").getValue(String.class);
                    String startDate = snapshot.child("start_date").getValue(String.class);
                    String endDate = snapshot.child("end_date").getValue(String.class);
                    int discountPercent = snapshot.child("discount_percent").getValue(Integer.class);
                    int status = status_spinner.getSelectedItemPosition();

                    // Set values to UI elements
                    discount_percent.setText(String.valueOf(discountPercent));
                    status_spinner.setSelection(status);
                    int index = getPositionByName(category_spinner, category);
                    category_spinner.setSelection(index);
                    start_date.setText(startDate);
                    end_date.setText(endDate);
                }

                // Đảm bảo dialog sẽ được tắt sau khi hoàn tất tải dữ liệu
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Đóng dialog nếu có lỗi
                progressDialog.dismiss();
                Toast.makeText(EditDiscountActivity.this, "Failed to load discount data.", Toast.LENGTH_SHORT).show();
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
                ArrayAdapter<String> adapter = new ArrayAdapter<>(EditDiscountActivity.this, android.R.layout.simple_spinner_item, cateList);
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

    private int getPositionByName(Spinner spinner,String name) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        if (adapter != null) {
            for (int i = 0; i < adapter.getCount(); i++) {
                if (adapter.getItem(i).equals(name)) {
                    return i; // Return the position if a match is found
                }
            }
        }
        return -1; // Return -1 if the item is not found
    }
}