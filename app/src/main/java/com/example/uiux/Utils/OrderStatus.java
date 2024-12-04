package com.example.uiux.Utils;

public class OrderStatus {
    // Định nghĩa các trạng thái của đơn hàng
    public static final int PENDING = 0; // Đang chờ
    public static final int PREPARING = 1; // Đã thanh toán
    public static final int SHIPPING = 2; // Đã giao hàng
    public static final int DELIVERED = 3; // Đã nhận hàng
    public static final int CANCELED = 4; // Đã hủy
    public static final int RETURNED = 5; // Đã trả lại

    // Hàm trả về tên trạng thái theo giá trị int
    public static String getStatusName(int status) {
        switch (status) {
            case PENDING:
                return "Pending";
            case PREPARING:
                return "Preparing";
            case SHIPPING:
                return "Delivering";
            case DELIVERED:
                return "Delivered";
            case CANCELED:
                return "Canceled";
            case RETURNED:
                return "Returned";
            default:
                return "Undefined";
        }
    }
}
