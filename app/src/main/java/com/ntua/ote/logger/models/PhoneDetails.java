package com.ntua.ote.logger.models;

public class PhoneDetails {

    private String brandModel;
    private String version;
    private String imei;
    private String imsi;
    private String msisdn;

    public PhoneDetails(String brandModel, String version, String imei, String imsi, String msisdn) {
        this.brandModel = brandModel;
        this.version = version;
        this.imei = imei;
        this.imsi = imsi;
        this.msisdn = msisdn;
    }

    public String getBrandModel() {
        return brandModel;
    }

    public String getVersion() {
        return version;
    }

    public String getImei() {
        return imei;
    }

    public String getImsi() {
        return imsi;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }
}
