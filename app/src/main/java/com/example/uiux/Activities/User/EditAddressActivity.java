package com.example.uiux.Activities.User;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.uiux.Model.Account_Address;
import com.example.uiux.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class EditAddressActivity extends AppCompatActivity {

    private Spinner provinceSpinner, districtSpinner, wardSpinner;
    private ArrayAdapter<String> provinceAdapter;
    private ArrayAdapter<String> districtAdapter;
    private ArrayAdapter<String> wardAdapter;
    private CheckBox checkBoxDefault;
    private Button btnSave;
    private EditText edtAddressDetails;
    private DatabaseReference addressRef;
    private String addressId;
    private List<String> provinces = new ArrayList<>();
    private List<String> districts = new ArrayList<>();
    private List<String> wards = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_address);

        provinceSpinner = findViewById(R.id.provinceSpinner);
        districtSpinner = findViewById(R.id.districtSpinner);
        wardSpinner = findViewById(R.id.wardSpinner);
        edtAddressDetails = findViewById(R.id.detailAddress);
        btnSave = findViewById(R.id.btnSave);

        addressId = getIntent().getStringExtra("address_id");
        addressRef = FirebaseDatabase.getInstance().getReference("Account_Address").child(addressId);

        initializeSpinners();
        loadAddressData();
    }

    private void initializeSpinners() {
        // Ban đầu, không thêm giá trị nào, chờ dữ liệu từ Firebase
        provinceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, provinces);
        provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        provinceSpinner.setAdapter(provinceAdapter);

        districtAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, districts);
        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        districtSpinner.setAdapter(districtAdapter);

        wardAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, wards);
        wardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        wardSpinner.setAdapter(wardAdapter);
    }

    private void loadAddressData() {
        addressRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Account_Address accountAddress = snapshot.getValue(Account_Address.class);
                    if (accountAddress != null) {
                        // Đặt giá trị từ Firebase làm mục đầu tiên (hint) cho spinner
                        String[] Province=accountAddress.getProvince().split("\\+");
                        String[] District=accountAddress.getDistrict().split("\\+");
                        String[] Ward=accountAddress.getWard().split("\\+");
                        provinces.add(0, Province[1]);
                        districts.add(0,District[1]);
                        wards.add(0, Ward[1]);

                        // Notify adapters to update views
                        provinceAdapter.notifyDataSetChanged();
                        districtAdapter.notifyDataSetChanged();
                        wardAdapter.notifyDataSetChanged();

                        // Set giá trị spinner hiện tại dựa trên dữ liệu từ Firebase
                        provinceSpinner.setSelection(0);
                        districtSpinner.setSelection(0);
                        wardSpinner.setSelection(0);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu có.
            }
        });
    }
}
