package com.example.uiux.Model;

public class District {
    private String districtId;
    private String districtName;

    public District(String districtId, String districtName) {
        this.districtId = districtId;
        this.districtName = districtName;
    }

    public String getDistrictId() {
        return districtId;
    }

    public String getDistrictName() {
        return districtName;
    }

    @Override
    public String toString() {
        // Hiển thị cả ID và tên của quận/huyện trong Spinner
        return  districtName;
    }
}
