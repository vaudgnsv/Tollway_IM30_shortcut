package org.centerm.Tollway.database;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class QrCode extends RealmObject {

    @PrimaryKey
    private int id;

    private String aid;
    private String qrTid;
    private String billerId;
    private String trace;       // Alipay invoice
    private String date;
    private String time;
    private String comCode;
    private String ref1;
    private String ref2;
//    private String ref3;
//    private String ref3;
    private String amount;
    private String header1;
    private String header2;
    private String header3;
    private String nameCompany;
    private String textQrGenerateAll;
    private String statusPrint; // 0 ยังไม่ได้ปริ้น 1 คือ ปรั้นแล้ว
    private String statusSuccess;// 0 ยังไม่สำเร็จ 1 คือ สำเร็จชำระเงินแล้ว

    // Paul_20181006 Start QR + Alipay
    private String hostTypeCard;
    private String voidflag;
    //Header
    private String reqBy;
    private String reqChannel;
    private String reqChannelDtm;
    private String reqChannelRefId;
    private String service;

    //Body
    private String amt;
    private String token;
    private String deviceid;
    private String merid;
    private String storeid;

    //Response

    private String respcode;
    private String reqid;
    private String reqdt;
    private String transid;
    private String walletcode;
    private String wallettransid;
    private String transdt;
    private String buyerid;
    private String foramt;
    private String convrate;
    private String walletcurr;
    private String exchrateunit;
    private String canceldt;
    private String cii;

    private String fee;
    private String amtplusfee;
    private String feeType;
    private String feeRate;
    private String merType;
    // Paul_20181006 End

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getQrTid() {
        return qrTid;
    }

    public void setQrTid(String qrTid) {
        this.qrTid = qrTid;
    }

    public String getBillerId() {
        return billerId;
    }

    public void setBillerId(String billerId) {
        this.billerId = billerId;
    }

    public String getTrace() {
        return trace;
    }

    public void setTrace(String trace) {
        this.trace = trace;
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

//    public String getRef3() {
//        return ref3;
//    }
//
//    public void setRef3(String ref3) {
//        this.ref3 = ref3;
//    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getHeader1() {
        return header1;
    }

    public void setHeader1(String header1) {
        this.header1 = header1;
    }

    public String getHeader2() {
        return header2;
    }

    public void setHeader2(String header2) {
        this.header2 = header2;
    }

    public String getHeader3() {
        return header3;
    }

    public void setHeader3(String header3) {
        this.header3 = header3;
    }

    public String getNameCompany() {
        return nameCompany;
    }

    public void setNameCompany(String nameCompany) {
        this.nameCompany = nameCompany;
    }

    public String getTextQrGenerateAll() {
        return textQrGenerateAll;
    }

    public void setTextQrGenerateAll(String textQrGenerateAll) {
        this.textQrGenerateAll = textQrGenerateAll;
    }

    public String getStatusPrint() {
        return statusPrint;
    }

    public void setStatusPrint(String statusPrint) {
        this.statusPrint = statusPrint;
    }

    public String getStatusSuccess() {
        return statusSuccess;
    }

    public void setStatusSuccess(String statusSuccess) {
        this.statusSuccess = statusSuccess;
    }

    // Paul_20181006 Start  QR + Alipay
    public String getHostTypeCard() {
        return hostTypeCard;
    }
    public void setHostTypeCard(String hostTypeCard) {
        this.hostTypeCard = hostTypeCard;
    }
    public void setVoidFlag(String type){
        this.voidflag = type;
    }
    public String getVoidFlag(){
        return voidflag;
    }

    public void setReqBy(String type){
        this.reqBy = type;
    }
    public void setReqChannel(String type){
        this.reqChannel = type;
    }
    public void setReqChannelDtm(String type){
        this.reqChannelDtm = type;
    }
    public void setReqChannelRefId(String type){
        this.reqChannelRefId = type;
    }
    public void setService(String type){
        this.service = type;
    }
    public void setAmt(String type){
        this.amt = type;
    }
    public void setToken(String type){
        this.token = type;
    }
    public void setDeviceId(String type){
        this.deviceid = type;
    }
    public void setMerId(String type){
        this.merid = type;
    }
    public void setStoreId(String type){
        this.storeid = type;
    }
    public void setFee(String type){
        this.fee = type;
    }
    public void setAmtplusfee(String type){
        this.amtplusfee = type;
    }
    public void setFeeType(String type){
        this.feeType = type;
    }
    public void setFeeRate(String type){
        this.feeRate = type;
    }
    public void setMerType(String type){
        this.merType = type;
    }
    public void setRespcode(String type){
        this.respcode = type;
    }
    public void setReqid(String type){
        this.reqid = type;
    }
    public void setReqdt(String type){
        this.reqdt = type;
    }
    public void setTransid(String type){
        this.transid = type;
    }
    public void setTransdt(String type){
        this.transdt = type;
    }
    public void setWalletcode(String type){
        this.walletcode = type;
    }
    public void setWallettransid(String type){
        this.wallettransid = type;
    }
    public void setBuyerid(String type){
        this.buyerid = type;
    }
    public void setForamt(String type){
        this.foramt = type;
    }
    public void setConvrate(String type){
        this.convrate = type;
    }
    public void setWalletcurr(String type){
        this.walletcurr = type;
    }
    public void setExchrateunit(String type){
        this.exchrateunit = type;
    }
    public void setCanceldt(String type){
        this.canceldt = type;
    }
    public void setCii(String type){
        this.cii = type;
    }

    public String getReqBy(){
        return reqBy;
    }
    public String getReqChannel(){
        return reqChannel;
    }
    public String getReqChannelDtm(){
        return reqChannelDtm;
    }
    public String getReqChannelRefId(){
        return reqChannelRefId;
    }
    public String getService(){
        return service;
    }
    public String getAmt(){
        return amt;
    }
    public String getToken(){
        return token;
    }
    public String getDeviceId(){
        return deviceid;
    }
    public String getMerid(){
        return merid;
    }
    public String getStoreId(){
        return storeid;
    }
    public String getFee(){
        return fee;
    }
    public String getAmtplusfee(){
        return amtplusfee;
    }
    public String getFeeType(){
        return feeType;
    }
    public String getFeeRate(){
        return feeRate;
    }
    public String getMerType(){
        return merType;
    }

    public String getRespcode(){
        return respcode;
    }
    public String getReqid(){
        return reqid;
    }
    public String getReqdt(){
        return reqdt;
    }
    public String getTransid(){
        return transid;
    }
    public String getTransdt(){
        return transdt;
    }
    public String getWalletcode(){
        return walletcode;
    }
    public String getWallettransid(){
        return wallettransid;
    }
    public String getBuyerid(){
        return buyerid;
    }
    public String getForamt(){
        return foramt;
    }
    public String getConvrate(){
        return convrate;
    }
    public String getWalletcurr(){
        return walletcurr;
    }
    public String getExchrateunit(){
        return exchrateunit;
    }
    public String getCanceldt(){
        return canceldt;
    }
    public String getCii(){
        return cii;
    }

    // Paul_20181006 End
}
