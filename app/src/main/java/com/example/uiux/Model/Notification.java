package com.example.uiux.Model;

import java.util.Date;

public class Notification {
    private String notification_id;
    private String account_id;
    private boolean sent_to_all;
    private  String content;
    private  int status;
    private int type;
    private String sent_time;

    public Notification() {
    }

    public Notification(String notification_id, String account_id, boolean sent_to_all, String content, int status, int type, String sent_time) {
        this.notification_id = notification_id;
        this.account_id = account_id;
        this.sent_to_all = sent_to_all;
        this.content = content;
        this.status = status;
        this.type = type;
        this.sent_time = sent_time;
    }

    public String getNotification_id() {
        return notification_id;
    }

    public void setNotification_id(String notification_id) {
        this.notification_id = notification_id;
    }

    public String getAccount_id() {
        return account_id;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }

    public boolean isSent_to_all() {
        return sent_to_all;
    }

    public void setSent_to_all(boolean sent_to_all) {
        this.sent_to_all = sent_to_all;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSent_time() {
        return sent_time;
    }

    public void setSent_time(String sent_time) {
        this.sent_time = sent_time;
    }
}
