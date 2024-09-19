package com.example.uiux.Utils;

public class PhoneNumberValidator {

    public static boolean isValidPhoneNumber(String phoneNumber) {
        // Số điện thoại phải bắt đầu với số 0 và có độ dài chính xác là 10 chữ số
        String regex = "^0\\d{9}$";

        // Kiểm tra xem số điện thoại có khớp với định dạng hay không
        return phoneNumber.matches(regex);
    }


}
