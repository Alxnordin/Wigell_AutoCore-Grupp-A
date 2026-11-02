package com.wac.autocore.util;

import java.text.NumberFormat;
import java.util.Locale;

public class PriceFormatter {

    public static String formatPrice(double amount) {
        NumberFormat format = NumberFormat.getNumberInstance(
            Locale.forLanguageTag("sv-SE"));

        format.setMinimumFractionDigits(0);
        format.setMaximumFractionDigits(2);

        return format.format(amount) + " kr";
    }

}
