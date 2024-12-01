package com.example.uiux.Model;

public class ChatMessage {
    private String message;
    private boolean isUser; // true nếu là tin nhắn từ người dùng, false nếu từ bot.

    public ChatMessage(String message, boolean isUser) {
        this.message = message;
        this.isUser = isUser;
    }

    public String getMessage() {
        return message;
    }

    public boolean isUser() {
        return isUser;
    }
}

