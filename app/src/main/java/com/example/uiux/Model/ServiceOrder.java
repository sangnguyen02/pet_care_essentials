package com.example.uiux.Model;

import java.util.Date;

public class ServiceOrder {
    private String service_order_id;
    private String service_id;
    private  String account_id;
    private String service_name;
    private String type;
    private String order_date;
    private double total_price;
    private String name;
    private String phone_number;
    private String email;
    private String branch_id;
    private String branch_name;
    private String branch_address;
    private  String time;
    private  int status;

    public ServiceOrder() {
    }

    public ServiceOrder(String service_order_id, String service_id, String account_id, String service_name, String type, String order_date, double total_price, String name, String phone_number, String email, String branch_id, String branch_name, String branch_address, String time, int status) {
        this.service_order_id = service_order_id;
        this.service_id = service_id;
        this.account_id = account_id;
        this.service_name = service_name;
        this.type = type;
        this.order_date = order_date;
        this.total_price = total_price;
        this.name = name;
        this.phone_number = phone_number;
        this.email = email;
        this.branch_id = branch_id;
        this.branch_name = branch_name;
        this.branch_address = branch_address;
        this.time = time;
        this.status = status;
    }

    public String getAccount_id() {
        return account_id;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getService_name() {
        return service_name;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public String getService_order_id() {
        return service_order_id;
    }

    public void setService_order_id(String service_order_id) {
        this.service_order_id = service_order_id;
    }

    public String getService_id() {
        return service_id;
    }

    public void setService_id(String service_id) {
        this.service_id = service_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public double getTotal_price() {
        return total_price;
    }

    public void setTotal_price(double total_price) {
        this.total_price = total_price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getBranch_address() {
        return branch_address;
    }

    public void setBranch_address(String branch_address) {
        this.branch_address = branch_address;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
