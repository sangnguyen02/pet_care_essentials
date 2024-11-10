package com.example.uiux.Model;

import java.util.Date;
import java.util.List;

public class Supplies_Price {
    private String supplies_price_id;
    private  String supplies_id;
    private  String supply;
    private List<Supplies_Detail> suppliesDetailList;
    private String effective_date;

    public Supplies_Price() {
    }

    public Supplies_Price(String supplies_price_id, String supplies_id, String supply, List<Supplies_Detail> suppliesDetailList, String effective_date) {
        this.supplies_price_id = supplies_price_id;
        this.supplies_id = supplies_id;
        this.supply = supply;
        this.suppliesDetailList = suppliesDetailList;
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

    public String getSupply() {
        return supply;
    }

    public void setSupply(String supply) {
        this.supply = supply;
    }

    public List<Supplies_Detail> getSuppliesDetailList() {
        return suppliesDetailList;
    }

    public void setSuppliesDetailList(List<Supplies_Detail> suppliesDetailList) {
        this.suppliesDetailList = suppliesDetailList;
    }

    public String getEffective_date() {
        return effective_date;
    }

    public void setEffective_date(String effective_date) {
        this.effective_date = effective_date;
    }
}
