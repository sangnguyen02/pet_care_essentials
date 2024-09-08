package com.example.uiux.Model;

public class Payment_Status {
    private String payment_status_id;
    private String order_id;
    private int status;
    private String info;//billing information
    private  int payment_method;

    public Payment_Status() {
    }

    public Payment_Status(String payment_status_id, String order_id, int status, String info, int payment_method) {
        this.payment_status_id = payment_status_id;
        this.order_id = order_id;
        this.status = status;
        this.info = info;
        this.payment_method = payment_method;
    }

    public String getPayment_status_id() {
        return payment_status_id;
    }

    public void setPayment_status_id(String payment_status_id) {
        this.payment_status_id = payment_status_id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(int payment_method) {
        this.payment_method = payment_method;
    }
}
