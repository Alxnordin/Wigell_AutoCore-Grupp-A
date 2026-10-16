package com.wac.autocore.view;

import com.wac.autocore.util.LanguageManager;
import javafx.scene.control.Alert;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;

//Alexander
//Visar bekräftelser för användaren på ett enhetligt sätt, motsvarigheten till ErrorHandler
//fast för när något har gått bra t.ex faktura skapad, betalning registrerad

public class InfoDialog {

    private static final Logger LOGGER = Logger.getLogger(InfoDialog.class.getName());

    private InfoDialog() {
    }

    public static void show(String messageKey, Object... args) {
        String message;
        try {
            message = MessageFormat.format(LanguageManager.getInstance().getString(messageKey), args);
        } catch (MissingResourceException e) {
            //saknad språknyckel ska inte krascha appen, visa nyckeln och logga så den kan läggas till
            LOGGER.log(Level.WARNING, "Missing language key: " + messageKey);
            message = messageKey;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}