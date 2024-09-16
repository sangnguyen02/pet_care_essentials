package com.example.uiux.Activities.User;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.uiux.Model.Account_Address;
import com.example.uiux.Model.District;
import com.example.uiux.Model.Province;
import com.example.uiux.Model.Ward;
import com.example.uiux.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AddressActivity extends AppCompatActivity {

    private Spinner provinceSpinner, districtSpinner, wardSpinner;
    private ArrayAdapter<Province> provinceAdapter;
    private ArrayAdapter<District> districtAdapter;
    private ArrayAdapter<Ward> wardAdapter;
    private EditText detailAdressTv;
    private DatabaseReference databaseReference;
    private Button saveBTN,updateAddressBTN;
    private CheckBox checkBoxDefault;
    Account_Address accountAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        // Khởi tạo spinner cho danh sách tỉnh, quận/huyện và phường/xã
        provinceSpinner = findViewById(R.id.provinceSpinner);
        districtSpinner = findViewById(R.id.districtSpinner);
        wardSpinner = findViewById(R.id.wardSpinner);
        detailAdressTv=findViewById(R.id.detailAddress);
        saveBTN=findViewById(R.id.saveBTN);
        checkBoxDefault=findViewById(R.id.checkBox);
        updateAddressBTN=findViewById(R.id.addressUpdate);
        accountAddress=new Account_Address();
        databaseReference = FirebaseDatabase.getInstance().getReference("Account_Address");
        Intent intent= getIntent();
        String account_id= intent.getStringExtra("account_id");
        accountAddress.setAccount_id(account_id);
        // Sự kiện Chọn tỉnh
        ProvinceSelection();
        //Sự kiện Chọn quận huyện
        DistrictSelection();
        //Sự kiện Chọn xã/phường
        Wardselection();
        checkBoxDefault.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                accountAddress.setIs_default(checkBoxDefault.isChecked());
            }
        });
        saveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadAddressToFirebase();
                Toast.makeText(getApplicationContext(), "Da upload", Toast.LENGTH_SHORT).show();
            }
        });
        updateAddressBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gotoUpdate=new Intent(AddressActivity.this,UpdateAddressActivity.class);
                gotoUpdate.putExtra("account_id",account_id);
                startActivity(gotoUpdate);
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
                provinces.add(0, new Province("-1", "Chọn tỉnh", ""));

                provinceAdapter = new ArrayAdapter<>(AddressActivity.this, android.R.layout.simple_spinner_item, provinces);
                provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                provinceSpinner.setAdapter(provinceAdapter);
            } else {
                Toast.makeText(AddressActivity.this, "Không thể lấy dữ liệu!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void uploadAddressToFirebase() {
        String detailAddress = detailAdressTv.getText().toString();
        if (!detailAddress.isEmpty()) {
            accountAddress.setAddress_details(detailAddress);
            String id = databaseReference.push().getKey();
            if (id != null) {
                accountAddress.setAccount_address_id(id);
                databaseReference.child(id).setValue(accountAddress)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(AddressActivity.this, "Địa chỉ đã được lưu!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(AddressActivity.this, "Lưu địa chỉ thất bại!", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        } else {
            Toast.makeText(AddressActivity.this, "Vui lòng nhập địa chỉ chi tiết!", Toast.LENGTH_SHORT).show();
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
                districts.add(0, new District("-1", "Chọn quận/huyện"));

                districtAdapter = new ArrayAdapter<>(AddressActivity.this, android.R.layout.simple_spinner_item, districts);
                districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                districtSpinner.setAdapter(districtAdapter);
            } else {
                Toast.makeText(AddressActivity.this, "Không thể lấy danh sách quận/huyện!", Toast.LENGTH_SHORT).show();
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
                wards.add(0, new Ward("-1", "Chọn phường/xã"));

                wardAdapter = new ArrayAdapter<>(AddressActivity.this, android.R.layout.simple_spinner_item, wards);
                wardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                wardSpinner.setAdapter(wardAdapter);
            } else {
                Toast.makeText(AddressActivity.this, "Không thể lấy danh sách phường/xã!", Toast.LENGTH_SHORT).show();
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
