package org.centerm.land.database;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Capk extends RealmObject {
    @PrimaryKey
    private int id = 0;
    private String rid;
    private String capki;
    private String hashIndex;
    private String arithIndex;
    private String modul;
    private String exponent;
    private String checkSum;
    private String expiry;
}