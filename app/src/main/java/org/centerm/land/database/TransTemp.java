package org.centerm.land.database;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class TransTemp extends RealmObject {
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
	private String pointServiceEntryMode; // mBlockData 22
	private String applicationPAN; // mBlockData 23
	private String expiry;
	private String refNo;
	private String iccData;
	private String ecr;
	private String apprvCode;
	private String transType;
	private String respCode;
	private String voidFlag;
	private String closeFlag;
	private String transStat;
	private String hostTypeCard;
	private String comCode;
	private String ref1;
	private String ref2;
	private String ref3;
	private String emciId;
	private String emciFree;
	private String pin;
	private String fee;
	private String de11OnlineTMS;

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

	public String getVoidFlag() {
		return voidFlag;
	}

	public void setVoidFlag(String voidFlag) {
		this.voidFlag = voidFlag;
	}

	public String getCloseFlag() {
		return closeFlag;
	}

	public void setCloseFlag(String closeFlag) {
		this.closeFlag = closeFlag;
	}

	public String getTransStat() {
		return transStat;
	}

	public void setTransStat(String transStat) {
		this.transStat = transStat;
	}

	public String getHostTypeCard() {
		return hostTypeCard;
	}

	public void setHostTypeCard(String hostTypeCard) {
		this.hostTypeCard = hostTypeCard;
	}

	public String getComCode() {
		return comCode;
	}

	public void setComCode(String comCode) {
		this.comCode = comCode;
	}

	public String getRef1() {
		return ref1;
	}

	public void setRef1(String ref1) {
		this.ref1 = ref1;
	}

	public String getRef2() {
		return ref2;
	}

	public void setRef2(String ref2) {
		this.ref2 = ref2;
	}

	public String getRef3() {
		return ref3;
	}

	public void setRef3(String ref3) {
		this.ref3 = ref3;
	}

	public String getEmciId() {
		return emciId;
	}

	public void setEmciId(String emciId) {
		this.emciId = emciId;
	}

	public String getEmciFree() {
		return emciFree;
	}

	public void setEmciFree(String emciFree) {
		this.emciFree = emciFree;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public String getFee() {
		return fee;
	}

	public void setFee(String fee) {
		this.fee = fee;
	}

	public String getDe11OnlineTMS() {
		return de11OnlineTMS;
	}

	public void setDe11OnlineTMS(String de11OnlineTMS) {
		this.de11OnlineTMS = de11OnlineTMS;
	}
}