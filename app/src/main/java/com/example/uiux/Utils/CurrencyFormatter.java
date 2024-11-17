package com.example.uiux.Utils;

import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyFormatter {
    public static String formatCurrency(double amount, String currencySymbol) {
        // Format the amount to 0 decimal places and add commas
        NumberFormat formatter = NumberFormat.getInstance(Locale.GERMANY);
        formatter.setMaximumFractionDigits(0);
        return String.format("%s %s", currencySymbol, formatter.format(amount));
    }
}
