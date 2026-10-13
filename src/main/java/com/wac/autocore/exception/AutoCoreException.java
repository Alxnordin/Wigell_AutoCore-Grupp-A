package com.wac.autocore.exception;


//Alexander
//basklass för alla fel i systemet.
//messageKey är en nyckel i språkfilerna, så att felet kan visas på svenska eller engelska.
//args fylls i meddelandet, t.ex. ett kundnummer: "Kund {0} finns inte".
public abstract class AutoCoreException extends RuntimeException {
    private final String messageKey;
    private final Object[] args;

    protected AutoCoreException(String messageKey, Object... args) {
        super(messageKey);
        this.messageKey = messageKey;
        this.args = args;
    }

    protected AutoCoreException(String messageKey, Throwable cause, Object... args) {
        super(messageKey, cause);
        this.messageKey = messageKey;
        this.args = args;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public Object[] getArgs() {
        return args;
    }
}
