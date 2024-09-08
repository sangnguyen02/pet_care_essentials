package Model;

import java.util.Date;

public class Revenue {
    private String revenue_id;
    private String order_id;
    private double revenue;
    private Date date;

    public Revenue() {
    }

    public Revenue(String revenue_id, String order_id, double revenue, Date date) {
        this.revenue_id = revenue_id;
        this.order_id = order_id;
        this.revenue = revenue;
        this.date = date;
    }

    public String getRevenue_id() {
        return revenue_id;
    }

    public void setRevenue_id(String revenue_id) {
        this.revenue_id = revenue_id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public double getRevenue() {
        return revenue;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
