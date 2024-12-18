package com.example.uiux.Activities.User.Profile;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.uiux.Model.Account_Address;
import com.example.uiux.Model.District;
import com.example.uiux.Model.Province;
import com.example.uiux.Model.Ward;
import com.example.uiux.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class EditAddressActivity extends AppCompatActivity {

    private Spinner provinceSpinner, districtSpinner, wardSpinner;
    private ArrayAdapter<Province> provinceAdapter;
    private ArrayAdapter<District> districtAdapter;
    private ArrayAdapter<Ward> wardAdapter;
    private TextInputEditText detailAdressTv;
    //private DatabaseReference databaseReference;
    private MaterialButton saveBTN;
    private ImageView imgv_back;
    private CheckBox checkBoxDefault;
    Account_Address accountAddress;
    private DatabaseReference addressRef;
    private String addressId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
        setContentView(R.layout.activity_edit_address);
        imgv_back = findViewById(R.id.imgv_back_edit_address);
        provinceSpinner = findViewById(R.id.provinceSpinner_edit);
        districtSpinner = findViewById(R.id.districtSpinner_edit);
        wardSpinner = findViewById(R.id.wardSpinner_edit);
        detailAdressTv = findViewById(R.id.edt_edit_address);
        saveBTN = findViewById(R.id.btn_save_edit);
        checkBoxDefault=findViewById(R.id.checkBox_edit);

        imgv_back.setOnClickListener(view -> {
            finish();
        });

        addressId = getIntent().getStringExtra("address_id");
        addressRef = FirebaseDatabase.getInstance().getReference("Account_Address").child(addressId);
        loadAddressData();
        // Sự kiện Chọn tỉnh
        ProvinceSelection();
        //Sự kiện Chọn quận huyện
        DistrictSelection();
        //Sự kiện Chọn xã/phường
        WardSelection();
        saveBTN.setOnClickListener(v -> {
            String detailAddress = detailAdressTv.getText().toString().trim();
            accountAddress.setIs_default(checkBoxDefault.isChecked());

            if (checkBoxDefault.isChecked()) {
                updateOtherAddressesToFalse(() -> { // Callback sau khi cập nhật xong các địa chỉ khác
                    // Cập nhật địa chỉ hiện tại
                    if (!detailAddress.isEmpty()) {
                        accountAddress.setAddress_details(detailAddress);
                    } else {
                        Toast.makeText(EditAddressActivity.this, "Please enter the detail address.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (provinceSpinner.getSelectedItem() != null && districtSpinner.getSelectedItem() != null && wardSpinner.getSelectedItem() != null) {
                        addressRef.setValue(accountAddress).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(EditAddressActivity.this, "Address updated.", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(EditAddressActivity.this, "Failed to edit address.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(EditAddressActivity.this, "Please select information.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // Nếu không chọn làm mặc định thì chỉ cập nhật địa chỉ hiện tại
                addressRef.setValue(accountAddress).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(EditAddressActivity.this, "Address updated.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(EditAddressActivity.this, "Failed to edit address.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
    private void updateOtherAddressesToFalse(Runnable onComplete) {
        String userId = accountAddress.getAccount_id();
        DatabaseReference addressRef2 = FirebaseDatabase.getInstance().getReference("Account_Address");

        addressRef2.orderByChild("account_id").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot addressSnapshot : snapshot.getChildren()) {
                    Account_Address address = addressSnapshot.getValue(Account_Address.class);

                    if (address != null && !address.getAccount_address_id().equals(accountAddress.getAccount_address_id())) {
                        address.setIs_default(false);
                        addressSnapshot.getRef().setValue(address);
                    }
                }
                onComplete.run();  // Gọi callback sau khi hoàn thành
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu có
            }
        });
    }


    private void loadAddressData() {
        addressRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                     accountAddress = snapshot.getValue(Account_Address.class);
                    detailAdressTv.setText(accountAddress.getAddress_details());
                     checkBoxDefault.setChecked(accountAddress.getIs_default());
//                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu có.
            }
        });
    }
    private void removeInvalidItems(ArrayAdapter<Province> provinceAdapter) {
        // Duyệt qua adapter từ cuối đến đầu
        for (int i = provinceAdapter.getCount() - 1; i >= 0; i--) {
            Province province = provinceAdapter.getItem(i);
            // Kiểm tra nếu provinceId là "-1"
            if (province != null && province.getProvinceId().equals("-1")) {
                // Xóa mục không hợp lệ
                provinceAdapter.remove(province);
            }
        }
    }
    private void WardSelection() {
        wardSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                Ward selectedWard = (Ward) adapterView.getItemAtPosition(position);
                accountAddress.setWard(selectedWard.getWardId() + "+" + selectedWard.getWardName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void DistrictSelection() {
        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                District selectedDistrict = (District) parentView.getItemAtPosition(position);
                accountAddress.setDistrict(selectedDistrict.getDistrictId() + "+" + selectedDistrict.getDistrictName());
                new FetchWardsTask().execute("https://esgoo.net/api-tinhthanh/3/" + selectedDistrict.getDistrictId() + ".htm");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }

    private void ProvinceSelection() {
        new EditAddressActivity.FetchProvincesTask().execute("https://esgoo.net/api-tinhthanh/1/0.htm");
//        provinceSpinner.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
//                    removeInvalidItems(provinceAdapter); // Gọi phương thức xóa các mục không hợp lệ
//                    provinceAdapter.notifyDataSetChanged(); // Cập nhật adapter
//                }
//                return false; // Trả về false để cho p
//            }
//        });

        provinceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Province selectedProvince = (Province) parentView.getItemAtPosition(position);
                accountAddress.setProvince(selectedProvince.getProvinceId() + "+" + selectedProvince.getProvinceName());
                new EditAddressActivity.FetchDistrictsTask().execute("https://esgoo.net/api-tinhthanh/2/" + selectedProvince.getProvinceId() + ".htm");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }

    private class FetchProvincesTask extends AsyncTask<String, Void, List<Province>> {
        @Override
        protected List<Province> doInBackground(String... strings) {
            return fetchData(strings[0], this::parseProvinces);
        }

        @Override
        protected void onPostExecute(List<Province> provinces) {
            if (provinces != null) {
                provinces.add(0, new Province("-1", "Select Province", ""));
                provinceAdapter = new ArrayAdapter<>(EditAddressActivity.this, android.R.layout.simple_spinner_item, provinces);
                provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                provinceSpinner.setAdapter(provinceAdapter);
            } else {
                Toast.makeText(EditAddressActivity.this, "Failed to load provinces", Toast.LENGTH_SHORT).show();
            }
        }

        private List<Province> parseProvinces(String json) {
            List<Province> provinceList = new ArrayList<>();
            try {
                JSONObject jsonObject = new JSONObject(json);
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject provinceObject = jsonArray.getJSONObject(i);
                    provinceList.add(new Province(
                            provinceObject.getString("id"),
                            provinceObject.getString("full_name")
                    ));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return provinceList;
        }
    }


    private class FetchDistrictsTask extends AsyncTask<String, Void, List<District>> {
        @Override
        protected List<District> doInBackground(String... strings) {
            return fetchData(strings[0], this::parseDistricts);
        }

        @Override
        protected void onPostExecute(List<District> districts) {
            if (districts != null) {
                districts.add(0, new District("-1", "Select District"));
                districtAdapter = new ArrayAdapter<>(EditAddressActivity.this, android.R.layout.simple_spinner_item, districts);
                districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                districtSpinner.setAdapter(districtAdapter);
            } else {
                Toast.makeText(EditAddressActivity.this, "Failed to load districts", Toast.LENGTH_SHORT).show();
            }
        }

        private List<District> parseDistricts(String json) {
            List<District> districtList = new ArrayList<>();

            try {
                JSONObject jsonObject = new JSONObject(json);
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject districtObject = jsonArray.getJSONObject(i);
                    districtList.add(new District(
                            districtObject.getString("id"),
                            districtObject.getString("full_name")
                            //districtObject.getString("full_name")
                    ));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return districtList;
        }
    }

    private class FetchWardsTask extends AsyncTask<String, Void, List<Ward>> {
        @Override
        protected List<Ward> doInBackground(String... strings) {
            return fetchData(strings[0], this::parseWards);
        }

        @Override
        protected void onPostExecute(List<Ward> wards) {
            if (wards != null) {
                wards.add(0, new Ward("-1", "Select Ward"));
                wardAdapter = new ArrayAdapter<>(EditAddressActivity.this, android.R.layout.simple_spinner_item, wards);
                wardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                wardSpinner.setAdapter(wardAdapter);
            } else {
                Toast.makeText(EditAddressActivity.this, "Failed to load wards", Toast.LENGTH_SHORT).show();
            }
        }

        private List<Ward> parseWards(String json) {
            List<Ward> wardList = new ArrayList<>();

            try {
                JSONObject jsonObject = new JSONObject(json);
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject wardObject = jsonArray.getJSONObject(i);
                    wardList.add(new Ward(
                            wardObject.getString("id"),
                            wardObject.getString("full_name")
                            //wardObject.getString("full_name")
                    ));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return wardList;
        }
    }

    private <T> List<T> fetchData(String urlString, Function<String, List<T>> parser) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();
            return parser.apply(result.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
