package com.ntua.ote.logger.utils;

public enum RequestType {

    INITIAL("initial/"),LOCATION("location/"),DURATION("duration/"),
    CHECK_VERSION("checkVersion/"),UPDATE("update/");

    public String endpoint;

    RequestType(String endpoint){
        this.endpoint = endpoint;
    }
}
