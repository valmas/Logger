package com.ntua.ote.logger.models.rs;

import com.ntua.ote.logger.utils.Direction;
import com.ntua.ote.logger.utils.LogType;

import java.util.Date;

public class InitialRequest extends AuthenticationRequest {

	private String brandModel;
	private String version;
	private String imei;
	private String imsi;
	private String phoneNumber;
	private String externalPhoneNumber;
	private Date dateTime;
	private String smsContent;
	private Direction direction;
	private int cellId;
	private int lac;
	private int rssi;
	private String lteRsrp;
	private String lteRsrq;
	private String lteRssnr;
	private String lteCqi;
	private LogType logType;
	private String rat;
	private int mnc;
	private int mcc;

	public InitialRequest(String brandModel, String version, String imei, String imsi,
						  String phoneNumber, String externalPhoneNumber, Date dateTime,
						  String smsContent, Direction direction, int cellId, int lac, int rssi, int mnc, int mcc,
						  String LTE_rsrp, String LTE_rsrq, String LTE_rssnr, String LTE_cqi, LogType logType, String rat) {
		this.brandModel = brandModel;
		this.version = version;
		this.imei = imei;
		this.imsi = imsi;
		this.phoneNumber = phoneNumber;
		this.externalPhoneNumber = externalPhoneNumber;
		this.dateTime = dateTime;
		this.smsContent = smsContent;
		this.direction = direction;
		this.cellId = cellId;
		this.lac = lac;
		this.rssi = rssi;
		this.lteRsrp = LTE_rsrp;
		this.lteRsrq = LTE_rsrq;
		this.lteRssnr = LTE_rssnr;
		this.lteCqi = LTE_cqi;
		this.logType = logType;
		this.rat = rat;
		this.mnc = mnc;
		this.mcc = mcc;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getExternalPhoneNumber() {
		return externalPhoneNumber;
	}

	public void setExternalPhoneNumber(String externalPhoneNumber) {
		this.externalPhoneNumber = externalPhoneNumber;
	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public String getSmsContent() {
		return smsContent;
	}

	public void setSmsContent(String smsContent) {
		this.smsContent = smsContent;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
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

	public int getRssi() {
		return rssi;
	}

	public void setRssi(int rssi) {
		this.rssi = rssi;
	}

	public String getBrandModel() {
		return brandModel;
	}

	public void setBrandModel(String brandModel) {
		this.brandModel = brandModel;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getImsi() {
		return imsi;
	}

	public void setImsi(String imsi) {
		this.imsi = imsi;
	}

	public String getLteRsrp() {
		return lteRsrp;
	}

	public void setLteRsrp(String lteRsrp) {
		this.lteRsrp = lteRsrp;
	}

	public String getLteRsrq() {
		return lteRsrq;
	}

	public void setLteRsrq(String lteRsrq) {
		this.lteRsrq = lteRsrq;
	}

	public String getLteRssnr() {
		return lteRssnr;
	}

	public void setLteRssnr(String lteRssnr) {
		this.lteRssnr = lteRssnr;
	}

	public String getLteCqi() {
		return lteCqi;
	}

	public void setLteCqi(String lteCqi) {
		this.lteCqi = lteCqi;
	}

	public LogType getLogType() {
		return logType;
	}

	public void setLogType(LogType logType) {
		this.logType = logType;
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
