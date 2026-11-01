package com.wac.autocore.util;

import java.text.NumberFormat;
import java.util.Locale;

public class PriceFormatter {

    public static String formatPrice(double amount) {
        Locale locale = LanguageManager.getInstance().localeProperty().get();

        NumberFormat format = NumberFormat.getNumberInstance(locale);
        format.setMinimumFractionDigits(0);
        format.setMaximumFractionDigits(2);

        return format.format(amount) + " kr";
    }

}
