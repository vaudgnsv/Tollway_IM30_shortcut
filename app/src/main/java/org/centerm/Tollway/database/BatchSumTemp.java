package org.centerm.Tollway.database;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class BatchSumTemp extends RealmObject {
    @PrimaryKey
    private int id = 0;
    private String appid;
    private String tid;
    private String mid;

    private String batchNo;
    private String batchDate;
    private String batchTime;
    private String batchAmount;
    private String totalCount;

    private String refNo;
    private String transType;
    private String respCode;
}