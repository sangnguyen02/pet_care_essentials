package com.example.uiux.Model;

public class Ward {
    private String wardId;
    private String wardName;

    public Ward(String wardId, String wardName) {
        this.wardId = wardId;
        this.wardName = wardName;
    }

    public String getWardId() {
        return wardId;
    }

    public String getWardName() {
        return wardName;
    }

    @Override
    public String toString() {
        // Hiển thị cả ID và tên của phường/xã trong Spinner
        return  wardName;
    }
}
