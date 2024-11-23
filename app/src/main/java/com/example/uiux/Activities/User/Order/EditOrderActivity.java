package com.example.uiux.Activities.User.Order;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uiux.Activities.Admin.Branch.EditBranchStoreActivity;
import com.example.uiux.Model.BranchStore;
import com.example.uiux.Model.Order;
import com.example.uiux.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditOrderActivity extends AppCompatActivity {
    TextView txtSoluong, txtTongTien,txtName,txtPhone,txtEmail,txtAddress,txtOrderDate,txtExpectedDeliveryDate,txtDeliveryDate;
    Spinner statusSpinner;
    Button saveBtn;
    private DatabaseReference orderRef;
    Order order;
    private String order_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_order);
        txtSoluong=findViewById(R.id.textViewQuantity);
        txtTongTien=findViewById(R.id.textViewTongTien);
        txtName=findViewById(R.id.textViewBuyerNameValue);
        txtPhone=findViewById(R.id.textViewPhoneValue);
        txtEmail=findViewById(R.id.textViewEmail);
        txtAddress=findViewById(R.id.textViewAddressValue);
        txtOrderDate=findViewById(R.id.textViewOrderDateValue);
        txtExpectedDeliveryDate=findViewById(R.id.textViewExpectedDeliveryDateValue);
        txtDeliveryDate=findViewById(R.id.textViewDeliveryDateValue);
        statusSpinner=findViewById(R.id.statusSpinner);
        saveBtn=findViewById(R.id.saveBTN);

        order_id = getIntent().getStringExtra("order_id");
        orderRef = FirebaseDatabase.getInstance().getReference("Order").child(order_id);
        FetchSpinnerStatus();
        loadOrderData();
        saveBtn.setOnClickListener(v -> {
            if(statusSpinner.getSelectedItemPosition()==3)
            {
                order.setIs_completed(1);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String currentDateAndTime = sdf.format(new Date());

                // Cập nhật delivery_date
                order.setDelivery_date(currentDateAndTime);
            }

            order.setStatus( statusSpinner.getSelectedItemPosition());

            // Nếu không chọn làm mặc định thì chỉ cập nhật địa chỉ hiện tại
            orderRef.setValue(order).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(EditOrderActivity.this, "Order updated.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditOrderActivity.this, "Failed to edit Order ", Toast.LENGTH_SHORT).show();
                }
            });

        });


    }

    private void FetchSpinnerStatus() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(EditOrderActivity.this,
                R.array.order_status, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);
    }
    private void loadOrderData() {
        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    order = snapshot.getValue(Order.class);
                    txtSoluong.setText(String.valueOf(order.getCart_items_ordered().size()));
                    txtTongTien.setText(String.valueOf(order.getTotal_price()));
                    txtName.setText(order.getName_customer());
                    txtPhone.setText(order.getPhone_number());
                    txtEmail.setText(order.getEmail());
                    txtAddress.setText(order.getAddress());
                    txtOrderDate.setText(order.getDate_order());
                    txtExpectedDeliveryDate.setText(order.getExpected_delivery_date());
                    txtDeliveryDate.setText(order.getDelivery_date());
                    statusSpinner.setSelection(order.getStatus());
//                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu có.
            }
        });
    }

}