package org.centerm.Tollway.database;

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
	private String emvAppLabel;
	private String emvAid;
	private String emvTc;
	private String emvNameCardHolder;
	private String taxAbb;

	/**
	 * HealthCare
	 */

	private String idCard;
	private String thName;
	private String engFName;
	private String engLName;
	private String engBirth;
	private String thBirth;
	private String address;
	private String engIssue;
	private String thIssue;
	private String engExpire;
	private String thExpire;
	private String religion;
	private String xphoto;
	private String conditionCode;
	private String typeSale;

	private String de63Sale;
	// Paul_20180724_OFF Start
	private String de2;
	private String de3;
	private String de4;
	private String de11;
	private String de22;
	private String de24;
	private String de25;
	private String de35;
	private String de41;
	private String de42;
	private String de52;
	private String de62;
	private String de63;
	private String ghcoffFlg;
	private String Reseve1;
	private String Reseve2;
// Paul_20180724_OFF End

	private String CardTypeHolder;	// Paul_20181202

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

	public String getEmvAppLabel() {
		return emvAppLabel;
	}

	public void setEmvAppLabel(String emvAppLabel) {
		this.emvAppLabel = emvAppLabel;
	}

	public String getEmvAid() {
		return emvAid;
	}

	public void setEmvAid(String emvAid) {
		this.emvAid = emvAid;
	}

	public String getEmvTc() {
		return emvTc;
	}

	public void setEmvTc(String emvTc) {
		this.emvTc = emvTc;
	}

	public String getEmvNameCardHolder() {
		return emvNameCardHolder;
	}

	public void setEmvNameCardHolder(String emvNameCardHolder) {
		this.emvNameCardHolder = emvNameCardHolder;
	}

	public String getTaxAbb() {
		return taxAbb;
	}

	public void setTaxAbb(String taxAbb) {
		this.taxAbb = taxAbb;
	}

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	public String getThName() {
		return thName;
	}

	public void setThName(String thName) {
		this.thName = thName;
	}

	public String getEngFName() {
		return engFName;
	}

	public void setEngFName(String engFName) {
		this.engFName = engFName;
	}

	public String getEngLName() {
		return engLName;
	}

	public void setEngLName(String engLName) {
		this.engLName = engLName;
	}

	public String getEngBirth() {
		return engBirth;
	}

	public void setEngBirth(String engBirth) {
		this.engBirth = engBirth;
	}

	public String getThBirth() {
		return thBirth;
	}

	public void setThBirth(String thBirth) {
		this.thBirth = thBirth;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEngIssue() {
		return engIssue;
	}

	public void setEngIssue(String engIssue) {
		this.engIssue = engIssue;
	}

	public String getThIssue() {
		return thIssue;
	}

	public void setThIssue(String thIssue) {
		this.thIssue = thIssue;
	}

	public String getEngExpire() {
		return engExpire;
	}

	public void setEngExpire(String engExpire) {
		this.engExpire = engExpire;
	}

	public String getThExpire() {
		return thExpire;
	}

	public void setThExpire(String thExpire) {
		this.thExpire = thExpire;
	}

	public String getReligion() {
		return religion;
	}

	public void setReligion(String religion) {
		this.religion = religion;
	}

	public String getXphoto() {
		return xphoto;
	}

	public void setXphoto(String xphoto) {
		this.xphoto = xphoto;
	}

	public String getTypeSale() {
		return typeSale;
	}

	public void setTypeSale(String typeSale) {
		this.typeSale = typeSale;
	}

	public String getDe63Sale() {
		return de63Sale;
	}

	public void setDe63Sale(String de63Sale) {
		this.de63Sale = de63Sale;
	}

	public String getConditionCode() {
		return conditionCode;
	}

	public void setConditionCode(String conditionCode) {
		this.conditionCode = conditionCode;
	}
	// Paul_20180724_OFF Start
	public String getDe2() {
		return de2;
	}

	public void setDe2(String de2) {
		this.de2 = de2;
	}

	public String getDe3() {
		return de3;
	}

	public void setDe3(String de3) {
		this.de3 = de3;
	}

	public String getDe4() {
		return de4;
	}

	public void setDe4(String de4) {
		this.de4 = de4;
	}

	public String getDe11() {
		return de11;
	}

	public void setDe11(String de11) {
		this.de11 = de11;
	}

	public String getDe22() {
		return de22;
	}

	public void setDe22(String de22) {
		this.de22 = de22;
	}

	public String getDe24() {
		return de24;
	}

	public void setDe24(String de24) {
		this.de24 = de24;
	}

	public String getDe25() {
		return de25;
	}

	public void setDe25(String de25) {
		this.de25 = de25;
	}

	public String getDe35() {
		return de35;
	}

	public void setDe35(String de35) {
		this.de35 = de35;
	}

	public String getDe41() {
		return de41;
	}

	public void setDe41(String de41) {
		this.de41 = de41;
	}

	public String getDe42() {
		return de42;
	}

	public void setDe42(String de42) {
		this.de42 = de42;
	}

	public String getDe52() {
		return de52;
	}

	public void setDe52(String de52) {
		this.de52 = de52;
	}

	public String getDe62() {
		return de62;
	}

	public void setDe62(String de62) {
		this.de62 = de62;
	}

	public String getDe63() {
		return de63;
	}

	public void setDe63(String de63) {
		this.de63 = de63;
	}

	public String getGhcoffFlg() {
		return ghcoffFlg;
	}

	public void setGhcoffFlg(String ghcoffFlg) {
		this.ghcoffFlg = ghcoffFlg;
	}

	public String getReseve1() {
		return Reseve1;
	}

	public void setReseve1(String Reseve1) {
		this.Reseve1 = Reseve1;
	}
	public String getReseve2() {
		return Reseve2;
	}

	public void setReseve2(String Reseve2) {
		this.Reseve2 = Reseve2;
	}

	// Paul_20181202
	public String getCardTypeHolder() {
		return CardTypeHolder;
	}

	public void setCardTypeHolderd(String CardTypeHolder) {
		this.CardTypeHolder = CardTypeHolder;
	}


// Paul_20180724_OFF End

}