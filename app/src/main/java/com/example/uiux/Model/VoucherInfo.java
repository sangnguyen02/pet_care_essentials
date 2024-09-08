package com.example.uiux.Model;

public class VoucherInfo {
    private String voucher_info_id;
    private String order_id;
    private String voucher_id;
    private  double total_amount;
    private double voucher_discount;
    private double discounted_amount;

    public VoucherInfo() {
    }

    public VoucherInfo(String voucher_info_id, String order_id, String voucher_id, double total_amount, double voucher_discount, double discounted_amount) {
        this.voucher_info_id = voucher_info_id;
        this.order_id = order_id;
        this.voucher_id = voucher_id;
        this.total_amount = total_amount;
        this.voucher_discount = voucher_discount;
        this.discounted_amount = discounted_amount;
    }

    public String getVoucher_info_id() {
        return voucher_info_id;
    }

    public void setVoucher_info_id(String voucher_info_id) {
        this.voucher_info_id = voucher_info_id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getVoucher_id() {
        return voucher_id;
    }

    public void setVoucher_id(String voucher_id) {
        this.voucher_id = voucher_id;
    }

    public double getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(double total_amount) {
        this.total_amount = total_amount;
    }

    public double getVoucher_discount() {
        return voucher_discount;
    }

    public void setVoucher_discount(double voucher_discount) {
        this.voucher_discount = voucher_discount;
    }

    public double getDiscounted_amount() {
        return discounted_amount;
    }

    public void setDiscounted_amount(double discounted_amount) {
        this.discounted_amount = discounted_amount;
    }
}
