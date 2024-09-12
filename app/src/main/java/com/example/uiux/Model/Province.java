package com.example.uiux.Model;

public class Province {
    private String provinceId;
    private String provinceName;
    private String provinceType;

    public Province(String provinceId, String provinceName, String provinceType) {
        this.provinceId = provinceId;
        this.provinceName = provinceName;
        this.provinceType = provinceType;
    }

    public String getProvinceId() {
        return provinceId;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public String getProvinceType() {
        return provinceType;
    }

    @Override
    public String toString() {
        // Hiển thị cả ID và tên của tỉnh trong Spinner
        //return provinceId + " - " + provinceName;
        return  provinceName;
    }
}
