package com.grobo.notifications.account.por;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;

@Keep
public class PORItem implements Parcelable {
    private String id;
    private String clubId;
    private String clubName;
    private int access;
    private String position;

    public PORItem() {}

    public PORItem(String id, String clubId, String clubName, int access, String position) {
        this.id = id;
        this.clubId = clubId;
        this.clubName = clubName;
        this.access = access;
        this.position = position;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClubId() {
        return clubId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public int getAccess() {
        return access;
    }

    public void setAccess(int access) {
        this.access = access;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    protected PORItem(Parcel in) {
        id = in.readString();
        clubId = in.readString();
        clubName = in.readString();
        access = in.readInt();
        position = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(clubId);
        dest.writeString(clubName);
        dest.writeInt(access);
        dest.writeString(position);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<PORItem> CREATOR = new Parcelable.Creator<PORItem>() {
        @Override
        public PORItem createFromParcel(Parcel in) {
            return new PORItem(in);
        }

        @Override
        public PORItem[] newArray(int size) {
            return new PORItem[size];
        }
    };
}