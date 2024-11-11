package com.example.uiux.Model;

import java.util.Date;

public class Voucher {
    private  String voucher_id;
    private  String category;
    private  String code;
    private  int remaining_quantity;
    private  int discount_percent;
    private  double max_discount_amount;
    private  int discount_amount;
    private  double minimum_order_value;
    private  int status;
    private String start_date;
    private String end_date;

    public Voucher() {
    }



    public String getVoucher_id() {
        return voucher_id;
    }

    public void setVoucher_id(String voucher_id) {
        this.voucher_id = voucher_id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getRemaining_quantity() {
        return remaining_quantity;
    }

    public void setRemaining_quantity(int remaining_quantity) {
        this.remaining_quantity = remaining_quantity;
    }

    public int getDiscount_percent() {
        return discount_percent;
    }

    public void setDiscount_percent(int discount_percent) {
        this.discount_percent = discount_percent;
    }

    public double getMax_discount_amount() {
        return max_discount_amount;
    }

    public void setMax_discount_amount(double max_discount_amount) {
        this.max_discount_amount = max_discount_amount;
    }

    public double getDiscount_amount() {
        return discount_amount;
    }

    public void setDiscount_amount(int discount_amount) {
        this.discount_amount = discount_amount;
    }

    public double getMinimum_order_value() {
        return minimum_order_value;
    }

    public void setMinimum_order_value(double minimum_order_value) {
        this.minimum_order_value = minimum_order_value;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
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
