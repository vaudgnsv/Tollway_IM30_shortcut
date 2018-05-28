package org.centerm.land.database;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Aid extends RealmObject {
    @PrimaryKey
    private int id = 0;
    private String aid;
    private String appLabel;
    private String appPreferredName;
    private String appPriority;
    private int termFloorLimit;
    private String TACDefault;
    private String TACDenial;
    private String TACOnline;
    private int targetPercentage;
    private int thresholdValue;
    private int maxTargetPercentage;
    private String acquirerId;
    private String mcc;
    private String mid;
    private String appVersionNumber;
    private String posEntryMode;
    private String transReferCurrencyCode;
    private String transReferCurrencyExponent;
    private String defaultDDOL;
    private String defaultTDOL;
    private String supportOnlinePin;
    private Boolean needCompleteMatching;
    private int contactlessLimit;
    private int contactlessFloorLimit;
    private int cvmLimit ;
    private int ECTermTransLimit;
}