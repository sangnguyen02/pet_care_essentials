package com.example.uiux.Model;

import java.util.Date;

public class CancelService {
    private String cancel_service_id;
    private String service_order_id;
    private String date;
    private String reason;
    private String detail_reason;
    private String service_name;
    private String user_name;
    private String phone_number;
    private String email;
    private double total_price;
    private String branch_id;
    private String branch_name;
    private String branch_address;

    public CancelService() {
    }

    public CancelService(String cancel_service_id, String service_order_id, String date, String reason, String detail_reason, String service_name, String user_name, String phone_number, String email, double total_price, String branch_id, String branch_name, String branch_addres) {
        this.cancel_service_id = cancel_service_id;
        this.service_order_id = service_order_id;
        this.date = date;
        this.reason = reason;
        this.detail_reason = detail_reason;
        this.service_name = service_name;
        this.user_name = user_name;
        this.phone_number = phone_number;
        this.email = email;
        this.total_price = total_price;
        this.branch_id = branch_id;
        this.branch_name = branch_name;
        this.branch_address = branch_addres;
    }

    public String getCancel_service_id() {
        return cancel_service_id;
    }

    public void setCancel_service_id(String cancel_service_id) {
        this.cancel_service_id = cancel_service_id;
    }

    public String getService_order_id() {
        return service_order_id;
    }

    public void setService_order_id(String service_order_id) {
        this.service_order_id = service_order_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDetail_reason() {
        return detail_reason;
    }

    public void setDetail_reason(String detail_reason) {
        this.detail_reason = detail_reason;
    }

    public String getService_name() {
        return service_name;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getTotal_price() {
        return total_price;
    }

    public void setTotal_price(double total_price) {
        this.total_price = total_price;
    }

    public String getBranch_id() {
        return branch_id;
    }

    public void setBranch_id(String branch_id) {
        this.branch_id = branch_id;
    }

    public String getBranch_name() {
        return branch_name;
    }

    public void setBranch_name(String branch_name) {
        this.branch_name = branch_name;
    }

    public String getBranch_addres() {
        return branch_address;
    }

    public void setBranch_addres(String branch_addres) {
        this.branch_address = branch_addres;
    }
}
