package org.centerm.land.database;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class QrCode extends RealmObject {

    @PrimaryKey
    private int id;

    private String aid;
    private String qrTid;
    private String billerId;
    private String trace;
    private String date;
    private String time;
    private String comCode;
    private String ref1;
    private String ref2;
    private String amount;
    private String header1;
    private String header2;
    private String header3;
    private String nameCompany;
    private String textQrGenerateAll;
    private String statusPrint; // 0 ยังไม่ได้ปริ้น 1 คือ ปรั้นแล้ว
    private String statusSuccess;// 0 ยังไม่สำเร็จ 1 คือ สำเร็จชำระเงินแล้ว

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
}
