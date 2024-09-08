package com.example.uiux.Model;

import java.util.Date;

public class Supplies_Import {
    private String supplies_import_id;
    private String supplies_id;
    private  int quantity;
    private int remaining_quantity;
    private  double import_price;
    private Date import_date;

    public Supplies_Import() {
    }

    public Supplies_Import(String supplies_import_id, String supplies_id, int quantity, int remaining_quantity, double import_price, Date import_date) {
        this.supplies_import_id = supplies_import_id;
        this.supplies_id = supplies_id;
        this.quantity = quantity;
        this.remaining_quantity = remaining_quantity;
        this.import_price = import_price;
        this.import_date = import_date;
    }

    public String getSupplies_import_id() {
        return supplies_import_id;
    }

    public void setSupplies_import_id(String supplies_import_id) {
        this.supplies_import_id = supplies_import_id;
    }

    public String getSupplies_id() {
        return supplies_id;
    }

    public void setSupplies_id(String supplies_id) {
        this.supplies_id = supplies_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getRemaining_quantity() {
        return remaining_quantity;
    }

    public void setRemaining_quantity(int remaining_quantity) {
        this.remaining_quantity = remaining_quantity;
    }

    public double getImport_price() {
        return import_price;
    }

    public void setImport_price(double import_price) {
        this.import_price = import_price;
    }

    public Date getImport_date() {
        return import_date;
    }

    public void setImport_date(Date import_date) {
        this.import_date = import_date;
    }
}
