package com.wac.autocore.logging;

import com.wac.autocore.exception.AutoCoreException;
import com.wac.autocore.exception.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

//Alexander
//Loggar fel på ett enhetligt sätt. Används av ErrorHandler (JavaFX) och av det
//fristående programmet, så att samma loggregler gäller i båda körlägena.
//Tekniska fel loggas som ERROR med hela stacktracen och får en felkod som även visas för användaren.
//Fel i indata eller regler loggas som WARN utan stacktrace och utan args
//(args kan innehålla personuppgifter, t.ex. e-postadress).

public class ExceptionLogger {

    private static final Logger LOG = LoggerFactory.getLogger(ExceptionLogger.class);

    private ExceptionLogger () {
    }
    //loggar felet, returnerar en felkod för tekniska fel, annars null
    public static String log (Exception exception) {
        if (exception instanceof DataAccessException) {
            String errorId = newErrorId();
            LOG.error("Databasfel [felkod {}] i {}: {}",
                    errorId, origin(exception), exception.getMessage(), exception);
            return errorId;
        }
        if (exception instanceof AutoCoreException) {
            AutoCoreException autoCoreException = (AutoCoreException) exception;
            LOG.warn("{} [{}] i {}", exception.getClass().getSimpleName(),
                    autoCoreException.getMessageKey(), origin(exception));
            return null;
        }
        String errorId = newErrorId();
        LOG.error("Oväntat tekniskt fel [felkod {}] i {}",
                errorId, origin(exception), exception);
        return errorId;
    }

    //kort felkod som visas för användaren och står i loggen så att felet kan spåras
    private static String newErrorId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    //var felet uppstod: första stället i servicelagret, annars första raden i stacktracen
    private static String origin(Throwable throwable) {
        StackTraceElement[] stackTrace = throwable.getStackTrace();
        for (StackTraceElement element : stackTrace) {
            if (element.getClassName().startsWith("com.wac.autocore.servive")) {
        }
    }
        return stackTrace.length > 0 ? simpleName(stackTrace[0]) : "okänd";
}

private static String simpleName(StackTraceElement element) {
    String className = element.getClassName();
    String simpleClassName = className.substring(className.lastIndexOf('.' + 1));
    return simpleClassName + "." + element.getMethodName();
    }
}
