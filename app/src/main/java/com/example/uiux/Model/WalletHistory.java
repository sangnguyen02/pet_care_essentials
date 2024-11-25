package com.example.uiux.Model;

public class WalletHistory
{
    private String wallet_history_id;
    private  String wallet_id;
    private String message;

    public WalletHistory() {
    }

    public WalletHistory(String wallet_history_id, String wallet_id, String message) {
        this.wallet_history_id = wallet_history_id;
        this.wallet_id = wallet_id;
        this.message = message;
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
}
