package com.ntua.ote.logger.models.rs;

import com.ntua.ote.logger.utils.RequestType;

public class AsyncResponseUpdateDetails {

    private Version version;
    private RequestType requestType;
    private byte[] content;

    public AsyncResponseUpdateDetails(Version version, RequestType requestType) {
        this.version = version;
        this.requestType = requestType;
    }

    public AsyncResponseUpdateDetails(byte[] content, RequestType requestType) {
        this.content = content;
        this.requestType = requestType;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }
}
