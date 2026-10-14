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

}
