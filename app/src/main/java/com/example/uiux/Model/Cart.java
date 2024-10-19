package com.example.uiux.Model;

import java.util.List;

public class Cart {
    private String account_id;
    private List<CartItem> cartItemList;

    public Cart() {
    }

    public Cart(String account_id, List<CartItem> cartItemList) {
        this.account_id = account_id;
        this.cartItemList = cartItemList;
    }

    public String getAccount_id() {
        return account_id;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }

    public List<CartItem> getCartItemList() {
        return cartItemList;
    }

    public void setCartItemList(List<CartItem> cartItemList) {
        this.cartItemList = cartItemList;
    }
}
