package com.ntua.ote.logger.models;

import com.ntua.ote.logger.utils.RequestType;

public class AsyncResponseDetails {

    private long localId;
    private long remoteId;
    private boolean success;
    private RequestType requestType;

    public AsyncResponseDetails(long localId, long remoteId, boolean success, RequestType requestType) {
        this.localId = localId;
        this.remoteId = remoteId;
        this.success = success;
        this.requestType = requestType;
    }

    public long getLocalId() {
        return localId;
    }

    public long getRemoteId() {
        return remoteId;
    }

    public boolean isSuccess() {
        return success;
    }

    public RequestType getRequestType() {
        return requestType;
    }
}
