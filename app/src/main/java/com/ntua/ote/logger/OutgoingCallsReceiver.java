package com.ntua.ote.logger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class OutgoingCallsReceiver extends BroadcastReceiver {

    public static final String TAG = OutgoingCallsReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent){

        String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        Log.d(TAG, "outgoing call to: " + phoneNumber);
    }
}
