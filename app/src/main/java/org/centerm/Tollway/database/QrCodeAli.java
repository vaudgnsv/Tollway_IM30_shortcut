package org.centerm.Tollway.database;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class QrCodeAli extends RealmObject {

    @PrimaryKey
    private int id;

    private String aid;
    private String qrTid;
    private String billerId;
    private String trace;
    private String date;
    private String time;
    private String amount;
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

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
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
