package com.example.uiux.Model;

public class Account_Address {
    private String account_address_id;
    private String account_id;
    private String phone;
    private  String province;
    private  String district;
    private String ward;
    private String address_details;



    public Account_Address(String account_address_id, String account_id, String phone, String province, String district, String ward, String address_details, boolean is_default) {
        this.account_address_id = account_address_id;
        this.account_id = account_id;
        this.phone = phone;
        this.province = province;
        this.district = district;
        this.ward = ward;
        this.address_details = address_details;
        this.is_default = is_default;
    }

    private  boolean is_default;

    public Account_Address() {
    }



    public String getAccount_address_id() {
        return account_address_id;
    }

    public void setAccount_address_id(String account_address_id) {
        this.account_address_id = account_address_id;
    }

    public String getAccount_id() {
        return account_id;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getAddress_details() {
        return address_details;
    }

    public void setAddress_details(String address_details) {
        this.address_details = address_details;
    }

    public boolean getIs_default() {
        return is_default;
    }

    public void setIs_default(boolean is_default) {
        this.is_default = is_default;
    }
}
