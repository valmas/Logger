package com.ntua.ote.logger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.ntua.ote.logger.models.LogDetails;
import com.ntua.ote.logger.utils.Direction;

import java.util.Date;

public class IncomingSmsListener extends BroadcastReceiver {

    public static final String TAG = IncomingSmsListener.class.getSimpleName();
    private CallLogService service;

    public IncomingSmsListener(CallLogService service) {
        this.service = service;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();

        try {
            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String target = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody();
                    LogDetails logDetails = new LogDetails(target, new Date(), null, message);
                    if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
                        logDetails.setDirection(Direction.INCOMING);
                    }

                    service.storeAndSend(logDetails);
                }
            }
        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" +e);

        }
    }
}
