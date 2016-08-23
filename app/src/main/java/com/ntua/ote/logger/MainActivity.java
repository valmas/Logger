package com.ntua.ote.logger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.jaredrummler.android.device.DeviceName;
import com.ntua.ote.logger.db.CallLogDbHelper;
import com.ntua.ote.logger.models.PhoneDetails;
import com.ntua.ote.logger.utils.CommonUtils;
import com.ntua.ote.logger.utils.Constants;
import com.ntua.ote.logger.utils.LogType;
import com.ntua.ote.logger.utils.PermissionsMapping;


public class MainActivity extends AppCompatActivity {

    private Intent callLogServiceIntent;

    private static final String TAG = MainActivity.class.getName();

    private BroadcastReceiver callLogReceiver;

    private TelephonyManager tm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        callLogReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int code = intent.getIntExtra(CallLogService.COPA_MESSAGE, 0);
                if(code == 1) {
                    initFromDb();
                }
            }
        };

        initFromPreferences();
        initFromDb();
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

    public void initFromDb(){
        long callNum = CallLogDbHelper.getInstance(this).getCallCount();
        ((TextView) this.findViewById(R.id.calls_num)).setText(String.valueOf(callNum));
        long smsNum = CallLogDbHelper.getInstance(this).getSmsCount();
        ((TextView) this.findViewById(R.id.sms_num)).setText(String.valueOf(smsNum));
    }

    public void getPhoneDetails(){
        ApplicationController.getInstance().updatePhoneDetails(tm);
        PhoneDetails phoneDetails = ApplicationController.getInstance().getPhoneDetails();

        TextView tv = (TextView) findViewById(R.id.brandModel);
        tv.setText(phoneDetails.getBrandModel());
        tv = (TextView) findViewById(R.id.version);
        tv.setText(phoneDetails.getVersion());
        tv = (TextView) findViewById(R.id.imei);
        tv.setText(phoneDetails.getImei());
        tv = (TextView) findViewById(R.id.imsi);
        tv.setText(phoneDetails.getImsi());

        tv = (TextView) findViewById(R.id.msisdn);
        String msisdn = phoneDetails.getMsisdn();
        if("".equals(msisdn)){
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            msisdn = sharedPref.getString(SettingsActivity.KEY_PREF_MSISDN, "");
            if("".equals(msisdn)) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(SettingsActivity.KEY_PREF_MSISDN, "6976497960");
                editor.apply();
                tv.setText("6976497960");
            } else {
                tv.setText(msisdn);
            }
        } else {
            tv.setText(msisdn);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume(){
        super.onResume();
        initFromPreferences();
        initFromDb();
        LocalBroadcastManager.getInstance(this).registerReceiver((callLogReceiver),
                new IntentFilter(CallLogService.COPA_RESULT)
        );
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).registerReceiver((callLogReceiver),
                new IntentFilter(CallLogService.COPA_RESULT)
        );
        super.onPause();
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

    public void expandCollapse(View v){
        v = findViewById(R.id.phone_details);
        if(v.getVisibility() == View.VISIBLE) {
            collapse(v);
        } else {
            expand(v);
        }
    }

    public void expand(final View v) {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public void viewCallsList(View view) {
        Intent intent = new Intent(this, CallsViewActivity.class);
        Bundle b = new Bundle();
        b.putInt(Constants.LOG_TYPE_KEY, LogType.CALL.code);
        intent.putExtras(b); //Put your id to your next Intent
        startActivity(intent);
    }

    public void viewSmsList(View view) {
        Intent intent = new Intent(this, CallsViewActivity.class);
        Bundle b = new Bundle();
        b.putInt(Constants.LOG_TYPE_KEY, LogType.SMS.code);
        intent.putExtras(b); //Put your id to your next Intent
        startActivity(intent);
    }
}