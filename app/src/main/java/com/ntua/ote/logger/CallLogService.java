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
import com.ntua.ote.logger.db.CallLogDbHelper;

import java.util.Date;

public class CallLogService extends Service{

    private AbstractObserver cObs;
    private LocalBroadcastManager broadcaster;
    private OutgoingCallsReceiver outgoingCallsReceiver;
    private SmsListener smsListener;
    private PhoneCallListener phoneCallListener;
    private TelephonyManager tm;

    static final public String COPA_RESULT = "com.ntua.ote.logger.CallLogService.REQUEST_PROCESSED";

    static final public String COPA_MESSAGE = "com.ntua.ote.logger.CallLogService.COPA_MSG";

    private static final String TAG = CallLogService.class.getName();

    @Override
    public void onDestroy() {
        unregisterReceiver(outgoingCallsReceiver);
        unregisterReceiver(smsListener);
        getContentResolver().unregisterContentObserver(cObs);
        tm.listen(phoneCallListener, PhoneStateListener.LISTEN_NONE );
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
        tm = (TelephonyManager) getSystemService( Context.TELEPHONY_SERVICE );
        broadcaster = LocalBroadcastManager.getInstance(this);
        CallLogDbHelper.getInstance(this).setService(this);
        if(cObs == null) {
            cObs = new CallObserver(new Handler(), this, latestCall);
            getContentResolver().registerContentObserver(
                    CallLog.Calls.CONTENT_URI, true, cObs);
        }

        phoneCallListener = new PhoneCallListener(this);
        tm.listen(phoneCallListener, PhoneStateListener.LISTEN_CALL_STATE );

        IntentFilter filter = new IntentFilter("android.intent.action.NEW_OUTGOING_CALL");
        outgoingCallsReceiver = new OutgoingCallsReceiver(this);
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
    public void dbUpdated(int code) {
        Intent intent = new Intent(COPA_RESULT);
        intent.putExtra(COPA_MESSAGE, code);
        broadcaster.sendBroadcast(intent);
    }

}
