package Model;

import java.util.Date;

public class Discount {
    private String discount_id;
    private String supplies_id;
    private double discount_percent;
    private double discounted_price;
    private int status;
    private Date start_date;
    private Date end_date;

    public Discount() {
    }

    public Discount(String discount_id, String supplies_id, double discount_percent, double discounted_price, int status, Date start_date, Date end_date) {
        this.discount_id = discount_id;
        this.supplies_id = supplies_id;
        this.discount_percent = discount_percent;
        this.discounted_price = discounted_price;
        this.status = status;
        this.start_date = start_date;
        this.end_date = end_date;
    }

    public String getDiscount_id() {
        return discount_id;
    }

    public void setDiscount_id(String discount_id) {
        this.discount_id = discount_id;
    }

    public String getSupplies_id() {
        return supplies_id;
    }

    public void setSupplies_id(String supplies_id) {
        this.supplies_id = supplies_id;
    }

    public double getDiscount_percent() {
        return discount_percent;
    }

    public void setDiscount_percent(double discount_percent) {
        this.discount_percent = discount_percent;
    }

    public double getDiscounted_price() {
        return discounted_price;
    }

    public void setDiscounted_price(double discounted_price) {
        this.discounted_price = discounted_price;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getStart_date() {
        return start_date;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    public Date getEnd_date() {
        return end_date;
    }

    public void setEnd_date(Date end_date) {
        this.end_date = end_date;
    }
}
