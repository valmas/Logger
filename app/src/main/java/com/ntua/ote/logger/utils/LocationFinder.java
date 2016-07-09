package com.ntua.ote.logger.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.ntua.ote.logger.db.CallLogDbHelper;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public final class LocationFinder implements LocationListener {

    private static final String TAG = LocationFinder.class.getName();

    private final Context mContext;
    private Queue<Long> pendingIds;

    private static LocationFinder sInstance;

    // flag for GPS status
    private boolean isGPSEnabled = false;

    // flag for network status
    private boolean isNetworkEnabled = false;

    private boolean isLocFinderRunning = false;

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 100;

    // Declaring a Location Manager
    protected LocationManager locationManager;

    private LocationFinder(Context context) {
        this.mContext = context;
        pendingIds = new PriorityQueue<>();
        locationManager = (LocationManager) mContext
                .getSystemService(Context.LOCATION_SERVICE);
    }

    public static synchronized LocationFinder getInstance(Context context){
        if(sInstance == null) {
            sInstance = new LocationFinder(context);
        }
        return sInstance;
    }

    public void getLocation(Long id) {
        if(canGetLocation()) {
            pendingIds.add(id);
        }
        if(!isLocFinderRunning) {
            startUsingLocation();
        }
    }

    public void removeIdFromPending(Long id){
        pendingIds.remove(id);
    }

    public void startUsingLocation() {
        if (canGetLocation()) {

            try {
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    isLocFinderRunning = true;
            }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    isLocFinderRunning = true;
                }

            } catch (SecurityException e){
                Log.e(TAG, "<getLocation> Permissions not found on accessing location");
            }
        }
    }

    public void stopUsingLocation() {
        try {
            if (locationManager != null) {
                locationManager.removeUpdates(LocationFinder.this);
            }
        } catch (SecurityException e){
            Log.e(TAG, "<stopUsingGPS> Permissions not found on accessing location");
        }
    }

    /**
     * Function to get latitude
     * */
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     * */
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     * */
    public boolean canGetLocation() {
        isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Log.d(TAG, isGPSEnabled + " " + isNetworkEnabled);
        return isGPSEnabled || isNetworkEnabled;
    }

    /**
     * Function to show settings alert dialog On pressing Settings button will
     * lauch Settings Options
     * */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog
                .setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        mContext.startActivity(intent);
                    }
                });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {

        while (!pendingIds.isEmpty()) {
            Long id = pendingIds.poll();
            double longitude = 0.0;
            double latitude = 0.0;
            if (location != null) {
                latitude = location.getLatitude();
            }
            if (location != null) {
                longitude = location.getLongitude();
            }
            Log.d(TAG, latitude + " " + longitude);
            CallLogDbHelper.getInstance(mContext).update(latitude, longitude, id);
        }
        stopUsingLocation();
        isLocFinderRunning = false;
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

}