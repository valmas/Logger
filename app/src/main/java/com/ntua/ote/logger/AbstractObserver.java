package com.ntua.ote.logger;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

public class AbstractObserver extends ContentObserver {

    public AbstractObserver(Handler handler) {
        super(handler);
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
    }

    @Override
    public boolean deliverSelfNotifications() {
        return true;
    }


    @Override
    public void onChange(boolean selfChange, Uri uri) {
        getInfoAndSend();
        super.onChange(selfChange, uri);
    }

    protected void getInfoAndSend(){

    }
}
