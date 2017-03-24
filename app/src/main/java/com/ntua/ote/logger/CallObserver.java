package com.ntua.ote.logger;

import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.CallLog;
import android.util.Log;

import com.ntua.ote.logger.db.CallLogDbHelper;
import com.ntua.ote.logger.models.rs.DurationRequest;
import com.ntua.ote.logger.utils.Direction;
import com.ntua.ote.logger.utils.LocationFinder;

import java.util.Date;

public class CallObserver extends AbstractObserver {

    private CallLogService service;
    private Date latestCall;

    public CallObserver(Handler handler, CallLogService service, Date latestCall) {
        super(handler);
        this.service = service;
        this.latestCall = latestCall;
    }

    protected void getInfoAndSend(){
        Uri callLogUri = CallLog.Calls.CONTENT_URI;
        try{
            Cursor managedCursor = service.getApplicationContext().getContentResolver().query(callLogUri, null, null, null, null);
            int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
            int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
            int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
            managedCursor.moveToLast();
            if (!managedCursor.isAfterLast()) {
                String callDate = managedCursor.getString(date);
                Date logsLateastCall = new Date(Long.valueOf(callDate));
                if (latestCall == null || logsLateastCall.after(latestCall)) {
                    latestCall = logsLateastCall;
                    String phNumber = managedCursor.getString(number);
                    int callDuration = managedCursor.getInt(duration);
                    Long id = ApplicationController.getInstance().getUnfinishedCallId(phNumber);
                    if(id != null) {
                        CallLogDbHelper.getInstance(service).update(callDuration, id);
                        LocationFinder.getInstance(service).removeIdFromPending(id);
                        OutboundController.getInstance(service).durationAdded(id, new DurationRequest(0, callDuration));
                    }
                }
            }
            managedCursor.close();
        } catch (SecurityException e) {
            Log.e("CallObserver", "<on change> READ CALL LOG permission not found");
        }
    }
}
