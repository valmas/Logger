package com.ntua.ote.logger.models;

import com.ntua.ote.logger.utils.Direction;
import com.ntua.ote.logger.utils.LogType;

import java.util.Date;

public class LogDetails extends StrengthDetails {

    private String externalNumber;
    private LogType type;
    private Date dateTime;
    private int duration;
    private Direction direction;
    private String smsContent;
    private double latitude;
    private double longitude;
    private int cellId;
    private int lac;
    private String rat;
    private int mnc;
    private int mcc;

    public LogDetails(String externalNumber, Date dateTime, Direction direction) {
        this.externalNumber = externalNumber;
        this.dateTime = dateTime;
        this.direction = direction;
        type = LogType.CALL;
    }

    public LogDetails(String externalNumber, Date dateTime, Direction direction, String smsContent) {
        this.externalNumber = externalNumber;
        this.dateTime = dateTime;
        this.direction = direction;
        this.smsContent = smsContent;
        type = LogType.SMS;
    }

    public LogDetails(LogType type, String externalNumber, Date dateTime, int duration, String smsContent,
                      Direction direction, double latitude, double longitude, int cellId, int lac, String rat,
                      int mnc, int mcc, int rssi, String LTE_rsrp, String LTE_rsrq, String LTE_rssnr, String LTE_cqi) {
        super(rssi, LTE_rsrp, LTE_rsrq, LTE_rssnr, LTE_cqi);
        this.type = type;
        this.externalNumber = externalNumber;
        this.dateTime = dateTime;
        this.duration = duration;
        this.direction = direction;
        this.smsContent = smsContent;
        this.latitude = latitude;
        this.longitude = longitude;
        this.cellId = cellId;
        this.lac = lac;
        this.rat = rat;
        this.mnc = mnc;
        this.mcc = mcc;
    }

    public String getExternalNumber() {
        return externalNumber;
    }

    public void setExternalNumber(String externalNumber) {
        this.externalNumber = externalNumber;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getSmsContent() {
        return smsContent;
    }

    public void setSmsContent(String smsContent) {
        this.smsContent = smsContent;
    }

    public LogType getType() {
        return type;
    }

    public void setType(LogType type) {
        this.type = type;
    }

    public int getCellId() {
        return cellId;
    }

    public void setCellId(int cellId) {
        this.cellId = cellId;
    }

    public int getLac() {
        return lac;
    }

    public void setLac(int lac) {
        this.lac = lac;
    }

    public String getRat() {
        return rat;
    }

    public void setRat(String rat) {
        this.rat = rat;
    }

    public int getMnc() {
        return mnc;
    }

    public void setMnc(int mnc) {
        this.mnc = mnc;
    }

    public int getMcc() {
        return mcc;
    }

    public void setMcc(int mcc) {
        this.mcc = mcc;
    }
}
