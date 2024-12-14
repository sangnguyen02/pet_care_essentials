package com.example.uiux.Utils;

public class OrderServiceStatus {
    public static final int BOOK = 0;
    public static final int CANCEL = 1;

    public  static String getOrderServcieStatus(int status)
    {
        switch (status)
        {
            case BOOK:
                return "Book";
            case CANCEL:
                return  "The booking has been cancelled";
            default:
                return "Undefined";
        }
    }
}
