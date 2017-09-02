package com.ntua.ote.logger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.ntua.ote.logger.models.LogDetails;
import com.ntua.ote.logger.utils.Direction;
import java.util.Date;

public class OutgoingCallsReceiver extends BroadcastReceiver {

    public static final String TAG = OutgoingCallsReceiver.class.getSimpleName();
    private LogService service;

    public OutgoingCallsReceiver(LogService service) {
        this.service = service;
    }

    @Override
    public void onReceive(Context context, Intent intent){
        String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        LogDetails logDetails = new LogDetails(phoneNumber, new Date(), Direction.OUTGOING);
        service.storeAndSend(logDetails);
        Log.i(TAG, "called " + phoneNumber);
    }
}
