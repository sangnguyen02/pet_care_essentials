package com.example.uiux.Model;

import java.util.List;

public class Supplies {
    private String supplies_id;
    private  String name;
    private  double sell_price;
    private  double cost_price;
    private  String size;
    private String description;
    private  int status;
    private  int quantity;
    private  String category;
    private  String type;
    private List<String> imageUrls;

    public Supplies() {
    }

    public Supplies(String supplies_id, String name, double sell_price, double cost_price, String size, String description, int status, int quantity, String category, String type, List<String> imageUrls) {
        this.supplies_id = supplies_id;
        this.name = name;
        this.sell_price = sell_price;
        this.cost_price = cost_price;
        this.size = size;
        this.description = description;
        this.status = status;
        this.quantity = quantity;
        this.category = category;
        this.type = type;
        this.imageUrls = imageUrls;
    }

    public String getSupplies_id() {
        return supplies_id;
    }

    public void setSupplies_id(String supplies_id) {
        this.supplies_id = supplies_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getSell_price() {
        return sell_price;
    }

    public void setSell_price(double sell_price) {
        this.sell_price = sell_price;
    }

    public double getCost_price() {
        return cost_price;
    }

    public void setCost_price(double cost_price) {
        this.cost_price = cost_price;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
}
