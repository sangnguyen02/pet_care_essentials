package com.example.uiux.Model;

public class ChatMessage {
    private String message;
    private boolean isUser; // true nếu là tin nhắn từ người dùng, false nếu từ bot.
    private String supplyId;
    private String imageUrl;


    public ChatMessage(String message, boolean isUser) {
        this.message = message;
        this.isUser = isUser;
    }
    public ChatMessage(String message, boolean isUser, String supplyId, String imageUrl) {
        this.message = message;
        this.isUser = isUser;
        this.supplyId = supplyId;
        this.imageUrl = imageUrl;
    }

    public String getMessage() {
        return message;
    }

    public boolean isUser() {
        return isUser;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSupplyId() {
        return supplyId;
    }

    public void setSupplyId(String supplyId) {
        this.supplyId = supplyId;
    }
}

