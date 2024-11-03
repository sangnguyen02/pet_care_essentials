package com.example.uiux.Model;

public class BranchStore {
    private String branch_Store_id;
    private  String branch_name;
    private  double longtitude;
    private  double latitude;

    private String address_details;
    private int status;

    public BranchStore() {
    }

    public BranchStore(String branch_Store_id, String branch_name, double longtitude, double latitude, String address_details, int status) {
        this.branch_Store_id = branch_Store_id;
        this.branch_name = branch_name;
        this.longtitude = longtitude;
        this.latitude = latitude;
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

    public double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
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
