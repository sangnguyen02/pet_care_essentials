package com.example.uiux.Model;

import java.util.Date;

public class Discount {
    private String discount_id;
    private String category;
    private double discount_percent;
    private String status;
    private String start_date;
    private String end_date;

    public Discount() {
    }

    public Discount(String discount_id, String category, double discount_percent, String status, String start_date, String end_date) {
        this.discount_id = discount_id;
        this.category = category;
        this.discount_percent = discount_percent;
        this.status = status;
        this.start_date = start_date;
        this.end_date = end_date;
    }

    public String getDiscount_id() {
        return discount_id;
    }

    public void setDiscount_id(String discount_id) {
        this.discount_id = discount_id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getDiscount_percent() {
        return discount_percent;
    }

    public void setDiscount_percent(double discount_percent) {
        this.discount_percent = discount_percent;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }
}
