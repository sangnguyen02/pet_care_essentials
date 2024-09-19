package com.example.uiux.Utils;

public class EmailValidator {

    public static boolean isValidEmail(String email) {
        // Biểu thức chính quy để kiểm tra định dạng email
        String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";

        // Kiểm tra xem email có khớp với định dạng hay không
        return email.matches(regex);
    }

}
