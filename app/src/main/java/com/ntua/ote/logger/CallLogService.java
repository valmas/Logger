package com.ntua.ote.logger;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.ntua.ote.logger.db.CallLogDbHelper;
import com.ntua.ote.logger.models.LogDetails;
import com.ntua.ote.logger.models.PhoneDetails;
import com.ntua.ote.logger.models.StrengthDetails;
import com.ntua.ote.logger.models.rs.InitialRequest;
import com.ntua.ote.logger.utils.CommonUtils;
import com.ntua.ote.logger.utils.GLocationFinder;
import com.ntua.ote.logger.utils.LocationFinder;
import com.ntua.ote.logger.utils.LogType;
import com.ntua.ote.logger.utils.RequestType;

import java.util.Date;

public class CallLogService extends Service{

    private AbstractObserver callObserver;
    private AbstractObserver smsObserver;
    private LocalBroadcastManager broadcaster;
    private OutgoingCallsReceiver outgoingCallsReceiver;
    private IncomingSmsListener incomingSmsListener;
    private PhoneCallListener phoneCallListener;
    private TelephonyManager tm;
    private GLocationFinder gLocationFinder;
    private PhoneStateListener signalStrengthListener;
    private StrengthDetails strengthDetails;
    private NetworkConnectivityReceiver networkConnectivityReceiver;

    static final public String COPA_RESULT = "com.ntua.ote.logger.CallLogService.REQUEST_PROCESSED";

    static final public String COPA_MESSAGE = "com.ntua.ote.logger.CallLogService.COPA_MSG";

    private static final String TAG = CallLogService.class.getName();

    @Override
    public void onDestroy() {
        gLocationFinder.stop();
        unregisterReceiver(outgoingCallsReceiver);
        unregisterReceiver(incomingSmsListener);
        getContentResolver().unregisterContentObserver(callObserver);
        getContentResolver().unregisterContentObserver(smsObserver);
        unregisterReceiver(networkConnectivityReceiver);
        tm.listen(phoneCallListener, PhoneStateListener.LISTEN_NONE );
        tm.listen(signalStrengthListener, PhoneStateListener.LISTEN_NONE);

        OutboundController.getInstance(this).destroy();

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        gLocationFinder = new GLocationFinder(this);
        gLocationFinder.start();
        Date latestCall = getLogsLatestCall();
        tm = (TelephonyManager) getSystemService( Context.TELEPHONY_SERVICE );
        broadcaster = LocalBroadcastManager.getInstance(this);
        CallLogDbHelper.getInstance(this).setService(this);
        if(callObserver == null) {
            callObserver = new CallObserver(new Handler(), this, latestCall);
            getContentResolver().registerContentObserver(
                    CallLog.Calls.CONTENT_URI, true, callObserver);
        }

        if(smsObserver == null) {
            smsObserver = new OutgoingSmsObserver(new Handler(), this);
            getContentResolver().registerContentObserver(
                    Telephony.Sms.CONTENT_URI, true, smsObserver);
        }

        phoneCallListener = new PhoneCallListener(this);
        tm.listen(phoneCallListener, PhoneStateListener.LISTEN_CALL_STATE );

        IntentFilter filter = new IntentFilter("android.intent.action.NEW_OUTGOING_CALL");
        outgoingCallsReceiver = new OutgoingCallsReceiver(this);
        registerReceiver(outgoingCallsReceiver,filter);

        filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        incomingSmsListener = new IncomingSmsListener(this);
        registerReceiver(incomingSmsListener,filter);

        strengthDetails = new StrengthDetails();

        signalStrengthListener = new PhoneStateListener() {
            @Override
            public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                super.onSignalStrengthsChanged(signalStrength);
                CommonUtils.getRS(signalStrength, tm, strengthDetails);
            }
        };
        tm.listen(signalStrengthListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

        filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkConnectivityReceiver = new NetworkConnectivityReceiver();
        registerReceiver(networkConnectivityReceiver,filter);

        ApplicationController.getInstance().updatePhoneDetails(this);

        OutboundController.getInstance(this).serviceStarted();

    }

    public void storeAndSend(LogDetails logDetails){
        logDetails.setCellId(CommonUtils.getCelliId(this));
        logDetails.setLac(CommonUtils.getLat(this));
        logDetails.setRssi(strengthDetails.getRssi());
        logDetails.setLTE_rsrp(strengthDetails.getLTE_rsrp());
        logDetails.setLTE_rsrq(strengthDetails.getLTE_rsrq());
        logDetails.setLTE_rssnr(strengthDetails.getLTE_rssnr());
        logDetails.setLTE_cqi(strengthDetails.getLTE_cqi());
        logDetails.setRat(CommonUtils.getRat(this));
        logDetails.setMnc(CommonUtils.getMobileNetworkCode(this));
        logDetails.setMcc(CommonUtils.getMobileCountryCode(this));

        PhoneDetails phoneDetails = ApplicationController.getInstance().getPhoneDetails();
        InitialRequest initialRequest = new InitialRequest(phoneDetails.getBrandModel(), phoneDetails.getVersion(),
                phoneDetails.getImei(), phoneDetails.getImsi(), phoneDetails.getMsisdn(), logDetails.getExternalNumber(),
                logDetails.getDateTime(), logDetails.getSmsContent(), logDetails.getDirection(), logDetails.getCellId(),
                logDetails.getLac(), logDetails.getRssi(), logDetails.getMnc(), logDetails.getMcc(), logDetails.getLTE_rsrp(),
                logDetails.getLTE_rsrq(), logDetails.getLTE_rssnr(), logDetails.getLTE_cqi(), logDetails.getType(), logDetails.getRat());

        long rowId = CallLogDbHelper.getInstance(this).insert(logDetails);
        OutboundController.getInstance(this).newEntryAdded(rowId, initialRequest);
        if(rowId != -1) {
            if(logDetails.getType() == LogType.CALL) {
                ApplicationController.getInstance().addUnfinishedCall(logDetails.getExternalNumber(), rowId);
            }
            LocationFinder.getInstance(this).getLocation(rowId);
            //gLocationFinder.getLocation(rowId);
        }
    }

    public Date getLogsLatestCall(){
        Uri callLogUri = CallLog.Calls.CONTENT_URI;
        Date callDayTime = null;
        try{
            Cursor managedCursor = this.getApplicationContext().getContentResolver().query(callLogUri, null, null, null, null);
            int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
            managedCursor.moveToLast();
            if (!managedCursor.isAfterLast()) {
                String callDate = managedCursor.getString(date);
                callDayTime = new Date(Long.valueOf(callDate));
            }
            managedCursor.close();
        } catch (SecurityException e) {
            Log.e("CallLogService", "<init> READ CALL LOG permission not found");
        }
        return callDayTime;
    }

    public void dbUpdated(int code) {
        Intent intent = new Intent(COPA_RESULT);
        intent.putExtra(COPA_MESSAGE, code);
        broadcaster.sendBroadcast(intent);
    }

}