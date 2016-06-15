package com.ntua.ote.logger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Intent callLogServiceIntent;
    private Intent smsLogServiceIntent;

    private static final String TAG = MainActivity.class.getName();

    private BroadcastReceiver callLogReceiver;
    private BroadcastReceiver smsLogReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        setBroadcastReceivers();

        callLogServiceIntent = new Intent(this, CallLogService.class);
        smsLogServiceIntent = new Intent(this, SmsLogService.class);
    }

    private void setBroadcastReceivers(){
        callLogReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String log = intent.getStringExtra(CallLogService.COPA_MESSAGE);
                TextView tv = (TextView) findViewById(R.id.LatestLog);
                tv.setText(tv.getText() + "\n" + log);
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver((callLogReceiver),
                new IntentFilter(CallLogService.COPA_RESULT)
        );

        smsLogReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String log = intent.getStringExtra(SmsLogService.COPA_MESSAGE);
                TextView tv = (TextView) findViewById(R.id.LatestLog);
                tv.setText(log);
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver((smsLogReceiver),
                new IntentFilter(SmsLogService.COPA_RESULT)
        );
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(callLogReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(smsLogReceiver);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void requestPermission(PermissionsMapping pm){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, pm.permission)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, pm.permission)) {
                ActivityCompat.requestPermissions(this, new String[]{pm.permission}, pm.requestCode);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{pm.permission}, pm.requestCode);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length == 0 || grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Switch sw = (Switch) findViewById(R.id.checkbox_callLog);
                    sw.setChecked(false);
                }
                return;
            }
            case 2: {
                if (grantResults.length == 0 || grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Switch sw = (Switch) findViewById(R.id.checkbox_smsLog);
                    sw.setChecked(false);
                }
            }
        }
    }

    public void onCallLogStart(View view) {
        requestPermission(PermissionsMapping.READ_CALL_LOG);
        boolean checked = ((Switch) view).isChecked();
        if(checked) {
            this.startService(callLogServiceIntent);
        } else {
            this.stopService(callLogServiceIntent);
        }
    }

    public void onSmsLogStart(View view) {
        requestPermission(PermissionsMapping.READ_SMS_LOG);
        boolean checked = ((Switch) view).isChecked();
        if(checked) {
            this.startService(smsLogServiceIntent);
        } else {
            this.stopService(smsLogServiceIntent);
        }
    }
}
