package com.wac.autocore.view;

import com.wac.autocore.util.LanguageManager;
import javafx.scene.control.Alert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.MissingResourceException;

//Alexander
//Visar bekräftelser för användaren på ett enhetligt sätt, motsvarigheten till ErrorHandler
//fast för när något har gått bra t.ex faktura skapad, betalning registrerad

public class InfoDialog {

    private static final Logger LOG = LoggerFactory.getLogger(InfoDialog.class);

    private InfoDialog() {
    }

    public static void show(String messageKey, Object... args) {
        String message;
        try {
            message = MessageFormat.format(LanguageManager.getInstance().getString(messageKey), args);
        } catch (MissingResourceException e) {
            //saknad språknyckel ska inte krascha appen, visa nyckeln och logga så den kan läggas till
            LOG.warn("Saknad språknyckel: {}", messageKey);
            message = messageKey;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}