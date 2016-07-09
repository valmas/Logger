package com.ntua.ote.logger;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.ntua.ote.logger.db.CallLogDbHelper;
import com.ntua.ote.logger.models.LogDetails;
import com.ntua.ote.logger.utils.Direction;
import com.ntua.ote.logger.utils.LocationFinder;

import java.util.Date;

public class PhoneCallListener extends PhoneStateListener {

    public static final String TAG = PhoneCallListener.class.getSimpleName();
    private Context context;

    public PhoneCallListener(Context context) {
        this.context = context;
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {

        switch(state) {
            case TelephonyManager.CALL_STATE_IDLE:
                break;
            case TelephonyManager.CALL_STATE_RINGING:
                LogDetails logDetails = new LogDetails(incomingNumber, new Date(), Direction.INCOMING);
                long rowId = CallLogDbHelper.getInstance(context).insert(logDetails);
                if(rowId != -1) {
                    ApplicationController.getInstance().addUnfinishedCall(incomingNumber, rowId);
                    LocationFinder.getInstance(context).getLocation(rowId);
                }
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:

                break;
        }

    }
}