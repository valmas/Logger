package com.ntua.ote.logger;

import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.CallLog;
import android.util.Log;

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
        StringBuffer sb = new StringBuffer();
        Uri callLogUri = CallLog.Calls.CONTENT_URI;
        try{
            Cursor managedCursor = service.getApplicationContext().getContentResolver().query(callLogUri, null, null, null, null);
            int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
            int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
            int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
            int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
            sb.append("Call Details :");
            managedCursor.moveToLast();
            if (!managedCursor.isAfterLast()) {
                String callDate = managedCursor.getString(date);
                Date logsLateastCall = new Date(Long.valueOf(callDate));
                if (logsLateastCall.after(latestCall)) {
                    String callDayTime = logsLateastCall.toString();
                    String phNumber = managedCursor.getString(number);
                    String callType = managedCursor.getString(type);
                    String callDuration = managedCursor.getString(duration);
                    String dir = null;
                    int dircode = Integer.parseInt(callType);
                    switch (dircode) {
                        case CallLog.Calls.OUTGOING_TYPE:
                            dir = "OUTGOING";
                            break;

                        case CallLog.Calls.INCOMING_TYPE:
                            dir = "INCOMING";
                            break;

                        case CallLog.Calls.MISSED_TYPE:
                            dir = "MISSED";
                            break;
                    }
                    sb.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- " + dir + " \nCall Date:--- " + callDayTime + " \nCall duration in sec :--- " + callDuration);
                    sb.append("\n----------------------------------");
                    service.sendResult(sb.toString());
                }
            }
            managedCursor.close();
        } catch (SecurityException e) {
            Log.e("CallObserver", "<on change> READ CALL LOG permission not found");
        }
    }
}
