package com.wac.autocore.validation;

import com.wac.autocore.exception.ValidationException;

//Alexander
//gemensamma valideringsregler som alla servicar använder.
//reglerna finns på ett ställe så samma kontroll ger samma fel oavsett var den görs

public class Validator {

    private Validator() {

    }

    //obligatorisk text får inte vara null, tom eller bara ett mellanslag
    public static void requireText(String value, String messageKey) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(messageKey);
        }
    }

    //Kan användas för alla fält där input är siffror
    public static int requireInteger(String value, String messageKey) {
        requireText(value, messageKey);
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new ValidationException(messageKey);
        }
    }

    //Alexander
    //talet måste ligga inom ett tillåtet intervall, t.ex. en årsmodell.
    //min och max skickas med så att felmeddelandet kan visa gränserna.
    public static void requireRange(int value, int min, int max, String messageKey) {
        if (value < min || value > max) {
            throw new ValidationException(messageKey, String.valueOf(min), String.valueOf(max));
        }
    }

    //Alexander
    //decimaltal, t.ex. ett pris: får inte vara tomt och måste gå att tolka som tal.
    //både 1200.50 och 1200,50 godkänns, eftersom svenska användare skriver komma.
    public static double requireDecimal(String value, String messageKey) {
        requireText(value, messageKey);
        try {
            return Double.parseDouble(value.trim().replace(',','.'));
        } catch (NumberFormatException e) {
            throw new ValidationException(messageKey);
        }
    }

    //Alexander
    //talet måste vara större än 0, t.ex pris eller tid
    public static void requirePositive(double value, String messageKey) {
        if (value <= 0) {
            throw new ValidationException(messageKey);
        }
    }
}
