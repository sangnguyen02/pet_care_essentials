package com.example.uiux.Model;

public class Supplies_Detail {
    private String id;
    private String size;
    private int quantity;
    private double import_price;
    private double cost_price;


    public Supplies_Detail() {
    }

    public Supplies_Detail(String id, String size, int quantity, double import_price, double cost_price) {
        this.id = id;
        this.size = size;
        this.quantity = quantity;
        this.import_price = import_price;
        this.cost_price = cost_price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getImport_price() {
        return import_price;
    }

    public void setImport_price(double import_price) {
        this.import_price = import_price;
    }

    public double getCost_price() {
        return cost_price;
    }

    public void setCost_price(double cost_price) {
        this.cost_price = cost_price;
    }
}
