package com.example.uiux.Model;

public class WalletHistory
{
    private String wallet_history_id;
    private  String wallet_id;
    private String message;
    private String status;
    private  String date;

    public WalletHistory(String wallet_history_id, String wallet_id, String message, String status, String date) {
        this.wallet_history_id = wallet_history_id;
        this.wallet_id = wallet_id;
        this.message = message;
        this.status = status;
        this.date = date;
    }

    public WalletHistory() {
    }

    public String getWallet_history_id() {
        return wallet_history_id;
    }

    public void setWallet_history_id(String wallet_history_id) {
        this.wallet_history_id = wallet_history_id;
    }

    public String getWallet_id() {
        return wallet_id;
    }

    public void setWallet_id(String wallet_id) {
        this.wallet_id = wallet_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
