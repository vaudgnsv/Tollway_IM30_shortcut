package org.centerm.Tollway.database;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class BatchDetailTemp extends RealmObject {
    @PrimaryKey
    private int id = 0;
    private String appid;
    private String tid;
    private String mid;
    private String traceNo;
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
    private String apprvCode;
    private String transType;
    private String respCode;
    private Boolean voidFlag;
    private Boolean closeFlag;
    private Boolean transStat;
}