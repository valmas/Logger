package com.ntua.ote.logger.models.rs;

public class LocationRequest {

	private long rowId;
	private double latitude;
	private double longitude;

    public LocationRequest(long rowId, double latitude, double longitude) {
        this.rowId = rowId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public long getRowId() {
		return rowId;
	}

	public void setRowId(long rowId) {
		this.rowId = rowId;
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
