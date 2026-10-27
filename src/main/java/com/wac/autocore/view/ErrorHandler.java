package com.wac.autocore.view;
import com.wac.autocore.exception.AutoCoreException;
import com.wac.autocore.logging.ExceptionLogger;
import com.wac.autocore.util.LanguageManager;
import javafx.scene.control.Alert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

//Alexander
//Visar fel för användaren på ett enhetligt sätt i hela appen
//Fel i indata eller regler visas som varning med ett begripligt meddelande
//Tekniska fel visas som ett allmänt felmeddelande med en felkod. Detaljerna loggas via ExceptionLogger
public class ErrorHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ErrorHandler.class);
    private ErrorHandler() {
    }
    public static void show(Exception exception) {
        LanguageManager languageManager = LanguageManager.getInstance();
        //inloggningen sköts av ExceptionLogger, ErrorHandler ansvarar bara för att visa felet
        String errorId = ExceptionLogger.log(exception);

        //Nekad behörighet: inloggad men rollen saknar rätt till operationen
        if (exception instanceof AccessDeniedException){
            showAlert(Alert.AlertType.WARNING, languageManager.getString("accessDeniedError"));
            return;
        }

        //ej inloggad: skyddad operation anropad utan inloggning
        if (exception instanceof AccessDeniedException){
            showAlert(Alert.AlertType.WARNING, languageManager.getString("notAuthenticatedError"));
            return;
        }

        if (errorId != null) {
            //tekniskt fel: inga interna detaljer till användaren, bara felkoden som finns i loggen
            showAlert(Alert.AlertType.ERROR, MessageFormat.format(
                    languageManager.getString("technicalErrorWithReference"), errorId));
        } else if (exception instanceof AutoCoreException) {
            AutoCoreException autoCoreException = (AutoCoreException) exception;
            String message;
            try {
                message = MessageFormat.format(
                        languageManager.getString(autoCoreException.getMessageKey()),
                        autoCoreException.getArgs());
            } catch (MissingResourceException e) {
                //saknad språknyckel ska inte krascha appen: visa nyckeln och logga så att den kan läggas till
                LOG.warn("Saknad språknyckel: {}", autoCoreException.getMessageKey());
                message = autoCoreException.getMessageKey();
            }
            showAlert(Alert.AlertType.WARNING, message);
        }
    }
    private static void showAlert(Alert.AlertType type, String message) {
//        Alert alert = new Alert(type, message);
//        alert.setHeaderText(null);
//        alert.showAndWait();
        Alert alert = UIComponents.createWarningAlert(type, message);
        alert.showAndWait();

    }
}