package com.ntua.ote.logger.utils;

public enum RequestType {

    INITIAL("initial/"),LOCATION("location/"),DURATION("duration/");

    public String endpoint;

    RequestType(String endpoint){
        this.endpoint = endpoint;
    }
}
