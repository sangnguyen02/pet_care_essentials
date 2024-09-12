package com.example.uiux.Utils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;
    private static final String BASE_URL = "https://provinces.open-api.vn/api/";  // Đổi thành API của bạn

    // Phương thức để khởi tạo Retrofit
    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())  // Sử dụng Gson để convert JSON
                    .build();
        }
        return retrofit;
    }
}
