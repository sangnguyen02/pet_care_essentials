package com.example.uiux.Model;

public class CartItem {
    private String supply_id;
    private String supply_title;
    private String supply_size;
    private double supply_price;
    private int quantity;
    private double totalPrice;
    private String imageUrl;
    private String combinedKey;

    public CartItem() {
    }

    public CartItem(String supply_id, String supply_title, String supply_size, double supply_price, int quantity, double totalPrice, String imageUrl) {
        this.supply_id = supply_id;
        this.supply_title = supply_title;
        this.supply_size = supply_size;
        this.supply_price = supply_price;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.imageUrl = imageUrl;
        this.combinedKey = generateCombinedKey(supply_id, supply_size);
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    private String generateCombinedKey(String supply_id, String supply_size) {
        return supply_id + "_" + supply_size;
    }
    public String getCombinedKey() {
        return combinedKey;
    }

    public void setCombinedKey(String combinedKey) {
        this.combinedKey = combinedKey;
    }

    public String getSupply_id() {
        return supply_id;
    }

    public void setSupply_id(String supply_id) {
        this.supply_id = supply_id;
    }

    public String getSupply_title() {
        return supply_title;
    }

    public void setSupply_title(String supply_title) {
        this.supply_title = supply_title;
    }

    public String getSupply_size() {
        return supply_size;
    }

    public void setSupply_size(String supply_size) {
        this.supply_size = supply_size;
    }

    public double getSupply_price() {
        return supply_price;
    }

    public void setSupply_price(double supply_price) {
        this.supply_price = supply_price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
