package com.example.uiux.Utils;

public class OrderStatus {
    // Định nghĩa các trạng thái của đơn hàng
    public static final int PENDING = 0; // Đang chờ
    public static final int PREPARING = 1; // Đã thanh toán
    public static final int SHIPPING = 2; // Đã giao hàng
    public static final int DELIVERED = 3; // Đã nhận hàng
    public static final int CANCELED = 4; // Đã hủy
    public static  final int RETURN_REQUEST_PENDING=6;
    public static  final  int RETURN_PRODUCT_WAITING=7;
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
            case RETURN_REQUEST_PENDING:
                return "Waiting for Reply";
            case RETURN_PRODUCT_WAITING:
                return  "Waiting for get products";
            default:
                return "Undefined";
        }
    }

    public static String getStatusMessage(int status) {
        switch (status) {
            case PENDING:
                return "Your order is currently pending and will be processed soon.";
            case PREPARING:
                return "Your order is being prepared. We will notify you once it is ready for delivery.";
            case SHIPPING:
                return "Your order is on its way! You can expect delivery soon.";
            case DELIVERED:
                return "Your order has been delivered. Thank you for shopping with us!";
            case CANCELED:
                return "Your order has been canceled. If this is a mistake, please contact our support.";
            case RETURNED:
                return "Your order has been returned. Please check your account for further details.";
            case RETURN_REQUEST_PENDING:
                return "Your return request is pending. We will get back to you shortly.";
            case RETURN_PRODUCT_WAITING:
                return "We are waiting for the product to be returned. Please send it at your earliest convenience.";
            default:
                return "Your order status has been updated.";
        }
    }
}
