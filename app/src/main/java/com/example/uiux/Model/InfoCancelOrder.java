package com.example.uiux.Model;

import java.util.Date;

public class InfoCancelOrder {
    private String info_cancel_id;
    private String order_id;
    private int type;//Cancellation reasons available
    private String other_type;
    private Date date;

    public InfoCancelOrder() {
    }

    public InfoCancelOrder(String info_cancel_id, String order_id, int type, String other_type, Date date) {
        this.info_cancel_id = info_cancel_id;
        this.order_id = order_id;
        this.type = type;
        this.other_type = other_type;
        this.date = date;
    }

    public String getInfo_cancel_id() {
        return info_cancel_id;
    }

    public void setInfo_cancel_id(String info_cancel_id) {
        this.info_cancel_id = info_cancel_id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getOther_type() {
        return other_type;
    }

    public void setOther_type(String other_type) {
        this.other_type = other_type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
