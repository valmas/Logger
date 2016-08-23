package com.ntua.ote.logger;

import android.content.Context;

import com.ntua.ote.logger.db.CallLogDbHelper;
import com.ntua.ote.logger.models.AsyncResponseDetails;
import com.ntua.ote.logger.models.rs.DurationRequest;
import com.ntua.ote.logger.models.rs.InitialRequest;
import com.ntua.ote.logger.models.rs.LocationRequest;
import com.ntua.ote.logger.utils.AsyncResponse;
import com.ntua.ote.logger.utils.CommonUtils;
import com.ntua.ote.logger.utils.RequestType;

import java.util.HashMap;
import java.util.Map;

public class OutboundController implements AsyncResponse {

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
        pendingInitialRequests = new HashMap<>();
        pendingLocationRequests = new HashMap<>();
        pendingDurationRequests = new HashMap<>();
        this.context = context;
    }

    public synchronized void networkConnected(){
        if(!pendingInitialRequests.isEmpty()) {
            for (Map.Entry<Long, InitialRequest> entry : pendingInitialRequests.entrySet()) {
                new RestClient(this).execute(RequestType.INITIAL, entry.getValue(), entry.getKey());
            }
        }
        sendSubsequentRequests();
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
        pendingLocationRequests.put(localId, locationRequest);
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
    public synchronized void processFinish(AsyncResponseDetails output) {
        if(output != null && output.isSuccess()) {
            switch (output.getRequestType()) {
                case INITIAL:
                    pendingInitialRequests.remove(output.getRemoteId());
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
                    pendingLocationRequests.remove(output.getRemoteId());
                    break;
                case DURATION:
                    pendingDurationRequests.remove(output.getRemoteId());
                    break;
            }
        }
    }
}
