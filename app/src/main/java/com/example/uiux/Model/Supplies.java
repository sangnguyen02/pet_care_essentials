package com.example.uiux.Model;

import java.util.List;
import java.util.Objects;

public class Supplies {
    private String supplies_id;
    private  String name;
    private String description;
    private  double sell_price;
    private  int status;
    private  int quantity;
    private  String category;
    private  String type;
    private List<String> imageUrls;

    public Supplies() {
    }

    public Supplies(String supplies_id, String name, double sell_price, String description, int status, int quantity, String category, String type, List<String> imageUrls) {
        this.supplies_id = supplies_id;
        this.name = name;
        this.sell_price = sell_price;
        this.description = description;
        this.status = status;
        this.quantity = quantity;
        this.category = category;
        this.type = type;
        this.imageUrls = imageUrls;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Supplies supplies = (Supplies) obj;
        return Objects.equals(supplies_id, supplies.supplies_id) &&
                Double.compare(supplies.sell_price, sell_price) == 0 &&
                quantity == supplies.quantity &&
                Objects.equals(name, supplies.name) &&
                Objects.equals(description, supplies.description) &&
                Objects.equals(category, supplies.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(supplies_id, name, sell_price, description, category, quantity);
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
