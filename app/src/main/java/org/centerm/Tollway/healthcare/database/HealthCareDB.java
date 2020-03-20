package org.centerm.Tollway.healthcare.database;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class HealthCareDB extends RealmObject {
	@PrimaryKey
    private int id = 0;
	private String appid; // ApplicationID 6 length
	private String tid; // TerminalID 8 length
	private String mid; // MerchantID 15 length
	private String traceNo; //Trace number 6 length
	private String date;
	private String time;
	private String amount;
	private String batch;
	private String invoice;
	private String comCode;
	private String cardNumber;

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

	private String apprCode;
	private String refNo;

	private String conditionCode;
	private String typeSale;

	private String de63Sale;
	private String statusVoid; // N = NO VOID  || Y = VOID

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

	public String getInvoice() {
		return invoice;
	}

	public void setInvoice(String invoice) {
		this.invoice = invoice;
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

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getBatch() {
		return batch;
	}

	public String getConditionCode() {
		return conditionCode;
	}

	public void setConditionCode(String conditionCode) {
		this.conditionCode = conditionCode;
	}

	public String getDe63Sale() {
		return de63Sale;
	}

	public void setDe63Sale(String de63Sale) {
		this.de63Sale = de63Sale;
	}

	public void setBatch(String batch) {
		this.batch = batch;
	}

	public String getComCode() {
		return comCode;
	}

	public void setComCode(String comCode) {
		this.comCode = comCode;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
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

	public String getApprCode() {
		return apprCode;
	}

	public void setApprCode(String apprCode) {
		this.apprCode = apprCode;
	}

	public String getRefNo() {
		return refNo;
	}

	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}

	public String getTypeSale() {
		return typeSale;
	}

	public void setTypeSale(String typeSale) {
		this.typeSale = typeSale;
	}

	public String getStatusVoid() {
		return statusVoid;
	}

	public void setStatusVoid(String statusVoid) {
		this.statusVoid = statusVoid;
	}
}