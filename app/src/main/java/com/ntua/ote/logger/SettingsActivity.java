package com.ntua.ote.logger;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ToggleButton;

import com.ntua.ote.logger.utils.CommonUtils;
import com.ntua.ote.logger.utils.PermissionsMapping;

public class SettingsActivity extends AppCompatActivity {

    public static final String KEY_PREF_RUN_ON_START = "pref_runOnStart";
    public static final String KEY_PREF_KEEP_SCREEN_ON = "pref_keepScreenOn";
    public static final String KEY_PREF_UPLOAD_WIFI = "pref_wifi";
    public static final String KEY_PREF_UPLOAD_DATA = "pref_data";
    public static final String KEY_PREF_UPLOAD_WIFI_DATA = "pref_wifiData";
    public static final String KEY_PREF_MSISDN = "pref_msisdn";
    public static final String KEY_PREF_UPDATE = "pref_update";
    public static final String KEY_PREF_SERVER_URL = "pref_server_url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new SettingsFragment())
                .commit();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public static void requestPermission(Activity act, PermissionsMapping pm){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(act, pm.permission[0])
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(act, pm.permission[0])) {
                ActivityCompat.requestPermissions(act, pm.permission, pm.requestCode);
            } else {
                ActivityCompat.requestPermissions(act, pm.permission, pm.requestCode);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 3: {
                if (grantResults.length == 0 || CommonUtils.deniedPermissionExists(grantResults)) {

                } else {
                    UpdateController.getInstance(this).download();
                }
                return;
            }
        }
    }

    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        private CheckBoxPreference wifiData;
        private CheckBoxPreference data;
        private CheckBoxPreference wifi;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);

            wifi = (CheckBoxPreference) findPreference(KEY_PREF_UPLOAD_WIFI);
            wifiData = (CheckBoxPreference) findPreference(KEY_PREF_UPLOAD_WIFI_DATA);
            data = (CheckBoxPreference) findPreference(KEY_PREF_UPLOAD_DATA);

            wifi.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newVal) {
                    final boolean value = (Boolean) newVal;
                    if(value) {
                        wifiData.setChecked(false);
                        data.setChecked(false);
                        wifi.setChecked(true);
                    }
                    return true;
                }
            });

            wifiData.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newVal) {
                    final boolean value = (Boolean) newVal;
                    if(value) {
                        wifi.setChecked(false);
                        data.setChecked(false);
                        wifiData.setChecked(true);
                    }
                    return true;
                }
            });

            data.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newVal) {
                    final boolean value = (Boolean) newVal;
                    if(value) {
                        wifiData.setChecked(false);
                        wifi.setChecked(false);
                        data.setChecked(true);
                    }
                    return true;
                }
            });

            Preference button = findPreference(KEY_PREF_UPDATE);
            button.setSummary(getString(R.string.current_version) + " " + ApplicationController.getVersion());

            button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    UpdateController.getInstance(getActivity()).checkVersion();
                    return true;
                }
            });
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(KEY_PREF_RUN_ON_START)) {
                CheckBoxPreference runOnStartPref = (CheckBoxPreference) findPreference(key);
                if(runOnStartPref.isChecked()) {
                    requestPermission(getActivity(), PermissionsMapping.REBOOT_NOTIF);
                }
            } else if(key.equals(KEY_PREF_KEEP_SCREEN_ON)){
                CheckBoxPreference pref = (CheckBoxPreference) findPreference(key);
                if(pref.isChecked()) {
                    getActivity().findViewById(R.id.main_layout).setKeepScreenOn(true);
                } else {
                    getActivity().getParent().findViewById(R.id.main_layout).setKeepScreenOn(false);
                }
            }
        }

    }
}
