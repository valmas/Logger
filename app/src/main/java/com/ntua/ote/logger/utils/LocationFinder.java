package com.ntua.ote.logger.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import com.ntua.ote.logger.OutboundController;
import com.ntua.ote.logger.db.CallLogDbHelper;
import com.ntua.ote.logger.models.rs.LocationRequest;
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
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000;
    // Declaring a Location Manager
    protected LocationManager locationManager;

    private LocationFinder(Context context) {
        this.mContext = context;
        pendingIds = new PriorityQueue<>();
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
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
            if(!isLocFinderRunning) {
                startUsingLocation();
            }
        } else {
            LocationRequest locationRequest = new LocationRequest(0, 0, 0, false);
            OutboundController.getInstance(mContext).locationAdded(id, locationRequest);
        }
    }

    public void removeIdFromPending(Long id){
        if(pendingIds.remove(id)) {
            LocationRequest locationRequest = new LocationRequest(0, 0, 0, false);
            OutboundController.getInstance(mContext).locationAdded(id, locationRequest);
        }
    }

    public void startUsingLocation() {
        if (canGetLocation()) {
            try {
                if (isNetworkEnabled) {locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    isLocFinderRunning = true;
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
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
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     * */
    public boolean canGetLocation() {
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Log.d(TAG, isGPSEnabled + " " + isNetworkEnabled);
        return isGPSEnabled || isNetworkEnabled;
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
            LocationRequest locationRequest = new LocationRequest(0, latitude, longitude, true);
            OutboundController.getInstance(mContext).locationAdded(id, locationRequest);
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