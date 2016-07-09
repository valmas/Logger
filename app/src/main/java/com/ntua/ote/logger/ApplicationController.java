package com.ntua.ote.logger;

import java.util.HashMap;
import java.util.Map;

public class ApplicationController {
    private static ApplicationController ourInstance = new ApplicationController();

    public static ApplicationController getInstance() {
        return ourInstance;
    }

    private Map<String, Long> unfinishedCalls;

    private ApplicationController() {
        unfinishedCalls = new HashMap<>();
    }

    public synchronized Long getUnfinishedCallId(String phoneNumber){
        if(unfinishedCalls.containsKey(phoneNumber)) {
            return unfinishedCalls.remove(phoneNumber);
        }
        return -1L;
    }

    public synchronized void addUnfinishedCall(String phoneNumber, long id){
        unfinishedCalls.put(phoneNumber, id);
    }
}
