package com.example.uiux.Activities.User;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.uiux.Model.Account_Address;
import com.example.uiux.Model.District;
import com.example.uiux.Model.Province;
import com.example.uiux.Model.Ward;
import com.example.uiux.R;
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

public class EditAddressActivity extends AppCompatActivity {

    private Spinner provinceSpinner, districtSpinner, wardSpinner;
    private ArrayAdapter<Province> provinceAdapter;
    private ArrayAdapter<District> districtAdapter;
    private ArrayAdapter<Ward> wardAdapter;
    private EditText detailAdressTv;
    //private DatabaseReference databaseReference;
    private Button saveBTN;
    private CheckBox checkBoxDefault;
    Account_Address accountAddress;
    private DatabaseReference addressRef;
    private String addressId;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_address);

        provinceSpinner = findViewById(R.id.provinceSpinner);
        districtSpinner = findViewById(R.id.districtSpinner);
        wardSpinner = findViewById(R.id.wardSpinner);
        detailAdressTv = findViewById(R.id.detailAddress);
        saveBTN = findViewById(R.id.btnSave);
        checkBoxDefault=findViewById(R.id.checkBox);

        addressId = getIntent().getStringExtra("address_id");
        addressRef = FirebaseDatabase.getInstance().getReference("Account_Address").child(addressId);
        loadAddressData();
        // Sự kiện Chọn tỉnh
        ProvinceSelection();
        //Sự kiện Chọn quận huyện
        DistrictSelection();
        //Sự kiện Chọn xã/phường
        Wardselection();
        saveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String detailAddress = detailAdressTv.getText().toString().trim();
                accountAddress.setIs_default(checkBoxDefault.isChecked());

                if (checkBoxDefault.isChecked()) {
                    updateOtherAddressesToFalse(() -> { // Callback sau khi cập nhật xong các địa chỉ khác
                        // Cập nhật địa chỉ hiện tại
                        if (!detailAddress.isEmpty()) {
                            accountAddress.setAddress_details(detailAddress);
                        } else {
                            Toast.makeText(EditAddressActivity.this, "Vui lòng nhập địa chỉ chi tiết", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (provinceSpinner.getSelectedItem() != null && districtSpinner.getSelectedItem() != null && wardSpinner.getSelectedItem() != null) {
                            addressRef.setValue(accountAddress).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(EditAddressActivity.this, "Địa chỉ đã được cập nhật", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(EditAddressActivity.this, "Lỗi khi cập nhật địa chỉ", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(EditAddressActivity.this, "Vui lòng chọn đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Nếu không chọn làm mặc định thì chỉ cập nhật địa chỉ hiện tại
                    addressRef.setValue(accountAddress).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(EditAddressActivity.this, "Địa chỉ đã được cập nhật", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(EditAddressActivity.this, "Lỗi khi cập nhật địa chỉ", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
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
    private void Wardselection() {
        wardSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Ward selectedWard = (Ward) adapterView.getItemAtPosition(i);

                Toast.makeText(getApplicationContext(), "Chọn ward: " + selectedWard.getWardName(), Toast.LENGTH_SHORT).show();
                accountAddress.setWard(selectedWard.getWardId()+"+"+ selectedWard.getWardName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    private void DistrictSelection() {
        // Xử lý sự kiện chọn quận/huyện
        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                District selectedDistrict = (District) parentView.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(), "Chọn quận/huyện: " + selectedDistrict.getDistrictName(), Toast.LENGTH_SHORT).show();
                accountAddress.setDistrict(selectedDistrict.getDistrictId()+"+"+selectedDistrict.getDistrictName());

                // Gọi API để lấy danh sách phường/xã theo district_id
                new FetchWardsTask().execute("https://vapi.vnappmob.com/api/province/ward/" + selectedDistrict.getDistrictId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Không có gì được chọn
            }
        });
    }
    private void ProvinceSelection() {

        new FetchProvincesTask().execute("https://vapi.vnappmob.com/api/province/");
        // Xử lý sự kiện chọn tỉnh
        provinceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Province selectedProvince = (Province) parentView.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(), "Chọn tỉnh: " + selectedProvince.getProvinceName(), Toast.LENGTH_SHORT).show();
                accountAddress.setProvince(selectedProvince.getProvinceId()+"+"+selectedProvince.getProvinceName());

                // Gọi API để lấy danh sách quận/huyện theo province_id
                new FetchDistrictsTask().execute("https://vapi.vnappmob.com/api/province/district/" + selectedProvince.getProvinceId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Không có gì được chọn
            }
        });
    }
    // AsyncTask để lấy danh sách tỉnh từ API
    private class FetchProvincesTask extends AsyncTask<String, Void, List<Province>> {

        @Override
        protected List<Province> doInBackground(String... strings) {
            String urlString = strings[0];
            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(5000);

                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();

                return parseProvinces(result.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Province> provinces) {
            super.onPostExecute(provinces);
            if (provinces != null) {
                // Thêm hint vào danh sách
                String[] Province=accountAddress.getProvince().split("\\+");
                provinces.add(0, new Province("-1", Province[1], ""));

                provinceAdapter = new ArrayAdapter<>(EditAddressActivity.this, android.R.layout.simple_spinner_item, provinces);
                provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                provinceSpinner.setAdapter(provinceAdapter);
            } else {
                Toast.makeText(EditAddressActivity.this, "Không thể lấy dữ liệu!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    // AsyncTask để lấy danh sách quận/huyện từ API
    private class FetchDistrictsTask extends AsyncTask<String, Void, List<District>> {

        @Override
        protected List<District> doInBackground(String... strings) {
            String urlString = strings[0];
            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(5000);

                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();

                return parseDistricts(result.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<District> districts) {
            super.onPostExecute(districts);
            if (districts != null) {
                // Thêm hint vào danh sách
                String[] District=accountAddress.getDistrict().split("\\+");
                districts.add(0, new District("-1", District[1]));

                districtAdapter = new ArrayAdapter<>(EditAddressActivity.this, android.R.layout.simple_spinner_item, districts);
                districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                districtSpinner.setAdapter(districtAdapter);
            } else {
                Toast.makeText(EditAddressActivity.this, "Không thể lấy danh sách quận/huyện!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    // AsyncTask để lấy danh sách phường/xã từ API
    private class FetchWardsTask extends AsyncTask<String, Void, List<Ward>> {

        @Override
        protected List<Ward> doInBackground(String... strings) {
            String urlString = strings[0];
            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(5000);

                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();

                return parseWards(result.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Ward> wards) {
            super.onPostExecute(wards);
            if (wards != null) {
                // Thêm mục hint vào danh sách
                String[] Ward=accountAddress.getWard().split("\\+");
                wards.add(0, new Ward("-1", Ward[1]));

                wardAdapter = new ArrayAdapter<>(EditAddressActivity.this, android.R.layout.simple_spinner_item, wards);
                wardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                wardSpinner.setAdapter(wardAdapter);
            } else {
                Toast.makeText(EditAddressActivity.this, "Không thể lấy danh sách phường/xã!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    // Phân tích JSON thành danh sách các đối tượng Province
    private List<Province> parseProvinces(String json) {
        List<Province> provinceList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray resultsArray = jsonObject.getJSONArray("results");

            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject provinceObject = resultsArray.getJSONObject(i);
                String id = provinceObject.getString("province_id");
                String name = provinceObject.getString("province_name");

                Province province = new Province(id, name, "");
                provinceList.add(province);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return provinceList;
    }
    // Phân tích JSON thành danh sách các đối tượng District
    private List<District> parseDistricts(String json) {
        List<District> districtList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray resultsArray = jsonObject.getJSONArray("results");

            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject districtObject = resultsArray.getJSONObject(i);
                String id = districtObject.getString("district_id");
                String name = districtObject.getString("district_name");

                District district = new District(id, name);
                districtList.add(district);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return districtList;
    }
    // Phân tích JSON thành danh sách các đối tượng Ward
    private List<Ward> parseWards(String json) {
        List<Ward> wardList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray resultsArray = jsonObject.getJSONArray("results");

            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject wardObject = resultsArray.getJSONObject(i);
                String id = wardObject.getString("ward_id");
                String name = wardObject.getString("ward_name");

                Ward ward = new Ward(id, name);
                wardList.add(ward);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return wardList;
    }
}
