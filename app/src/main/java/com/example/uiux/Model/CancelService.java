package com.example.uiux.Model;

import java.util.Date;

public class CancelService {
    private String cancel_service_id;
    private String service_order_id;
    private Date date;
    private String reason;
    private String detail_reason;
    private String name;
    private String phone_number;
    private String email;

    public CancelService() {
    }

    public CancelService(String cancel_service_id, String service_order_id, Date date, String reason, String detail_reason, String name, String phone_number, String email) {
        this.cancel_service_id = cancel_service_id;
        this.service_order_id = service_order_id;
        this.date = date;
        this.reason = reason;
        this.detail_reason = detail_reason;
        this.name = name;
        this.phone_number = phone_number;
        this.email = email;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
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
