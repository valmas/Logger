package com.ntua.ote.logger.utils;

public enum Direction {

    INCOMING(0),
    OUTGOING(1);

    public int code;

    Direction(int code) {
        this.code = code;
    }

    public static Direction parseCode(int code){
        switch(code){
            case 0 : return INCOMING;
            case 1 : return OUTGOING;
        }
        return null;
    }
}
