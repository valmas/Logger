package com.ntua.ote.logger.utils;

public enum LogType {

    CALL(0),
    SMS(1);

    public int code;

    LogType (int code) {
        this.code = code;
    }

    public static LogType parseCode(int code){
        switch(code){
            case 0 : return CALL;
            case 1 : return SMS;
        }
        return null;
    }
}
