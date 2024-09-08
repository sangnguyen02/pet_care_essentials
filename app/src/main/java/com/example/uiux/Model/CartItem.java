package com.example.uiux.Model;

public class CartItem {
    private String cart_item_id;
    private int quantity;
    private  String cart_id;
    private  int supplies_id;

    public CartItem() {
    }

    public CartItem(String cart_item_id, int quantity, String cart_id, int supplies_id) {
        this.cart_item_id = cart_item_id;
        this.quantity = quantity;
        this.cart_id = cart_id;
        this.supplies_id = supplies_id;
    }

    public String getCart_item_id() {
        return cart_item_id;
    }

    public void setCart_item_id(String cart_item_id) {
        this.cart_item_id = cart_item_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getCart_id() {
        return cart_id;
    }

    public void setCart_id(String cart_id) {
        this.cart_id = cart_id;
    }

    public int getSupplies_id() {
        return supplies_id;
    }

    public void setSupplies_id(int supplies_id) {
        this.supplies_id = supplies_id;
    }
}
