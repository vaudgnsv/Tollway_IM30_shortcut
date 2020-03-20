package org.centerm.Tollway.healthcare.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class CardId implements Parcelable {
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
    private Bitmap xphoto;

    public CardId() {
    }

    protected CardId(Parcel in) {
        idCard = in.readString();
        thName = in.readString();
        engFName = in.readString();
        engLName = in.readString();
        engBirth = in.readString();
        thBirth = in.readString();
        address = in.readString();
        engIssue = in.readString();
        thIssue = in.readString();
        engExpire = in.readString();
        thExpire = in.readString();
        religion = in.readString();
        xphoto = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<CardId> CREATOR = new Creator<CardId>() {
        @Override
        public CardId createFromParcel(Parcel in) {
            return new CardId(in);
        }

        @Override
        public CardId[] newArray(int size) {
            return new CardId[size];
        }
    };

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

    public Bitmap getXphoto() {
        return xphoto;
    }

    public void setXphoto(Bitmap xphoto) {
        this.xphoto = xphoto;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(idCard);
        dest.writeString(thName);
        dest.writeString(engFName);
        dest.writeString(engLName);
        dest.writeString(engBirth);
        dest.writeString(thBirth);
        dest.writeString(address);
        dest.writeString(engIssue);
        dest.writeString(thIssue);
        dest.writeString(engExpire);
        dest.writeString(thExpire);
        dest.writeString(religion);
        dest.writeParcelable(xphoto, flags);
    }
}
