package org.centerm.Tollway.healthcare.database;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SaleOfflineHealthCare extends RealmObject {
    @PrimaryKey
    private int id = 0;
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
    private String type;

    private String appid; // ApplicationID 6 length
    private String tid; // TerminalID 8 length
    private String mid; // MerchantID 15 length
    private String traceNo; //Trace number 6 length
    private String transDate;
    private String transTime;
    private String amount;
    private String track2;
    private String apprvCode;
    private String refNo;

    private String comCode;
    private String ref1;
    private String ref2;
    private String ref3;
    private String ecr;

    private String conditionCode;
    private String typeSale;

    private String de63Sale;
    private String cardNo;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getTrack2() {
        return track2;
    }

    public void setTrack2(String track2) {
        this.track2 = track2;
    }

    public String getApprvCode() {
        return apprvCode;
    }

    public void setApprvCode(String apprvCode) {
        this.apprvCode = apprvCode;
    }

    public String getRefNo() {
        return refNo;
    }

    public void setRefNo(String refNo) {
        this.refNo = refNo;
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

    public String getEcr() {
        return ecr;
    }

    public void setEcr(String ecr) {
        this.ecr = ecr;
    }

    public String getConditionCode() {
        return conditionCode;
    }

    public void setConditionCode(String conditionCode) {
        this.conditionCode = conditionCode;
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

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
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
}