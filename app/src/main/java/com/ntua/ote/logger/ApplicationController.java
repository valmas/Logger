package com.ntua.ote.logger;

import android.telephony.TelephonyManager;

import com.ntua.ote.logger.models.PhoneDetails;
import com.ntua.ote.logger.models.rs.InitialRequest;
import com.ntua.ote.logger.utils.CommonUtils;

import java.util.HashMap;
import java.util.Map;

public class ApplicationController {
    private static ApplicationController ourInstance = new ApplicationController();

    public static ApplicationController getInstance() {
        return ourInstance;
    }

    private Map<String, Long> unfinishedCalls;

    private PhoneDetails phoneDetails;

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

    public void updatePhoneDetails(TelephonyManager tm){
        phoneDetails = CommonUtils.getPhoneDetails(tm);
    }

    public PhoneDetails getPhoneDetails(){
        return phoneDetails;
    }
}
