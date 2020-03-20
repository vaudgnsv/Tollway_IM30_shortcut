package org.centerm.Tollway.database;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ReversalHealthCare extends RealmObject {
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

    public String getDe52() {
        return de52;
    }

    public void setDe52(String de52) {
        this.de52 = de52;
    }

    public void setDe42(String de42) {
        this.de42 = de42;
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
}