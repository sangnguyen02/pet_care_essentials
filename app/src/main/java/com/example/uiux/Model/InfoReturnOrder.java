package com.example.uiux.Model;

import java.util.Date;

public class InfoReturnOrder {
    private String info_return_id;
    private String order_id;
    private String reason;
    private String detail_reason;
    private Date request_date;
    private Date return_date;
    private  String name;
    private  String address;
    private  String phone_number;
    private  String email;

    public InfoReturnOrder() {
    }

    public InfoReturnOrder(String info_return_id, String order_id, String reason, String detail_reason, Date request_date, Date return_date, String name, String address, String phone_number, String email) {
        this.info_return_id = info_return_id;
        this.order_id = order_id;
        this.reason = reason;
        this.detail_reason = detail_reason;
        this.request_date = request_date;
        this.return_date = return_date;
        this.name = name;
        this.address = address;
        this.phone_number = phone_number;
        this.email = email;
    }

    public String getInfo_return_id() {
        return info_return_id;
    }

    public void setInfo_return_id(String info_return_id) {
        this.info_return_id = info_return_id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
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

    public Date getRequest_date() {
        return request_date;
    }

    public void setRequest_date(Date request_date) {
        this.request_date = request_date;
    }

    public Date getReturn_date() {
        return return_date;
    }

    public void setReturn_date(Date return_date) {
        this.return_date = return_date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
