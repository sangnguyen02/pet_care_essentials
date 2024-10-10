package com.example.uiux.Model;

import java.util.Date;

public class Supplies_Import {
    private String supplies_import_id;
    private String supplies_Id;
    private String supplies_name;
    private  int quantity;
    private int remaining_quantity;
    private  double import_price;
    private String import_date;

    public Supplies_Import() {
    }

    public Supplies_Import(String supplies_import_id, String supplies_Id, String supplies_name, int quantity, int remaining_quantity, double import_price, String import_date) {
        this.supplies_import_id = supplies_import_id;
        this.supplies_Id = supplies_Id;
        this.supplies_name = supplies_name;
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

    public String getSupplies_Id() {
        return supplies_Id;
    }

    public void setSupplies_Id(String supplies_Id) {
        this.supplies_Id = supplies_Id;
    }

    public String getSupplies_name() {
        return supplies_name;
    }

    public void setSupplies_name(String supplies_name) {
        this.supplies_name = supplies_name;
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

    public String getImport_date() {
        return import_date;
    }

    public void setImport_date(String import_date) {
        this.import_date = import_date;
    }
}
