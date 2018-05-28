package org.centerm.land.model;

import java.io.Serializable;

public class Card implements Serializable {

    private String no;
    private String expireDate;
    private String serviceCode;

    public Card(String no) {
        this.no = no;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    @Override
    public String toString() {
        return "No : " + no + ", expire date : " + expireDate + ", service code : " + serviceCode;
    }
}
