package com.ntua.ote.logger;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.ntua.ote.logger.utils.PermissionsMapping;

public class SettingsActivity extends AppCompatActivity {

    public static final String KEY_PREF_RUN_ON_START = "pref_runOnStart";
    public static final String KEY_PREF_KEEP_SCREEN_ON = "pref_keepScreenOn";
    public static final String KEY_PREF_UPLOAD = "pref_upload";
    public static final String KEY_PREF_MSISDN = "pref_msisdn";

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

    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
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
            } else if(key.equals(KEY_PREF_UPLOAD)){
                ListPreference mListPreference = (ListPreference)getPreferenceScreen().findPreference(key);
                if("0".equals(mListPreference.getEntry().toString())) {

                } else {

                }
            }
        }

    }
}
