package com.example.uiux.Model;

public class PaymentMethod {
    private int id;
    private String title;
    private int iconResId;

    public PaymentMethod(int id, String title, int iconResId) {
        this.id = id;
        this.title = title;
        this.iconResId = iconResId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }
}
