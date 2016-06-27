package com.ntua.ote.logger;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.Date;

public class CallLogService extends Service {

    private AbstractObserver cObs;
    private LocalBroadcastManager broadcaster;
    private OutgoingCallsReceiver outgoingCallsReceiver;
    private SmsListener smsListener;

    static final public String COPA_RESULT = "com.ntua.ote.logger.CallLogService.REQUEST_PROCESSED";

    static final public String COPA_MESSAGE = "com.ntua.ote.logger.CallLogService.COPA_MSG";

    @Override
    public void onDestroy() {
        unregisterReceiver(outgoingCallsReceiver);
        unregisterReceiver(smsListener);
        getContentResolver().unregisterContentObserver(cObs);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Date latestCall = getLogsLatestCall();
        broadcaster = LocalBroadcastManager.getInstance(this);
        if(cObs == null) {
            cObs = new CallObserver(new Handler(), this, latestCall);
            getContentResolver().registerContentObserver(
                    CallLog.Calls.CONTENT_URI, true, cObs);
        }

        PhoneCallListener phoneCallListener = new PhoneCallListener();
        TelephonyManager tm = (TelephonyManager) getSystemService( Context.TELEPHONY_SERVICE );
        tm.listen(phoneCallListener, PhoneStateListener.LISTEN_CALL_STATE );

        IntentFilter filter = new IntentFilter("android.intent.action.NEW_OUTGOING_CALL");
        outgoingCallsReceiver = new OutgoingCallsReceiver();
        registerReceiver(outgoingCallsReceiver,filter);

        filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        filter.addAction("android.provider.Telephony.SMS_SENT");
        smsListener = new SmsListener();
        registerReceiver(smsListener,filter);
    }

    public void sendResult(String message) {
        Intent intent = new Intent(COPA_RESULT);
        if(message != null)
            intent.putExtra(COPA_MESSAGE, message);
        broadcaster.sendBroadcast(intent);
    }

    public Date getLogsLatestCall(){
        Uri callLogUri = CallLog.Calls.CONTENT_URI;
        Date callDayTime = null;
        try{
            Cursor managedCursor = this.getApplicationContext().getContentResolver().query(callLogUri, null, null, null, null);
            int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
            managedCursor.moveToLast();
            if (!managedCursor.isAfterLast()) {
                String callDate = managedCursor.getString(date);
                callDayTime = new Date(Long.valueOf(callDate));
            }
            managedCursor.close();
        } catch (SecurityException e) {
            Log.e("CallLogService", "<init> READ CALL LOG permission not found");
        }
        return callDayTime;
    }
}
