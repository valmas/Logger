package com.ntua.ote.logger;

import android.os.Handler;


public class SmsObserver extends AbstractObserver {

    private SmsLogService service;

    public SmsObserver(Handler handler, SmsLogService service) {
        super(handler);
        this.service = service;
    }

    private void getInfoAndSend(){

    }
}
