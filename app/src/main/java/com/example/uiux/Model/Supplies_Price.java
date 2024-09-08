package com.example.uiux.Model;

import java.util.Date;

public class Supplies_Price {
    private String supplies_price_id;
    private  String supplies_id;
    private  double sell_price;
    private Date effective_date;

    public Supplies_Price() {
    }

    public Supplies_Price(String supplies_price_id, String supplies_id, double sell_price, Date effective_date) {
        this.supplies_price_id = supplies_price_id;
        this.supplies_id = supplies_id;
        this.sell_price = sell_price;
        this.effective_date = effective_date;
    }

    public String getSupplies_price_id() {
        return supplies_price_id;
    }

    public void setSupplies_price_id(String supplies_price_id) {
        this.supplies_price_id = supplies_price_id;
    }

    public String getSupplies_id() {
        return supplies_id;
    }

    public void setSupplies_id(String supplies_id) {
        this.supplies_id = supplies_id;
    }

    public double getSell_price() {
        return sell_price;
    }

    public void setSell_price(double sell_price) {
        this.sell_price = sell_price;
    }

    public Date getEffective_date() {
        return effective_date;
    }

    public void setEffective_date(Date effective_date) {
        this.effective_date = effective_date;
    }
}
