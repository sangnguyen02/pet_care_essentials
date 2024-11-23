package com.example.uiux.Model;

import java.util.Date;
import java.util.List;

public class Order {
    private String order_id;
    private String date_order;
    private String expected_delivery_date;
    private  String delivery_date;
    private List<CartItem> cart_items_ordered;
    private double total_price;
    private  String name_customer;
    private  String phone_number;
    private  String email;
    private  String account_id;
    private  int is_completed;
    private String address;
    private  int status;

    public Order() {
    }

    public Order(String order_id, String date_order, String expected_delivery_date, String delivery_date, double total_price, String name_customer, String phone_number, String email, String account_id, int is_completed, String address, int status) {
        this.order_id = order_id;
        this.date_order = date_order;
        this.expected_delivery_date = expected_delivery_date;
        this.delivery_date = delivery_date;
        this.total_price = total_price;
        this.name_customer = name_customer;
        this.phone_number = phone_number;
        this.email = email;
        this.account_id = account_id;
        this.is_completed = is_completed;
        this.address = address;
        this.status = status;
    }
    @Override
    public String toString() {
        return "Order{" +
                "order_id='" + order_id + '\'' +
                ", date_order='" + date_order + '\'' +
                ", expected_delivery_date='" + expected_delivery_date + '\'' +
                ", delivery_date='" + delivery_date + '\'' +
                ", cart_items_ordered=" + cart_items_ordered +
                ", total_price=" + total_price +
                ", name_customer='" + name_customer + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", email='" + email + '\'' +
                ", account_id='" + account_id + '\'' +
                ", is_completed=" + is_completed +
                ", address='" + address + '\'' +
                ", status=" + status +
                '}';
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getDate_order() {
        return date_order;
    }

    public void setDate_order(String date_order) {
        this.date_order = date_order;
    }

    public String getExpected_delivery_date() {
        return expected_delivery_date;
    }

    public void setExpected_delivery_date(String expected_delivery_date) {
        this.expected_delivery_date = expected_delivery_date;
    }

    public String getDelivery_date() {
        return delivery_date;
    }

    public void setDelivery_date(String delivery_date) {
        this.delivery_date = delivery_date;
    }

    public List<CartItem> getCart_items_ordered() {
        return cart_items_ordered;
    }

    public void setCart_items_ordered(List<CartItem> cart_items_ordered) {
        this.cart_items_ordered = cart_items_ordered;
    }

    public double getTotal_price() {
        return total_price;
    }

    public void setTotal_price(double total_price) {
        this.total_price = total_price;
    }

    public String getName_customer() {
        return name_customer;
    }

    public void setName_customer(String name_customer) {
        this.name_customer = name_customer;
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

    public String getAccount_id() {
        return account_id;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }

    public int getIs_completed() {
        return is_completed;
    }

    public void setIs_completed(int is_completed) {
        this.is_completed = is_completed;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
