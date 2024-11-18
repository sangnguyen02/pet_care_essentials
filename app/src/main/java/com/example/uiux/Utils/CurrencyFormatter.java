package com.example.uiux.Utils;

import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyFormatter {
    public static String formatCurrency(double amount, String currencySymbol) {
        // Sử dụng định dạng số phù hợp với Việt Nam
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        formatter.setMaximumFractionDigits(0);  // Không hiển thị phần thập phân
        return String.format("%s %s", currencySymbol, formatter.format(amount));
    }
}
