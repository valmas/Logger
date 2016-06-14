package com.ntua.ote.logger;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by valmas on 10/6/2016.
 */
public class SmsLogService extends Service {

    private AbstractObserver cObs;
    private LocalBroadcastManager broadcaster;

    static final public String COPA_RESULT = "com.ntua.ote.logger.SmsLogService.REQUEST_PROCESSED";

    static final public String COPA_MESSAGE = "com.ntua.ote.logger.SmsLogService.COPA_MSG";

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(cObs);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        broadcaster = LocalBroadcastManager.getInstance(this);
        if(cObs == null) {
            cObs = new SmsObserver(new Handler(), this);
            getContentResolver().registerContentObserver(
                    Telephony.Sms.CONTENT_URI,
                    true,
                    cObs);
        }
    }

    public void sendResult(String message) {
        Intent intent = new Intent(COPA_RESULT);
        if(message != null)
            intent.putExtra(COPA_MESSAGE, message);
        broadcaster.sendBroadcast(intent);
    }
}
