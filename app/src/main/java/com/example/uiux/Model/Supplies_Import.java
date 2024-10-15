package com.example.uiux.Model;

import java.util.List;

public class Supplies_Import {
    private String supplies_import_id;
    private String supplies_name;
//    private  int quantity;
//    private int remaining_quantity;
//    private String import_price;
    private String import_date;
    private List<Supplies_Detail> sizes;

    public Supplies_Import() {
    }

    public Supplies_Import(String supplies_import_id, String supplies_name, String import_date, List<Supplies_Detail> sizes) {
        this.supplies_import_id = supplies_import_id;
        this.supplies_name = supplies_name;
        this.import_date = import_date;
        this.sizes = sizes;
    }

    public String getSupplies_import_id() {
        return supplies_import_id;
    }

    public void setSupplies_import_id(String supplies_import_id) {
        this.supplies_import_id = supplies_import_id;
    }

    public String getSupplies_name() {
        return supplies_name;
    }

    public void setSupplies_name(String supplies_name) {
        this.supplies_name = supplies_name;
    }

    public String getImport_date() {
        return import_date;
    }

    public void setImport_date(String import_date) {
        this.import_date = import_date;
    }
    public List<Supplies_Detail> getSuppliesDetail() {
        return sizes;
    }

    public void setSuppliesDetail(List<Supplies_Detail> sizes) {
        this.sizes = sizes;
    }
}
