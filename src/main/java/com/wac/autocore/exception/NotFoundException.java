package com.wac.autocore.exception;

//Alexander
//en referens pekar på något som inte finns, t.ex. ett kund-ID eller fordons-ID
public class NotFoundException extends AutoCoreException {

    public NotFoundException(String messageKey, Object... args){
        super(messageKey, args);
    }
}
