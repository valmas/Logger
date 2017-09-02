package com.ntua.ote.logger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;

import com.ntua.ote.logger.models.LogDetails;
import com.ntua.ote.logger.utils.Direction;

import java.util.Date;

public class IncomingSmsListener extends BroadcastReceiver {

    public static final String TAG = IncomingSmsListener.class.getSimpleName();
    private LogService service;

    public IncomingSmsListener(LogService service) {
        this.service = service;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();

        try {
            if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
                if (bundle != null) {
                    String target = null, message="";
                    final Object[] pdusObj = (Object[]) bundle.get("pdus");

                    if (pdusObj != null) {
                        for (Object aPdusObj : pdusObj) {
                            SmsMessage currentSMS;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                String format = bundle.getString("format");
                                currentSMS = SmsMessage.createFromPdu((byte[]) aPdusObj, format);
                            } else {
                                currentSMS = SmsMessage.createFromPdu((byte[]) aPdusObj);
                            }
                            target = currentSMS.getDisplayOriginatingAddress();
                            message += currentSMS.getMessageBody();
                        }
                    }
                    LogDetails logDetails = new LogDetails(target, new Date(), Direction.INCOMING, message);
                    if(!TextUtils.isEmpty(logDetails.getExternalNumber())) {
                        service.storeAndSend(logDetails);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" +e);
        }
    }
}
