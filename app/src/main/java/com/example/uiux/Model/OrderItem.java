package com.example.uiux.Model;

public class OrderItem {
    private String order_item_id;
    private  int quantity;
    private  double total_price;
    private String supplies_id;
    private String order_id;
    private  double cost_price;
    private  double sell_price;

    public OrderItem() {
    }

    public OrderItem(String order_item_id, int quantity, double total_price, String supplies_id, String order_id, double cost_price, double sell_price) {
        this.order_item_id = order_item_id;
        this.quantity = quantity;
        this.total_price = total_price;
        this.supplies_id = supplies_id;
        this.order_id = order_id;
        this.cost_price = cost_price;
        this.sell_price = sell_price;
    }

    public String getOrder_item_id() {
        return order_item_id;
    }

    public void setOrder_item_id(String order_item_id) {
        this.order_item_id = order_item_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotal_price() {
        return total_price;
    }

    public void setTotal_price(double total_price) {
        this.total_price = total_price;
    }

    public String getSupplies_id() {
        return supplies_id;
    }

    public void setSupplies_id(String supplies_id) {
        this.supplies_id = supplies_id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public double getCost_price() {
        return cost_price;
    }

    public void setCost_price(double cost_price) {
        this.cost_price = cost_price;
    }

    public double getSell_price() {
        return sell_price;
    }

    public void setSell_price(double sell_price) {
        this.sell_price = sell_price;
    }
}
