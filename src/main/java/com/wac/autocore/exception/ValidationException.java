package com.wac.autocore.exception;


//Alexander
//felaktiga eller saknade uppgifter, t.ex. tomt namn, ogiltigt datum eller negativt pris.
public class ValidationException extends  AutoCoreException{

    public ValidationException(String messageKey, Object... args){
        super(messageKey, args);
    }

}
