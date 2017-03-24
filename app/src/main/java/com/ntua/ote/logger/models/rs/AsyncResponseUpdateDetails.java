package com.ntua.ote.logger.models.rs;

import com.ntua.ote.logger.utils.RequestType;

public class AsyncResponseUpdateDetails {

    private String filename;
    private RequestType requestType;
    private byte[] content;

    public AsyncResponseUpdateDetails(String filename, RequestType requestType) {
        this.filename = filename;
        this.requestType = requestType;
    }

    public AsyncResponseUpdateDetails(byte[] content, RequestType requestType) {
        this.content = content;
        this.requestType = requestType;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
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
