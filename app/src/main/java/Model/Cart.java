package Model;

public class Cart {
    private  String cart_id;
    private  String account_id;

    public Cart() {
    }

    public Cart(String cart_id, String account_id) {
        this.cart_id = cart_id;
        this.account_id = account_id;
    }

    public String getCart_id() {
        return cart_id;
    }

    public void setCart_id(String cart_id) {
        this.cart_id = cart_id;
    }

    public String getAccount_id() {
        return account_id;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }
}
