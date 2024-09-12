package com.example.uiux.Utils;
import com.example.uiux.Model.City;
import com.example.uiux.Model.District;
import com.example.uiux.Model.Ward;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
public interface ApiService {
    @GET("api/province")
    Call<List<City>> getProvinces();

    @GET("api/district")
    Call<List<District>> getDistricts(@Query("province_id") int provinceId);

    @GET("api/ward")
    Call<List<Ward>> getWards(@Query("district_id") int districtId);

}
