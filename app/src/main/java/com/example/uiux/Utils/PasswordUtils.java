package com.example.uiux.Utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {
    public static String hashPassword(String password) {
        // Thêm salt vào mật khẩu và băm
        return BCrypt.hashpw(password, BCrypt.gensalt(12));  // 12 là độ mạnh của salt
    }

    // Phương thức để kiểm tra mật khẩu
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        // Kiểm tra mật khẩu băm với mật khẩu người dùng nhập vào
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
