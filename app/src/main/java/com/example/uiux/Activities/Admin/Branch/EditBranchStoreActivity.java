package com.example.uiux.Activities.Admin.Branch;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uiux.Activities.User.Profile.EditAddressActivity;
import com.example.uiux.Model.Account_Address;
import com.example.uiux.Model.BranchStore;
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

public class EditBranchStoreActivity extends AppCompatActivity {
    private Spinner provinceSpinner, districtSpinner, wardSpinner,statusSpinner;
    private ArrayAdapter<Province> provinceAdapter;
    private ArrayAdapter<District> districtAdapter;
    private ArrayAdapter<Ward> wardAdapter;
    private TextInputEditText edt_address,edt_branch_name;
    private DatabaseReference branchStoreRef;
    private MaterialButton saveBTN, updateAddressBTN;
    private ImageView imgv_back;
    BranchStore branchStore;
    private String branchStore_id;
    private String selectedProvinceName = "";
    private String selectedDistrictName = "";
    private String selectedWardName = "";
    private String detailedStreetAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
        setContentView(R.layout.activity_edit_branch_store);
        imgv_back = findViewById(R.id.img_back_new_address);
        provinceSpinner = findViewById(R.id.provinceSpinner);
        districtSpinner = findViewById(R.id.districtSpinner);
        wardSpinner = findViewById(R.id.wardSpinner);
        statusSpinner=findViewById(R.id.statusSpinner);
        edt_branch_name=findViewById(R.id.edt_branch_name);
        edt_address =findViewById(R.id.edt_new_address);
        saveBTN=findViewById(R.id.saveBTN);
        imgv_back.setOnClickListener(view -> {
            finish();
        });

        branchStore_id = getIntent().getStringExtra("branchStore_id");
        branchStoreRef = FirebaseDatabase.getInstance().getReference("Branch Store").child(branchStore_id);
        loadBranchStoreData();
        // Sự kiện Chọn tỉnh
        ProvinceSelection();
        //Sự kiện Chọn quận huyện
        DistrictSelection();
        //Sự kiện Chọn xã/phường
        WardSelection();
        FectchSpinnerStatus();
        saveBTN.setOnClickListener(v -> {
            detailedStreetAddress = edt_address.getText().toString().trim();
            String branch_name=edt_branch_name.getText().toString().trim();
            branchStore.setStatus( statusSpinner.getSelectedItemPosition());
            if (detailedStreetAddress.isEmpty() || branch_name.isEmpty()) {
                Toast.makeText(EditBranchStoreActivity.this, "Please enter all details.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Construct the full address
            String detail_address = String.format("%s, %s, %s, %s",
                    detailedStreetAddress, selectedWardName, selectedDistrictName, selectedProvinceName);
            if (!detail_address.isEmpty()&&!branch_name.isEmpty()) {
                branchStore.setAddress_details(detail_address);
                branchStore.setBranch_name(branch_name);
            } else {
                Toast.makeText(EditBranchStoreActivity.this, "Please enter the detail address.", Toast.LENGTH_SHORT).show();
                return;
            }
                // Nếu không chọn làm mặc định thì chỉ cập nhật địa chỉ hiện tại
                branchStoreRef.setValue(branchStore).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(EditBranchStoreActivity.this, "Branch Store updated.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(EditBranchStoreActivity.this, "Failed to edit Branch Store.", Toast.LENGTH_SHORT).show();
                    }
                });

        });
    }
    private void FectchSpinnerStatus() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(EditBranchStoreActivity.this,
                R.array.branch_store_status, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);
    }



    private void loadBranchStoreData() {
        branchStoreRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    branchStore = snapshot.getValue(BranchStore.class);
                    edt_address.setText(branchStore.getAddress_details());
                    edt_branch_name.setText(branchStore.getBranch_name());
                    statusSpinner.setSelection(branchStore.getStatus());
//                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu có.
            }
        });
    }

    private void WardSelection() {
        wardSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                Ward selectedWard = (Ward) adapterView.getItemAtPosition(position);
                selectedWardName=selectedWard.getWardName();
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
                selectedDistrictName=selectedDistrict.getDistrictName();
                new EditBranchStoreActivity.FetchWardsTask().execute("https://esgoo.net/api-tinhthanh/3/" + selectedDistrict.getDistrictId() + ".htm");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }
    private void ProvinceSelection() {
        new FetchProvincesTask().execute("https://esgoo.net/api-tinhthanh/1/0.htm");
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
                selectedDistrictName=selectedProvince.getProvinceName();
                new FetchDistrictsTask().execute("https://esgoo.net/api-tinhthanh/2/" + selectedProvince.getProvinceId() + ".htm");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
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



    private class FetchProvincesTask extends AsyncTask<String, Void, List<Province>> {
        @Override
        protected List<Province> doInBackground(String... strings) {
            return fetchData(strings[0], this::parseProvinces);
        }

        @Override
        protected void onPostExecute(List<Province> provinces) {
            if (provinces != null) {
                provinces.add(0, new Province("-1", "Select Province", ""));
                provinceAdapter = new ArrayAdapter<>(EditBranchStoreActivity.this, android.R.layout.simple_spinner_item, provinces);
                provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                provinceSpinner.setAdapter(provinceAdapter);
            } else {
                Toast.makeText(EditBranchStoreActivity.this, "Failed to load provinces", Toast.LENGTH_SHORT).show();
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
                districtAdapter = new ArrayAdapter<>(EditBranchStoreActivity.this, android.R.layout.simple_spinner_item, districts);
                districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                districtSpinner.setAdapter(districtAdapter);
            } else {
                Toast.makeText(EditBranchStoreActivity.this, "Failed to load districts", Toast.LENGTH_SHORT).show();
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
                wardAdapter = new ArrayAdapter<>(EditBranchStoreActivity.this, android.R.layout.simple_spinner_item, wards);
                wardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                wardSpinner.setAdapter(wardAdapter);
            } else {
                Toast.makeText(EditBranchStoreActivity.this, "Failed to load wards", Toast.LENGTH_SHORT).show();
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