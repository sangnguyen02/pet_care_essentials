package com.example.uiux.Model;

public class AccountWallet {
    private String wallet_id;
    private String account_id;
    private String PIN;
    private double balance;

    public AccountWallet(String wallet_id, String account_id, String PIN, double balance) {
        this.wallet_id = wallet_id;
        this.account_id = account_id;
        this.PIN = PIN;
        this.balance = balance;
    }

    public String getPIN() {
        return PIN;
    }

    public void setPIN(String PIN) {
        this.PIN = PIN;
    }

    public AccountWallet() {
    }

    public String getWallet_id() {
        return wallet_id;
    }

    public void setWallet_id(String wallet_id) {
        this.wallet_id = wallet_id;
    }

    public String getAccount_id() {
        return account_id;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
