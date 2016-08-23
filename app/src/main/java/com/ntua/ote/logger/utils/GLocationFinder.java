package com.ntua.ote.logger.utils;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.ntua.ote.logger.db.CallLogDbHelper;

public class GLocationFinder implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private boolean ready;
    private Context context;

    private static final String TAG = GLocationFinder.class.getName();

    public GLocationFinder(Context context){
        this.context = context;
    }

    public void start(){
        ready = false;
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();
    }

    public void stop() {
        ready = false;
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        ready = true;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public void getLocation(long rowId){
        if(ready) {
            try {
                Location location = LocationServices.FusedLocationApi.getLastLocation(
                        mGoogleApiClient);
                double longitude = 0.0;
                double latitude = 0.0;
                if (location != null) {
                    latitude = location.getLatitude();
                }
                if (location != null) {
                    longitude = location.getLongitude();
                }
                Log.d(TAG, latitude + " " + longitude);
                CallLogDbHelper.getInstance(context).update(latitude, longitude, rowId);
            } catch (SecurityException e){

            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "connection failed" + connectionResult);
    }
}
