package com.example.uiux.Activities.User.Profile;

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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.uiux.Model.Account_Address;
import com.example.uiux.Model.District;
import com.example.uiux.Model.Province;
import com.example.uiux.Model.Ward;
import com.example.uiux.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
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
import java.util.function.Function;

//public class AddressActivity extends AppCompatActivity {
//    private Spinner provinceSpinner, districtSpinner, wardSpinner;
//    private ArrayAdapter<Province> provinceAdapter;
//    private ArrayAdapter<District> districtAdapter;
//    private ArrayAdapter<Ward> wardAdapter;
//    private TextInputEditText edt_address;
//    private DatabaseReference databaseReference;
//    private MaterialButton saveBTN, updateAddressBTN;
//    private CheckBox checkBoxDefault;
//    private ImageView imgv_back;
//    Account_Address accountAddress;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
//        setContentView(R.layout.activity_address);
//        // Khởi tạo spinner cho danh sách tỉnh, quận/huyện và phường/xã
//        imgv_back = findViewById(R.id.img_back_new_address);
//        provinceSpinner = findViewById(R.id.provinceSpinner);
//        districtSpinner = findViewById(R.id.districtSpinner);
//        wardSpinner = findViewById(R.id.wardSpinner);
//        edt_address =findViewById(R.id.edt_new_address);
//        saveBTN=findViewById(R.id.saveBTN);
//        checkBoxDefault=findViewById(R.id.checkBox);
//        //updateAddressBTN=findViewById(R.id.addressUpdate);
//        accountAddress=new Account_Address();
//        databaseReference = FirebaseDatabase.getInstance().getReference("Account_Address");
//        Intent intent= getIntent();
//        String account_id= intent.getStringExtra("account_id");
//        accountAddress.setAccount_id(account_id);
//        // Sự kiện Chọn tỉnh
//        ProvinceSelection();
//        //Sự kiện Chọn quận huyện
//        DistrictSelection();
//        //Sự kiện Chọn xã/phường
//        Wardselection();
//
//        imgv_back.setOnClickListener(view -> {
//            finish();
//        });
//        checkBoxDefault.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                accountAddress.setIs_default(checkBoxDefault.isChecked());
//            }
//        });
//        saveBTN.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                uploadAddressToFirebase();
//                Toast.makeText(getApplicationContext(), "Uploaded", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void Wardselection() {
//        wardSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                Ward selectedWard = (Ward) adapterView.getItemAtPosition(i);
//
//                Toast.makeText(getApplicationContext(), "Select ward: " + selectedWard.getWardName(), Toast.LENGTH_SHORT).show();
//                accountAddress.setWard(selectedWard.getWardId()+"+"+ selectedWard.getWardName());
//
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//    }
//
//    private void DistrictSelection() {
//        // Xử lý sự kiện chọn quận/huyện
//        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//                District selectedDistrict = (District) parentView.getItemAtPosition(position);
//                Toast.makeText(getApplicationContext(), "Select district: " + selectedDistrict.getDistrictName(), Toast.LENGTH_SHORT).show();
//                accountAddress.setDistrict(selectedDistrict.getDistrictId()+"+"+selectedDistrict.getDistrictName());
//
//                // Gọi API để lấy danh sách phường/xã theo district_id
//                new FetchWardsTask().execute("https://vapi.vnappmob.com/api/province/ward/" + selectedDistrict.getDistrictId());
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parentView) {
//                // Không có gì được chọn
//            }
//        });
//    }
//
//    private void ProvinceSelection() {
//
//        new FetchProvincesTask().execute("https://vapi.vnappmob.com/api/province/");
//
//
//        // Xử lý sự kiện chọn tỉnh
//        provinceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//                Province selectedProvince = (Province) parentView.getItemAtPosition(position);
//                Toast.makeText(getApplicationContext(), "Select province: " + selectedProvince.getProvinceName(), Toast.LENGTH_SHORT).show();
//                accountAddress.setProvince(selectedProvince.getProvinceId()+"+"+selectedProvince.getProvinceName());
//
//                // Gọi API để lấy danh sách quận/huyện theo province_id
//                new FetchDistrictsTask().execute("https://vapi.vnappmob.com/api/province/district/" + selectedProvince.getProvinceId());
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parentView) {
//                // Không có gì được chọn
//            }
//        });
//    }
//
//    // AsyncTask để lấy danh sách tỉnh từ API
//    private class FetchProvincesTask extends AsyncTask<String, Void, List<Province>> {
//
//        @Override
//        protected List<Province> doInBackground(String... strings) {
//            String urlString = strings[0];
//            try {
//                URL url = new URL(urlString);
//                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setRequestMethod("GET");
//                urlConnection.setConnectTimeout(5000);
//
//                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
//                StringBuilder result = new StringBuilder();
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    result.append(line);
//                }
//                reader.close();
//
//                return parseProvinces(result.toString());
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(List<Province> provinces) {
//            super.onPostExecute(provinces);
//            if (provinces != null) {
//                // Thêm hint vào danh sách
//                provinces.add(0, new Province("-1", "Select Province", ""));
//
//                provinceAdapter = new ArrayAdapter<>(AddressActivity.this, android.R.layout.simple_spinner_item, provinces);
//                provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                provinceSpinner.setAdapter(provinceAdapter);
//            } else {
//                Toast.makeText(AddressActivity.this, "Cannot get provinces data!", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//    private void uploadAddressToFirebase() {
//        String detailAddress = edt_address.getText().toString();
//        if (!detailAddress.isEmpty()) {
//            accountAddress.setAddress_details(detailAddress);
//            String id = databaseReference.push().getKey();
//            if (id != null) {
//                accountAddress.setAccount_address_id(id);
//                databaseReference.child(id).setValue(accountAddress)
//                        .addOnCompleteListener(task -> {
//                            if (task.isSuccessful()) {
//                                Toast.makeText(AddressActivity.this, "Address successfully saved", Toast.LENGTH_SHORT).show();
//                            } else {
//                                Toast.makeText(AddressActivity.this, "Failed to save address!", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//            }
//        } else {
//            Toast.makeText(AddressActivity.this, "Please enter the detail address!", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    // AsyncTask để lấy danh sách quận/huyện từ API
//    private class FetchDistrictsTask extends AsyncTask<String, Void, List<District>> {
//
//        @Override
//        protected List<District> doInBackground(String... strings) {
//            String urlString = strings[0];
//            try {
//                URL url = new URL(urlString);
//                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setRequestMethod("GET");
//                urlConnection.setConnectTimeout(5000);
//
//                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
//                StringBuilder result = new StringBuilder();
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    result.append(line);
//                }
//                reader.close();
//
//                return parseDistricts(result.toString());
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(List<District> districts) {
//            super.onPostExecute(districts);
//            if (districts != null) {
//                // Thêm hint vào danh sách
//                districts.add(0, new District("-1", "Select District"));
//
//                districtAdapter = new ArrayAdapter<>(AddressActivity.this, android.R.layout.simple_spinner_item, districts);
//                districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                districtSpinner.setAdapter(districtAdapter);
//            } else {
//                Toast.makeText(AddressActivity.this, "Cannot get districts data!", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//    // AsyncTask để lấy danh sách phường/xã từ API
//    private class FetchWardsTask extends AsyncTask<String, Void, List<Ward>> {
//
//        @Override
//        protected List<Ward> doInBackground(String... strings) {
//            String urlString = strings[0];
//            try {
//                URL url = new URL(urlString);
//                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setRequestMethod("GET");
//                urlConnection.setConnectTimeout(5000);
//
//                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
//                StringBuilder result = new StringBuilder();
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    result.append(line);
//                }
//                reader.close();
//
//                return parseWards(result.toString());
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(List<Ward> wards) {
//            super.onPostExecute(wards);
//            if (wards != null) {
//                // Thêm mục hint vào danh sách
//                wards.add(0, new Ward("-1", "Select ward"));
//
//                wardAdapter = new ArrayAdapter<>(AddressActivity.this, android.R.layout.simple_spinner_item, wards);
//                wardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                wardSpinner.setAdapter(wardAdapter);
//            } else {
//                Toast.makeText(AddressActivity.this, "Cannot get wards data!", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    // Phân tích JSON thành danh sách các đối tượng Province
//    private List<Province> parseProvinces(String json) {
//        List<Province> provinceList = new ArrayList<>();
//        try {
//            JSONObject jsonObject = new JSONObject(json);
//            JSONArray resultsArray = jsonObject.getJSONArray("results");
//
//            for (int i = 0; i < resultsArray.length(); i++) {
//                JSONObject provinceObject = resultsArray.getJSONObject(i);
//                String id = provinceObject.getString("province_id");
//                String name = provinceObject.getString("province_name");
//
//                Province province = new Province(id, name, "");
//                provinceList.add(province);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return provinceList;
//    }
//
//    // Phân tích JSON thành danh sách các đối tượng District
//    private List<District> parseDistricts(String json) {
//        List<District> districtList = new ArrayList<>();
//        try {
//            JSONObject jsonObject = new JSONObject(json);
//            JSONArray resultsArray = jsonObject.getJSONArray("results");
//
//            for (int i = 0; i < resultsArray.length(); i++) {
//                JSONObject districtObject = resultsArray.getJSONObject(i);
//                String id = districtObject.getString("district_id");
//                String name = districtObject.getString("district_name");
//
//                District district = new District(id, name);
//                districtList.add(district);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return districtList;
//    }
//
//    // Phân tích JSON thành danh sách các đối tượng Ward
//    private List<Ward> parseWards(String json) {
//        List<Ward> wardList = new ArrayList<>();
//        try {
//            JSONObject jsonObject = new JSONObject(json);
//            JSONArray resultsArray = jsonObject.getJSONArray("results");
//
//            for (int i = 0; i < resultsArray.length(); i++) {
//                JSONObject wardObject = resultsArray.getJSONObject(i);
//                String id = wardObject.getString("ward_id");
//                String name = wardObject.getString("ward_name");
//
//                Ward ward = new Ward(id, name);
//                wardList.add(ward);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return wardList;
//    }
//}

public class AddressActivity extends AppCompatActivity {
    private Spinner provinceSpinner, districtSpinner, wardSpinner;
    private ArrayAdapter<Province> provinceAdapter;
    private ArrayAdapter<District> districtAdapter;
    private ArrayAdapter<Ward> wardAdapter;
    private TextInputEditText edt_address;
    private DatabaseReference databaseReference;
    private MaterialButton saveBTN;
    private CheckBox checkBoxDefault;
    private ImageView imgv_back;
    Account_Address accountAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
        setContentView(R.layout.activity_address);

        imgv_back = findViewById(R.id.img_back_new_address);
        provinceSpinner = findViewById(R.id.provinceSpinner);
        districtSpinner = findViewById(R.id.districtSpinner);
        wardSpinner = findViewById(R.id.wardSpinner);
        edt_address = findViewById(R.id.edt_new_address);
        saveBTN = findViewById(R.id.saveBTN);
        checkBoxDefault = findViewById(R.id.checkBox);
        accountAddress = new Account_Address();
        databaseReference = FirebaseDatabase.getInstance().getReference("Account_Address");

        Intent intent = getIntent();
        String account_id = intent.getStringExtra("account_id");
        accountAddress.setAccount_id(account_id);

        ProvinceSelection();
        DistrictSelection();
        WardSelection();

        imgv_back.setOnClickListener(view -> finish());

        checkBoxDefault.setOnCheckedChangeListener((compoundButton, isChecked) -> accountAddress.setIs_default(isChecked));

        saveBTN.setOnClickListener(view -> {
            uploadAddressToFirebase();
            Toast.makeText(getApplicationContext(), "Uploaded", Toast.LENGTH_SHORT).show();
        });
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
        new FetchProvincesTask().execute("https://esgoo.net/api-tinhthanh/1/0.htm");

        provinceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Province selectedProvince = (Province) parentView.getItemAtPosition(position);
                accountAddress.setProvince(selectedProvince.getProvinceId() + "+" + selectedProvince.getProvinceName());
                new FetchDistrictsTask().execute("https://esgoo.net/api-tinhthanh/2/" + selectedProvince.getProvinceId() + ".htm");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }

    private void uploadAddressToFirebase() {
        String detailAddress = edt_address.getText().toString();
        if (!detailAddress.isEmpty()) {
            accountAddress.setAddress_details(detailAddress);
            String id = databaseReference.push().getKey();
            if (id != null) {
                accountAddress.setAccount_address_id(id);
                databaseReference.child(id).setValue(accountAddress)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(AddressActivity.this, "Address successfully saved", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(AddressActivity.this, "Failed to save address!", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        } else {
            Toast.makeText(AddressActivity.this, "Please enter the detail address!", Toast.LENGTH_SHORT).show();
        }
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
                provinceAdapter = new ArrayAdapter<>(AddressActivity.this, android.R.layout.simple_spinner_item, provinces);
                provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                provinceSpinner.setAdapter(provinceAdapter);
            } else {
                Toast.makeText(AddressActivity.this, "Failed to load provinces", Toast.LENGTH_SHORT).show();
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
                districtAdapter = new ArrayAdapter<>(AddressActivity.this, android.R.layout.simple_spinner_item, districts);
                districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                districtSpinner.setAdapter(districtAdapter);
            } else {
                Toast.makeText(AddressActivity.this, "Failed to load districts", Toast.LENGTH_SHORT).show();
            }
        }

        private List<District> parseDistricts(String json) {
            List<District> districtList = new ArrayList<>();
//            try {
//                JSONArray jsonArray = new JSONArray(json);
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    JSONObject districtObject = jsonArray.getJSONObject(i);
//                    districtList.add(new District(districtObject.getString("id"), districtObject.getString("name")));
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            return districtList;

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
                wardAdapter = new ArrayAdapter<>(AddressActivity.this, android.R.layout.simple_spinner_item, wards);
                wardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                wardSpinner.setAdapter(wardAdapter);
            } else {
                Toast.makeText(AddressActivity.this, "Failed to load wards", Toast.LENGTH_SHORT).show();
            }
        }

        private List<Ward> parseWards(String json) {
            List<Ward> wardList = new ArrayList<>();
//            try {
//                JSONArray jsonArray = new JSONArray(json);
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    JSONObject wardObject = jsonArray.getJSONObject(i);
//                    wardList.add(new Ward(wardObject.getString("id"), wardObject.getString("name")));
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            return wardList;

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

