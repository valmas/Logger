package com.ntua.ote.logger;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ntua.ote.logger.db.CallLogDbHelper;
import com.ntua.ote.logger.db.CallLogDbSchema;
import com.ntua.ote.logger.models.AsyncResponseLogDetails;
import com.ntua.ote.logger.models.rs.AuthenticationRequest;
import com.ntua.ote.logger.models.rs.DurationRequest;
import com.ntua.ote.logger.models.rs.InitialRequest;
import com.ntua.ote.logger.models.rs.LocationRequest;
import com.ntua.ote.logger.utils.AsyncResponse;
import com.ntua.ote.logger.utils.CommonUtils;
import com.ntua.ote.logger.utils.Constants;
import com.ntua.ote.logger.utils.RequestType;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class OutboundController implements AsyncResponse<AsyncResponseLogDetails> {

    public static final String TAG = OutboundController.class.getSimpleName();

    private Map<Long, InitialRequest> pendingInitialRequests;
    private Map<Long, LocationRequest> pendingLocationRequests;
    private Map<Long, DurationRequest> pendingDurationRequests;

    private static OutboundController ourInstance;

    private Context context;

    public static OutboundController getInstance(Context context) {
        if(ourInstance == null) {
            ourInstance = new OutboundController(context);
        } else {
            ourInstance.context = context;
        }
        return ourInstance;
    }

    /** Initializes OutboundController by invoking the database to retrieve any pending logs that have
     *  not been sent to the server
     */
    private OutboundController(Context context){
        this.context = context;
        Gson gson = new Gson();
        String initial = CallLogDbHelper.getInstance(context)
                .getPending(CallLogDbSchema.PendingRequestEntry.COLUMN_NAME_INITIAL);
        if(initial != null) {
            Type typeOfHashMap = new TypeToken<Map<Long, InitialRequest>>() { }.getType();
            pendingInitialRequests = gson.fromJson(initial, typeOfHashMap);
        } else {
            pendingInitialRequests = new HashMap<>();
        }
        String location = CallLogDbHelper.getInstance(context)
                .getPending(CallLogDbSchema.PendingRequestEntry.COLUMN_NAME_LOCATION);
        if(location != null) {
            Type typeOfHashMap = new TypeToken<Map<Long, LocationRequest>>() { }.getType();
            pendingLocationRequests = gson.fromJson(location, typeOfHashMap);
        } else {
            pendingLocationRequests = new HashMap<>();
        }
        String duration = CallLogDbHelper.getInstance(context)
                .getPending(CallLogDbSchema.PendingRequestEntry.COLUMN_NAME_DURATION);
        if(duration != null) {
            Log.i(TAG, duration);
            Type typeOfHashMap = new TypeToken<Map<Long, DurationRequest>>() { }.getType();
            pendingDurationRequests = gson.fromJson(duration, typeOfHashMap);
        } else {
            pendingDurationRequests = new HashMap<>();
        }
        CallLogDbHelper.getInstance(context).deletePending();
    }

    /** When internet is available tries to send the pending requests */
    public synchronized void networkConnected(){
        Log.i(TAG, "network Connected");
        if(!pendingInitialRequests.isEmpty()) {
            for (Map.Entry<Long, InitialRequest> entry : pendingInitialRequests.entrySet()) {
                new RestClient(this).execute(RequestType.INITIAL, entry.getValue(), entry.getKey(), context);
            }
        }
        sendSubsequentRequests();
    }

    /** Sends those LOCATION and DURATION requests that their corresponding INITIAL request
     *  has been sent successfully */
    public synchronized void sendSubsequentRequests(){
        if(!pendingLocationRequests.isEmpty()) {
            for (Map.Entry<Long, LocationRequest> entry : pendingLocationRequests.entrySet()) {
                if(entry.getValue().getRowId() < 1) {
                    long remoteId = CallLogDbHelper.getInstance(context).getRemoteId(entry.getKey());
                    if(remoteId > 0) {
                        entry.getValue().setRowId(remoteId);
                        new RestClient(this).execute(RequestType.LOCATION, entry.getValue(), entry.getKey(), context);
                    }
                } else {
                    new RestClient(this).execute(RequestType.LOCATION, entry.getValue(), entry.getKey(), context);
                }
            }
        }
        if(!pendingDurationRequests.isEmpty()) {
            for (Map.Entry<Long, DurationRequest> entry : pendingDurationRequests.entrySet()) {
                if(entry.getValue().getRowId() < 1) {
                    long remoteId = CallLogDbHelper.getInstance(context).getRemoteId(entry.getKey());
                    if(remoteId > 0) {
                        entry.getValue().setRowId(remoteId);
                        new RestClient(this).execute(RequestType.DURATION, entry.getValue(), entry.getKey(), context);
                    }
                } else {
                    new RestClient(this).execute(RequestType.DURATION, entry.getValue(), entry.getKey(), context);
                }
            }
        }
    }

    /** When a new entry has been logged try to sent it to the server */
    public synchronized void newEntryAdded(long localId, InitialRequest initialRequest){
        Log.i(TAG, "new entry");
        setAuthentication(initialRequest);
        pendingInitialRequests.put(localId, initialRequest);
        if(CommonUtils.haveNetworkConnectionPermitted(context)) {
            new RestClient(this).execute(RequestType.INITIAL, initialRequest, localId, context);
        }
    }

    /** When a new location entry has been logged try to sent it to the server when its
     * corresponding INITIAL request has been sent successfully */
    public synchronized void locationAdded(long localId, LocationRequest locationRequest){
        setAuthentication(locationRequest);
        if(!pendingLocationRequests.containsKey(localId)) {
            pendingLocationRequests.put(localId, locationRequest);
        }
        if(CommonUtils.haveNetworkConnectionPermitted(context)) {
            long remoteId = CallLogDbHelper.getInstance(context).getRemoteId(localId);
            if(remoteId > 0) {
                locationRequest.setRowId(remoteId);
                new RestClient(this).execute(RequestType.LOCATION, locationRequest, localId, context);
            }
        }
    }

    /** When a new duration entry has been logged try to sent it to the server when its
     * corresponding INITIAL request has been sent successfully */
    public synchronized void durationAdded(long localId, DurationRequest durationRequest){
        setAuthentication(durationRequest);
        pendingDurationRequests.put(localId, durationRequest);
        if(CommonUtils.haveNetworkConnectionPermitted(context)) {
            long remoteId = CallLogDbHelper.getInstance(context).getRemoteId(localId);
            if(remoteId > 0) {
                durationRequest.setRowId(remoteId);
                new RestClient(this).execute(RequestType.DURATION, durationRequest, localId, context);
            }
        }
    }

    /** On successful submission of a request to the server this method is invoked. If the request
     *  ia an INITIAL request the pending request is deleted and its corresponding duration and
     *  location requests are trying to be sent. If the request is a DURATION or a LOCATION the
     *  pending request is deleted */
    @Override
    public synchronized void processFinish(AsyncResponseLogDetails output) {
        if(output != null && output.isSuccess()) {
            switch (output.getRequestType()) {
                case INITIAL:
                    pendingInitialRequests.remove(output.getLocalId());
                    long localId = output.getLocalId();
                    CallLogDbHelper.getInstance(context).setRemoteId(localId, output.getRemoteId());
                    if(pendingLocationRequests.get(localId) != null) {
                        pendingLocationRequests.get(localId).setRowId(output.getRemoteId());
                        new RestClient(this).execute(RequestType.LOCATION,
                                pendingLocationRequests.get(localId), localId, context);
                    }
                    if(pendingDurationRequests.get(localId) != null) {
                        pendingDurationRequests.get(localId).setRowId(output.getRemoteId());
                        new RestClient(this).execute(RequestType.DURATION,
                                pendingDurationRequests.get(localId), localId, context);
                    }
                    break;
                case LOCATION:
                    pendingLocationRequests.remove(output.getLocalId());
                    break;
                case DURATION:
                    pendingDurationRequests.remove(output.getLocalId());
                    break;
            }
        }
    }

    /** On termination of the service all the pending requests are stored to the Database in order
     * to be sent the next time that the service starts */
    public void destroy(){
        String initial = null;
        String location = null;
        String duration = null;
        Gson gson = new Gson();
        if(!pendingInitialRequests.isEmpty()) {
            initial = gson.toJson(pendingInitialRequests);
        }
        if(!pendingLocationRequests.isEmpty()) {
            location = gson.toJson(pendingLocationRequests);
        }
        if(!pendingDurationRequests.isEmpty()) {
            duration = gson.toJson(pendingDurationRequests);
            Log.i(TAG, duration);
        }
        CallLogDbHelper.getInstance(context).insertPending(initial, location, duration);
        ourInstance=null;
    }

    private void setAuthentication(AuthenticationRequest authRequest){
        authRequest.setUserName(Constants.SERVER_USERNAME);
        authRequest.setPassword(Constants.SERVER_PASSWORD);
    }
}
