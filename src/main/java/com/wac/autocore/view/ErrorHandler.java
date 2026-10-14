package com.wac.autocore.view;

import com.wac.autocore.exception.AutoCoreException;
import com.wac.autocore.exception.DataAccessException;
import com.wac.autocore.util.LanguageManager;
import javafx.scene.control.Alert;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;


//Alexander
//Visar fel för användaren på ett enhetligt sätt i hela appen
//Fel i indata eller regler visas som varning med ett begripligt meddelande
//Tekniska fel visas som ett allmänt felmeddelande, och detaljerna loggas för felsökning

public class ErrorHandler {

    private static final Logger LOGGER = Logger.getLogger(ErrorHandler.class.getName());

    private ErrorHandler() {
    }

    public static void show(Exception exception) {
        LanguageManager languageManager = LanguageManager.getInstance();

        if (exception instanceof DataAccessException) {
            LOGGER.log(Level.SEVERE, "Database error", exception);
            showAlert(Alert.AlertType.ERROR, languageManager.getString("technicalErrorMessage"));
        } else if (exception instanceof AutoCoreException) {
            AutoCoreException autoCoreException = (AutoCoreException) exception;
            String message;
            try {
                message = MessageFormat.format(
                        languageManager.getString(autoCoreException.getMessageKey()),
                        autoCoreException.getArgs());
            } catch (MissingResourceException e) {
                //saknad språknyckel ska inte krascha appen: visa nyckeln och logga så att den kan läggas till
                LOGGER.log(Level.WARNING, "Missing language key: " + autoCoreException.getMessageKey());
                message = autoCoreException.getMessageKey();
            }
            showAlert(Alert.AlertType.WARNING, message);

        } else {
            LOGGER.log(Level.SEVERE, "Unexpected error", exception);
            showAlert(Alert.AlertType.ERROR, languageManager.getString("technicalErrorMessage"));
        }
    }

    private static void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}