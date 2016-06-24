package com.ntua.ote.logger;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PhoneCallListener extends PhoneStateListener {

    public static final String TAG = PhoneCallListener.class.getSimpleName();

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {

        switch(state) {
            case TelephonyManager.CALL_STATE_IDLE:
                break;
            case TelephonyManager.CALL_STATE_RINGING:
                Log.d(TAG, "incoming call from: " + incomingNumber);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                break;
        }

    }
}