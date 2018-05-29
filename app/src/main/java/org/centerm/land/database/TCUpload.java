package org.centerm.land.database;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class TCUpload extends RealmObject {
	@PrimaryKey
    private int id = 0;
	private String appid; // ApplicationID 6 length
	private String tid; // TerminalID 8 length
	private String mid; // MerchantID 15 length
	private String traceNo; //Trace number 6 length
	private String transDate;
	private String transTime;
	private String amount;
	private String cardType;
	private String cardNo;
	private String track1;
	private String track2;
	private String procCode;
	private String posem;
	private String posoc;
	private String nii;
	private String expiry;
	private String refNo;
	private String iccData;
	private String ecr;
	private String apprvCode;
	private String transType;
	private String respCode;
	private String statusTC; // 1 = ส่งสำเร็จ 0 = ไม่สำเร็จ
	private String hostTypeCard;
	private String pointServiceEntryMode; // mBlockData 22
	private String applicationPAN; // mBlockData 23
	private String fee;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public String getTraceNo() {
		return traceNo;
	}

	public void setTraceNo(String traceNo) {
		this.traceNo = traceNo;
	}

	public String getTransDate() {
		return transDate;
	}

	public void setTransDate(String transDate) {
		this.transDate = transDate;
	}

	public String getTransTime() {
		return transTime;
	}

	public void setTransTime(String transTime) {
		this.transTime = transTime;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getTrack1() {
		return track1;
	}

	public void setTrack1(String track1) {
		this.track1 = track1;
	}

	public String getTrack2() {
		return track2;
	}

	public void setTrack2(String track2) {
		this.track2 = track2;
	}

	public String getProcCode() {
		return procCode;
	}

	public void setProcCode(String procCode) {
		this.procCode = procCode;
	}

	public String getPosem() {
		return posem;
	}

	public void setPosem(String posem) {
		this.posem = posem;
	}

	public String getPosoc() {
		return posoc;
	}

	public void setPosoc(String posoc) {
		this.posoc = posoc;
	}

	public String getNii() {
		return nii;
	}

	public void setNii(String nii) {
		this.nii = nii;
	}

	public String getExpiry() {
		return expiry;
	}

	public void setExpiry(String expiry) {
		this.expiry = expiry;
	}

	public String getRefNo() {
		return refNo;
	}

	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}

	public String getIccData() {
		return iccData;
	}

	public void setIccData(String iccData) {
		this.iccData = iccData;
	}

	public String getEcr() {
		return ecr;
	}

	public void setEcr(String ecr) {
		this.ecr = ecr;
	}

	public String getApprvCode() {
		return apprvCode;
	}

	public void setApprvCode(String apprvCode) {
		this.apprvCode = apprvCode;
	}

	public String getTransType() {
		return transType;
	}

	public void setTransType(String transType) {
		this.transType = transType;
	}

	public String getRespCode() {
		return respCode;
	}

	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}

	public String getStatusTC() {
		return statusTC;
	}

	public void setStatusTC(String statusTC) {
		this.statusTC = statusTC;
	}

	public String getHostTypeCard() {
		return hostTypeCard;
	}

	public void setHostTypeCard(String hostTypeCard) {
		this.hostTypeCard = hostTypeCard;
	}

	public String getPointServiceEntryMode() {
		return pointServiceEntryMode;
	}

	public void setPointServiceEntryMode(String pointServiceEntryMode) {
		this.pointServiceEntryMode = pointServiceEntryMode;
	}

	public String getApplicationPAN() {
		return applicationPAN;
	}

	public void setApplicationPAN(String applicationPAN) {
		this.applicationPAN = applicationPAN;
	}

	public String getFee() {
		return fee;
	}

	public void setFee(String fee) {
		this.fee = fee;
	}
}