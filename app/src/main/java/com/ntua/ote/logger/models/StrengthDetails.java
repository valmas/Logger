package com.ntua.ote.logger.models;

public class StrengthDetails {

    private int rssi;
    private String LTE_rsrp;
    private String LTE_rsrq;
    private String LTE_rssnr;
    private String LTE_cqi;
    private String rat;

    public StrengthDetails() {
    }

    public StrengthDetails(int rssi, String LTE_rsrp, String LTE_rsrq, String LTE_rssnr, String LTE_cqi) {
        this.rssi = rssi;
        this.LTE_rsrp = LTE_rsrp;
        this.LTE_rsrq = LTE_rsrq;
        this.LTE_rssnr = LTE_rssnr;
        this.LTE_cqi = LTE_cqi;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public String getLTE_rsrp() {
        return LTE_rsrp;
    }

    public void setLTE_rsrp(String LTE_rsrp) {
        this.LTE_rsrp = LTE_rsrp;
    }

    public String getLTE_rsrq() {
        return LTE_rsrq;
    }

    public void setLTE_rsrq(String LTE_rsrq) {
        this.LTE_rsrq = LTE_rsrq;
    }

    public String getLTE_rssnr() {
        return LTE_rssnr;
    }

    public void setLTE_rssnr(String LTE_rssnr) {
        this.LTE_rssnr = LTE_rssnr;
    }

    public String getLTE_cqi() {
        return LTE_cqi;
    }

    public void setLTE_cqi(String LTE_cqi) {
        this.LTE_cqi = LTE_cqi;
    }

    public String getRat() {
        return rat;
    }

    public void setRat(String rat) {
        this.rat = rat;
    }
}
