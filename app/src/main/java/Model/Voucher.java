package Model;

import java.util.Date;

public class Voucher {
    private  String voucher_id;
    private  String category_id;
    private  String code;//Code of voucher
    private  int remaining_quantity;
    private  int discount_percent;
    private  double max_discount_amount;
    private  double discount_amount;
    private  double minimum_order_value;
    private  int voucher_scope;
    private  int status;
    private Date start_date;
    private Date end_date;

    public Voucher() {
    }

    public Voucher(String voucher_id, String category_id, String code, int remaining_quantity, int discount_percent, double max_discount_amount, double discount_amount, double minimum_order_value, int voucher_scope, int status, Date start_date, Date end_date) {
        this.voucher_id = voucher_id;
        this.category_id = category_id;
        this.code = code;
        this.remaining_quantity = remaining_quantity;
        this.discount_percent = discount_percent;
        this.max_discount_amount = max_discount_amount;
        this.discount_amount = discount_amount;
        this.minimum_order_value = minimum_order_value;
        this.voucher_scope = voucher_scope;
        this.status = status;
        this.start_date = start_date;
        this.end_date = end_date;
    }

    public String getVoucher_id() {
        return voucher_id;
    }

    public void setVoucher_id(String voucher_id) {
        this.voucher_id = voucher_id;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getRemaining_quantity() {
        return remaining_quantity;
    }

    public void setRemaining_quantity(int remaining_quantity) {
        this.remaining_quantity = remaining_quantity;
    }

    public int getDiscount_percent() {
        return discount_percent;
    }

    public void setDiscount_percent(int discount_percent) {
        this.discount_percent = discount_percent;
    }

    public double getMax_discount_amount() {
        return max_discount_amount;
    }

    public void setMax_discount_amount(double max_discount_amount) {
        this.max_discount_amount = max_discount_amount;
    }

    public double getDiscount_amount() {
        return discount_amount;
    }

    public void setDiscount_amount(double discount_amount) {
        this.discount_amount = discount_amount;
    }

    public double getMinimum_order_value() {
        return minimum_order_value;
    }

    public void setMinimum_order_value(double minimum_order_value) {
        this.minimum_order_value = minimum_order_value;
    }

    public int getVoucher_scope() {
        return voucher_scope;
    }

    public void setVoucher_scope(int voucher_scope) {
        this.voucher_scope = voucher_scope;
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
