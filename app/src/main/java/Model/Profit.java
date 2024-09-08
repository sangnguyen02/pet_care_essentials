package Model;

import java.util.Date;

public class Profit {
    private String profit_id;
    private String order_item_id;
    private double cost_price;
    private double sell_price;
    private double profit;
    private Date date;

    public Profit() {
    }

    public Profit(String profit_id, String order_item_id, double cost_price, double sell_price, double profit, Date date) {
        this.profit_id = profit_id;
        this.order_item_id = order_item_id;
        this.cost_price = cost_price;
        this.sell_price = sell_price;
        this.profit = profit;
        this.date = date;
    }

    public String getProfit_id() {
        return profit_id;
    }

    public void setProfit_id(String profit_id) {
        this.profit_id = profit_id;
    }

    public String getOrder_item_id() {
        return order_item_id;
    }

    public void setOrder_item_id(String order_item_id) {
        this.order_item_id = order_item_id;
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

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
