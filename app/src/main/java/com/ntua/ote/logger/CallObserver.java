package com.ntua.ote.logger;

import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.CallLog;
import android.util.Log;

import com.ntua.ote.logger.db.CallLogDbHelper;
import com.ntua.ote.logger.models.rs.DurationRequest;
import com.ntua.ote.logger.utils.LocationFinder;

import java.util.Date;

public class CallObserver extends AbstractObserver {

    private static final String TAG = CallObserver.class.getName();

    private LogService service;
    private Date latestCall;

    public CallObserver(Handler handler, LogService service, Date latestCall) {
        super(handler);
        this.service = service;
        this.latestCall = latestCall;
    }

    protected void getInfoAndSend(){
        Uri callLogUri = CallLog.Calls.CONTENT_URI;
        try{
            Cursor managedCursor = service.getApplicationContext().getContentResolver().query(callLogUri, null, null, null, null);
            if(managedCursor != null) {
                int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
                int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
                int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
                managedCursor.moveToLast();
                if (!managedCursor.isAfterLast()) {
                    String callDate = managedCursor.getString(date);
                    Date logsLatestCall = new Date(Long.valueOf(callDate));
                    Log.i(TAG, "Latest Call: " + latestCall + " Logs latest call: " + logsLatestCall);
                    if (latestCall == null || logsLatestCall.after(latestCall)) {
                        latestCall = logsLatestCall;
                        String phNumber = managedCursor.getString(number);
                        int callDuration = managedCursor.getInt(duration);
                        Long id = ApplicationController.getInstance().getUnfinishedCallId(phNumber);
                        Log.i(TAG, "ID: " + id + " callDuration " + callDuration + " number: " + phNumber);
                        if (id != null) {
                            CallLogDbHelper.getInstance(service).update(callDuration, id);
                            LocationFinder.getInstance(service).removeIdFromPending(id);
                            OutboundController.getInstance(service).durationAdded(id, new DurationRequest(0, callDuration));
                        }
                    }
                }
                managedCursor.close();
            }
        } catch (SecurityException e) {
            Log.e(TAG, "<on change> READ CALL LOG permission not found");
        }
    }
}
