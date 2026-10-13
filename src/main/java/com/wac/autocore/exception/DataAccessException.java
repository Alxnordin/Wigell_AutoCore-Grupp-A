package com.wac.autocore.exception;

//Alexander
//tekniskt fel vid lagring eller hämtning, t.ex. att databasen inte svarar.
//Det ursprungliga felet (cause) sparas för felsökning.
public class DataAccessException extends AutoCoreException {

    public DataAccessException(String messageKey, Throwable cause,Object... args){
        super(messageKey,cause,args);
    }
}
