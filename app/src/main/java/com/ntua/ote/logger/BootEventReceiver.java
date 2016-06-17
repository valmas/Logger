package com.ntua.ote.logger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.CallLog;

import com.ntua.ote.logger.db.PropertiesDbHelper;

public class BootEventReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        PropertiesDbHelper mhelper = new PropertiesDbHelper(context);
        String value = mhelper.getValue("runOnStart");
        if("1".equals(value)) {
            Intent startServiceIntent = new Intent(context, CallLog.class);
            context.startService(startServiceIntent);
        }
    }
}
