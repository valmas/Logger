package com.ntua.ote.logger.models.rs;

public class DurationRequest extends AuthenticationRequest {

	private long rowId;
	private int duration;

	public DurationRequest(long rowId, int duration) {
		this.rowId = rowId;
		this.duration = duration;
	}

	public long getRowId() {
		return rowId;
	}

	public void setRowId(long rowId) {
		this.rowId = rowId;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

}
