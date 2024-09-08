package com.example.uiux.Model;

import java.util.Date;

public class ServiceOrder {
    private String service_order_id;
    private String service_id;
    private String type_id;
    private Date order_date;
    private double total_price;
    private String name;
    private String phone_number;
    private String email;

    public ServiceOrder() {
    }

    public ServiceOrder(String service_order_id, String service_id, String type_id, Date order_date, double total_price, String name, String phone_number, String email) {
        this.service_order_id = service_order_id;
        this.service_id = service_id;
        this.type_id = type_id;
        this.order_date = order_date;
        this.total_price = total_price;
        this.name = name;
        this.phone_number = phone_number;
        this.email = email;
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

    public String getType_id() {
        return type_id;
    }

    public void setType_id(String type_id) {
        this.type_id = type_id;
    }

    public Date getOrder_date() {
        return order_date;
    }

    public void setOrder_date(Date order_date) {
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
}
