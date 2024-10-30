package com.example.uiux.Activities.Admin.Branch;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.uiux.Activities.Admin.Supplies.SuppliesActivity;
import com.example.uiux.Model.BranchStore;
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

public class BranchStoreActivity extends AppCompatActivity {
    private Spinner provinceSpinner, districtSpinner, wardSpinner,statusSpinner;
    private ArrayAdapter<Province> provinceAdapter;
    private ArrayAdapter<District> districtAdapter;
    private ArrayAdapter<Ward> wardAdapter;
    private TextInputEditText edt_address,edt_branch_name;
    private DatabaseReference databaseReference;
    private MaterialButton saveBTN, updateAddressBTN;
    private ImageView imgv_back;
    BranchStore branchStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_branch_store);
        imgv_back = findViewById(R.id.img_back_new_address);
        provinceSpinner = findViewById(R.id.provinceSpinner);
        districtSpinner = findViewById(R.id.districtSpinner);
        wardSpinner = findViewById(R.id.wardSpinner);
        statusSpinner=findViewById(R.id.statusSpinner);
        edt_branch_name=findViewById(R.id.edt_branch_name);
        edt_address =findViewById(R.id.edt_new_address);
        saveBTN=findViewById(R.id.saveBTN);

        //updateAddressBTN=findViewById(R.id.addressUpdate);
        branchStore=new BranchStore();
        databaseReference = FirebaseDatabase.getInstance().getReference("Branch Store");

//        // Sự kiện Chọn tỉnh
//        ProvinceSelection();
//        //Sự kiện Chọn quận huyện
//        DistrictSelection();
//        //Sự kiện Chọn xã/phường
//        Wardselection();
//        FectchSpinnerStatus();
//
//        imgv_back.setOnClickListener(view -> {
//            finish();
//        });
//        saveBTN.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                uploadBranchStoreToFirebase();
//                //Toast.makeText(getApplicationContext(), "Uploaded", Toast.LENGTH_SHORT).show();
//            }
//        });

    }
//    private void FectchSpinnerStatus() {
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(BranchStoreActivity.this,
//                R.array.branch_store_status, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        statusSpinner.setAdapter(adapter);
//    }
//    private void Wardselection() {
//        wardSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                Ward selectedWard = (Ward) adapterView.getItemAtPosition(i);
//
//                Toast.makeText(getApplicationContext(), "Select ward: " + selectedWard.getWardName(), Toast.LENGTH_SHORT).show();
//                branchStore.setWard(selectedWard.getWardId()+"+"+ selectedWard.getWardName());
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
//                branchStore.setDistrict(selectedDistrict.getDistrictId()+"+"+selectedDistrict.getDistrictName());
//
//                // Gọi API để lấy danh sách phường/xã theo district_id
//                new BranchStoreActivity.FetchWardsTask().execute("https://vapi.vnappmob.com/api/province/ward/" + selectedDistrict.getDistrictId());
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
//        new BranchStoreActivity.FetchProvincesTask().execute("https://vapi.vnappmob.com/api/province/");
//
//
//        // Xử lý sự kiện chọn tỉnh
//        provinceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//                Province selectedProvince = (Province) parentView.getItemAtPosition(position);
//                Toast.makeText(getApplicationContext(), "Select province: " + selectedProvince.getProvinceName(), Toast.LENGTH_SHORT).show();
//                branchStore.setProvince(selectedProvince.getProvinceId()+"+"+selectedProvince.getProvinceName());
//
//                // Gọi API để lấy danh sách quận/huyện theo province_id
//                new BranchStoreActivity.FetchDistrictsTask().execute("https://vapi.vnappmob.com/api/province/district/" + selectedProvince.getProvinceId());
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
//                provinceAdapter = new ArrayAdapter<>(BranchStoreActivity.this, android.R.layout.simple_spinner_item, provinces);
//                provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                provinceSpinner.setAdapter(provinceAdapter);
//            } else {
//                Toast.makeText(BranchStoreActivity.this, "Cannot get provinces data!", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//    private void uploadBranchStoreToFirebase() {
//        String detailAddress = edt_address.getText().toString();
//        String branchName=edt_branch_name.getText().toString();
//        if (!detailAddress.isEmpty()&&!branchName.isEmpty()) {
//            branchStore.setAddress_details(detailAddress);
//            branchStore.setBranch_name(branchName);
//            branchStore.setStatus( statusSpinner.getSelectedItemPosition());
//            String id = databaseReference.push().getKey();
//            if (id != null) {
//                branchStore.setBranch_Store_id(id);
//                databaseReference.child(id).setValue(branchStore)
//                        .addOnCompleteListener(task -> {
//                            if (task.isSuccessful()) {
//                                Toast.makeText(BranchStoreActivity.this, "BranchStore successfully saved", Toast.LENGTH_SHORT).show();
//                            } else {
//                                Toast.makeText(BranchStoreActivity.this, "Failed to save BranchStore!", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//            }
//        } else {
//            Toast.makeText(BranchStoreActivity.this, "Please enter the detail address!", Toast.LENGTH_SHORT).show();
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
//                districtAdapter = new ArrayAdapter<>(BranchStoreActivity.this, android.R.layout.simple_spinner_item, districts);
//                districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                districtSpinner.setAdapter(districtAdapter);
//            } else {
//                Toast.makeText(BranchStoreActivity.this, "Cannot get districts data!", Toast.LENGTH_SHORT).show();
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
//                wardAdapter = new ArrayAdapter<>(BranchStoreActivity.this, android.R.layout.simple_spinner_item, wards);
//                wardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                wardSpinner.setAdapter(wardAdapter);
//            } else {
//                Toast.makeText(BranchStoreActivity.this, "Cannot get wards data!", Toast.LENGTH_SHORT).show();
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
}