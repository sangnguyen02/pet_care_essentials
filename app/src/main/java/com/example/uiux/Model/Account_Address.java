package com.example.uiux.Model;

public class Account_Address {
    private String account_address_id;
    private String recipient_name;
    private String phone;
    private  int city_id;
    private  int district_id;
    private int ward_id;
    private String address_details;
    private  int is_default;

    public Account_Address() {
    }

    public Account_Address(String account_address_id, String recipient_name, String phone, int city_id, int district_id, int ward_id, String address_details, int is_default) {
        this.account_address_id = account_address_id;
        this.recipient_name = recipient_name;
        this.phone = phone;
        this.city_id = city_id;
        this.district_id = district_id;
        this.ward_id = ward_id;
        this.address_details = address_details;
        this.is_default = is_default;
    }

    public String getAccount_address_id() {
        return account_address_id;
    }

    public void setAccount_address_id(String account_address_id) {
        this.account_address_id = account_address_id;
    }

    public String getRecipient_name() {
        return recipient_name;
    }

    public void setRecipient_name(String recipient_name) {
        this.recipient_name = recipient_name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getCity_id() {
        return city_id;
    }

    public void setCity_id(int city_id) {
        this.city_id = city_id;
    }

    public int getDistrict_id() {
        return district_id;
    }

    public void setDistrict_id(int district_id) {
        this.district_id = district_id;
    }

    public int getWard_id() {
        return ward_id;
    }

    public void setWard_id(int ward_id) {
        this.ward_id = ward_id;
    }

    public String getAddress_details() {
        return address_details;
    }

    public void setAddress_details(String address_details) {
        this.address_details = address_details;
    }

    public int getIs_default() {
        return is_default;
    }

    public void setIs_default(int is_default) {
        this.is_default = is_default;
    }
}
