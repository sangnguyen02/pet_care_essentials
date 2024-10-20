package com.example.uiux.Model;

public class BranchStore {
    private String branch_Store_id;
    private  String branch_name;
    private  String province;
    private  String district;
    private String ward;
    private String address_details;
    private int status;

    public BranchStore() {
    }

    public BranchStore(String branch_Store_id, String branch_name, String province, String district, String ward, String address_details, int status) {
        this.branch_Store_id = branch_Store_id;
        this.branch_name = branch_name;
        this.province = province;
        this.district = district;
        this.ward = ward;
        this.address_details = address_details;
        this.status = status;
    }

    public String getBranch_Store_id() {
        return branch_Store_id;
    }

    public void setBranch_Store_id(String branch_Store_id) {
        this.branch_Store_id = branch_Store_id;
    }

    public String getBranch_name() {
        return branch_name;
    }

    public void setBranch_name(String branch_name) {
        this.branch_name = branch_name;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
