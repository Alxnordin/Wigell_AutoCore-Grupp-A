package com.wac.autocore.exception;


//Alexander
//operationen är inte tillåten enligt verksamhetens regler,
//t.ex. en otillåten statusövergång eller en mekaniker som redan är bokad
public class BusinessRuleException extends AutoCoreException {

    public BusinessRuleException(String messageKey, Object... args){
        super(messageKey, args);
    }
}
