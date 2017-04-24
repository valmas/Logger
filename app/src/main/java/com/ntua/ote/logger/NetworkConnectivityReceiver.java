package com.ntua.ote.logger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ntua.ote.logger.utils.CommonUtils;

public class NetworkConnectivityReceiver extends BroadcastReceiver {

    private static final String TAG = NetworkConnectivityReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if(CommonUtils.haveNetworkConnectionPermitted(context)){
            OutboundController.getInstance(context).networkConnected();
        }
    }
}
