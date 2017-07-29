package com.ntua.ote.logger;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.ntua.ote.logger.models.PhoneDetails;
import com.ntua.ote.logger.utils.CommonUtils;

import java.util.HashMap;
import java.util.Map;

public class ApplicationController {

    private static ApplicationController ourInstance = new ApplicationController();

    public static ApplicationController getInstance() {
        return ourInstance;
    }

    public static final String VERSION = "1.3";

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

    public void updatePhoneDetails(Context context){
        TelephonyManager tm = (TelephonyManager) context.getSystemService( Context.TELEPHONY_SERVICE );
        phoneDetails = CommonUtils.getPhoneDetails(tm);
        if(TextUtils.isEmpty(phoneDetails.getMsisdn())) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            String msisdn = sharedPref.getString(SettingsActivity.KEY_PREF_MSISDN, "");
            phoneDetails.setMsisdn(msisdn);
        }
    }

    public PhoneDetails getPhoneDetails(){
        return phoneDetails;
    }

}
