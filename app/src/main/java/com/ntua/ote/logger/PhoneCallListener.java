package com.ntua.ote.logger;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.ntua.ote.logger.models.LogDetails;
import com.ntua.ote.logger.utils.Direction;

import java.util.Date;

public class PhoneCallListener extends PhoneStateListener {

    public static final String TAG = PhoneCallListener.class.getSimpleName();
    private LogService service;

    public PhoneCallListener(LogService service) {
        this.service = service;
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {

        switch(state) {
            case TelephonyManager.CALL_STATE_IDLE:
                break;
            case TelephonyManager.CALL_STATE_RINGING:
                LogDetails logDetails = new LogDetails(incomingNumber, new Date(), Direction.INCOMING);
                service.storeAndSend(logDetails);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:

                break;
        }

    }
}