package com.ntua.ote.logger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.jaredrummler.android.device.DeviceName;
import com.ntua.ote.logger.utils.CommonUtils;


public class MainActivity extends AppCompatActivity {

    private Intent callLogServiceIntent;
    private Intent smsLogServiceIntent;

    private static final String TAG = MainActivity.class.getName();

    private BroadcastReceiver callLogReceiver;

    private TelephonyManager tm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setBroadcastReceivers();
        initFromPreferences();
        tm = (TelephonyManager) getSystemService( Context.TELEPHONY_SERVICE );

        if(CommonUtils.havePermissions(PermissionsMapping.INIT_PERMISSIONS, this)) {
            getPhoneDetails();
        } else {
            CommonUtils.requestPermission(PermissionsMapping.INIT_PERMISSIONS, this);
        }

        callLogServiceIntent = new Intent(this, CallLogService.class);
        if(CommonUtils.isServiceRunning(CallLogService.class, this)) {
            Log.d(TAG, "service is running");
            ToggleButton sw = (ToggleButton) findViewById(R.id.launch_btn);
            sw.setChecked(true);
        }


    }

    public void initFromPreferences(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean value = sharedPref.getBoolean(SettingsActivity.KEY_PREF_KEEP_SCREEN_ON, false);
        this.findViewById(R.id.main_layout).setKeepScreenOn(value);
    }

    public void getPhoneDetails(){
        String deviceName = DeviceName.getDeviceName();
        TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String imsi = mTelephonyMgr.getSubscriberId();
        String imei = tm.getDeviceId();
        String version = CommonUtils.getDetailedOsVersion();
        String mPhoneNumber = mTelephonyMgr.getVoiceMailNumber();

        TextView tv = (TextView) findViewById(R.id.brandModel);
        tv.setText(deviceName);
        tv = (TextView) findViewById(R.id.version);
        tv.setText(version);
        tv = (TextView) findViewById(R.id.imei);
        tv.setText(imei);
        tv = (TextView) findViewById(R.id.imsi);
        tv.setText(imsi);
        tv = (TextView) findViewById(R.id.msisdn);
        tv.setText(mPhoneNumber);
    }

    //TODO delete
    private void showinLog(String log){
        TextView tv = (TextView) findViewById(R.id.LatestLog);
        tv.setText(log);
    }

    private void setBroadcastReceivers(){
        callLogReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String log = intent.getStringExtra(CallLogService.COPA_MESSAGE);
                showinLog(log);
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver((callLogReceiver),
                new IntentFilter(CallLogService.COPA_RESULT)
        );
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(callLogReceiver);
        super.onDestroy();
    }

    @Override
    protected void onResume(){
        initFromPreferences();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length == 0 || CommonUtils.deniedPermissionExists(grantResults)) {
                    ToggleButton sw = (ToggleButton) findViewById(R.id.launch_btn);
                    sw.setChecked(false);
                } else {
                    this.startService(callLogServiceIntent);
                }
                return;
            }
            case 2: {
                if (grantResults.length == 0 || CommonUtils.deniedPermissionExists(grantResults)) {

                } else {
                    getPhoneDetails();
                }
                return;
            }
        }
    }

    public void onLogStart(View view) {
        boolean checked = ((ToggleButton) view).isChecked();
        if(checked) {
            if(CommonUtils.havePermissions(PermissionsMapping.LOGGER_PERMISSIONS, this)) {
                this.startService(callLogServiceIntent);
            } else {
                CommonUtils.requestPermission(PermissionsMapping.LOGGER_PERMISSIONS, this);
            }
        } else {
            this.stopService(callLogServiceIntent);
        }
    }

    public void settings(MenuItem item) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void refresh(View view) {

    }
}
