package Model;

import java.util.Date;

public class Supplies_Review {
    private String supplies_review_id;
    private String account_id;
    private String supplies_id;
    private int rating;
    private String comment;
    private Date date_posted;
    private  boolean is_purchase;
    private boolean is_published;

    public Supplies_Review() {
    }

    public Supplies_Review(String supplies_review_id, String account_id, String supplies_id, int rating, String comment, Date date_posted, boolean is_purchase, boolean is_published) {
        this.supplies_review_id = supplies_review_id;
        this.account_id = account_id;
        this.supplies_id = supplies_id;
        this.rating = rating;
        this.comment = comment;
        this.date_posted = date_posted;
        this.is_purchase = is_purchase;
        this.is_published = is_published;
    }

    public String getSupplies_review_id() {
        return supplies_review_id;
    }

    public void setSupplies_review_id(String supplies_review_id) {
        this.supplies_review_id = supplies_review_id;
    }

    public String getAccount_id() {
        return account_id;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }

    public String getSupplies_id() {
        return supplies_id;
    }

    public void setSupplies_id(String supplies_id) {
        this.supplies_id = supplies_id;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getDate_posted() {
        return date_posted;
    }

    public void setDate_posted(Date date_posted) {
        this.date_posted = date_posted;
    }

    public boolean isIs_purchase() {
        return is_purchase;
    }

    public void setIs_purchase(boolean is_purchase) {
        this.is_purchase = is_purchase;
    }

    public boolean isIs_published() {
        return is_published;
    }

    public void setIs_published(boolean is_published) {
        this.is_published = is_published;
    }
}
