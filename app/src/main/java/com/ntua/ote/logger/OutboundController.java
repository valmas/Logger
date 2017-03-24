package com.ntua.ote.logger;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ntua.ote.logger.db.CallLogDbHelper;
import com.ntua.ote.logger.db.CallLogDbSchema;
import com.ntua.ote.logger.models.AsyncResponseLogDetails;
import com.ntua.ote.logger.models.rs.DurationRequest;
import com.ntua.ote.logger.models.rs.InitialRequest;
import com.ntua.ote.logger.models.rs.LocationRequest;
import com.ntua.ote.logger.utils.AsyncResponse;
import com.ntua.ote.logger.utils.CommonUtils;
import com.ntua.ote.logger.utils.RequestType;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class OutboundController implements AsyncResponse<AsyncResponseLogDetails> {

    private Map<Long, InitialRequest> pendingInitialRequests;
    private Map<Long, LocationRequest> pendingLocationRequests;
    private Map<Long, DurationRequest> pendingDurationRequests;

    private static OutboundController ourInstance;

    private Context context;

    public static OutboundController getInstance(Context context) {
        if(ourInstance == null) {
            ourInstance = new OutboundController(context);
        }
        return ourInstance;
    }

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
                .getPending(CallLogDbSchema.PendingRequestEntry.COLUMN_NAME_INITIAL);
        if(duration != null) {
            Type typeOfHashMap = new TypeToken<Map<Long, DurationRequest>>() { }.getType();
            pendingDurationRequests = gson.fromJson(duration, typeOfHashMap);
        } else {
            pendingDurationRequests = new HashMap<>();
        }
        CallLogDbHelper.getInstance(context).deletePending();
    }

    public synchronized void networkConnected(){
        if(!pendingInitialRequests.isEmpty()) {
            for (Map.Entry<Long, InitialRequest> entry : pendingInitialRequests.entrySet()) {
                new RestClient(this).execute(RequestType.INITIAL, entry.getValue(), entry.getKey());
            }
        }
        sendSubsequentRequests();
    }

    public synchronized void serviceStarted(){
//        if(CommonUtils.haveNetworkConnection(context)) {
//            networkConnected();
//        }
    }

    public synchronized void sendSubsequentRequests(){
        if(!pendingLocationRequests.isEmpty()) {
            for (Map.Entry<Long, LocationRequest> entry : pendingLocationRequests.entrySet()) {
                if(entry.getValue().getRowId() < 1) {
                    long remoteId = CallLogDbHelper.getInstance(context).getRemoteId(entry.getKey());
                    if(remoteId > 0) {
                        entry.getValue().setRowId(remoteId);
                        new RestClient(this).execute(RequestType.LOCATION, entry.getValue(), entry.getKey());
                    }
                } else {
                    new RestClient(this).execute(RequestType.LOCATION, entry.getValue(), entry.getKey());
                }
            }
        }
        if(!pendingDurationRequests.isEmpty()) {
            for (Map.Entry<Long, DurationRequest> entry : pendingDurationRequests.entrySet()) {
                if(entry.getValue().getRowId() < 1) {
                    long remoteId = CallLogDbHelper.getInstance(context).getRemoteId(entry.getKey());
                    if(remoteId > 0) {
                        entry.getValue().setRowId(remoteId);
                        new RestClient(this).execute(RequestType.DURATION, entry.getValue(), entry.getKey());
                    }
                } else {
                    new RestClient(this).execute(RequestType.DURATION, entry.getValue(), entry.getKey());
                }
            }
        }
    }

    public synchronized void newEntryAdded(long localId, InitialRequest initialRequest){
        pendingInitialRequests.put(localId, initialRequest);
        if(CommonUtils.haveNetworkConnection(context)) {
            new RestClient(this).execute(RequestType.INITIAL, initialRequest, localId);
        }
    }

    public synchronized void locationAdded(long localId, LocationRequest locationRequest){
        if(!pendingLocationRequests.containsKey(localId)) {
            pendingLocationRequests.put(localId, locationRequest);
        }
        if(CommonUtils.haveNetworkConnection(context)) {
            long remoteId = CallLogDbHelper.getInstance(context).getRemoteId(localId);
            if(remoteId > 0) {
                locationRequest.setRowId(remoteId);
                new RestClient(this).execute(RequestType.LOCATION, locationRequest, localId);
            }
        }
    }

    public synchronized void durationAdded(long localId, DurationRequest durationRequest){
        pendingDurationRequests.put(localId, durationRequest);
        if(CommonUtils.haveNetworkConnection(context)) {
            long remoteId = CallLogDbHelper.getInstance(context).getRemoteId(localId);
            if(remoteId > 0) {
                durationRequest.setRowId(remoteId);
                new RestClient(this).execute(RequestType.DURATION, durationRequest, localId);
            }
        }
    }

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
                                pendingLocationRequests.get(localId), localId);
                    }
                    if(pendingDurationRequests.get(localId) != null) {
                        pendingDurationRequests.get(localId).setRowId(output.getRemoteId());
                        new RestClient(this).execute(RequestType.DURATION,
                                pendingDurationRequests.get(localId), localId);
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
        }
        CallLogDbHelper.getInstance(context).insertPending(initial, location, duration);
        ourInstance=null;
    }
}
