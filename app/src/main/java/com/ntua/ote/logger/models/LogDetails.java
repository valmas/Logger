package com.ntua.ote.logger.models;

import com.ntua.ote.logger.utils.Direction;

import java.util.Date;

public class LogDetails {

    private String externalNumber;
    private Date dateTime;
    private int duration;
    private Direction direction;
    private double latitude;
    private double longitude;

    public LogDetails(String externalNumber, Date dateTime, Direction direction) {
        this.externalNumber = externalNumber;
        this.dateTime = dateTime;
        this.direction = direction;
    }

    public LogDetails(String externalNumber, Date dateTime, int duration, Direction direction, double latitude, double longitude) {
        this.externalNumber = externalNumber;
        this.dateTime = dateTime;
        this.duration = duration;
        this.direction = direction;
        this.latitude = latitude;
        this.longitude = longitude;
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
}
