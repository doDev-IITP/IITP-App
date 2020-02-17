package com.grobo.notifications.account.por;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;

import java.util.ArrayList;
import java.util.List;

@Keep
public class PORItem implements Parcelable {
    private String id;
    private String clubId;
    private String clubName;
    private int code;
    private String position;
    private List<Integer> access;

    public PORItem() {}

    public PORItem(String id, String clubId, String clubName, int code, String position, List<Integer> access) {
        this.id = id;
        this.clubId = clubId;
        this.clubName = clubName;
        this.code = code;
        this.position = position;
        this.access = access;
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

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public List<Integer> getAccess() {
        return access;
    }

    public void setAccess(List<Integer> access) {
        this.access = access;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    protected PORItem(Parcel in) {
        id = in.readString();
        clubId = in.readString();
        clubName = in.readString();
        code = in.readInt();
        position = in.readString();
        if (in.readByte() == 0x01) {
            access = new ArrayList<Integer>();
            in.readList(access, Integer.class.getClassLoader());
        } else {
            access = null;
        }
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
        dest.writeInt(code);
        dest.writeString(position);
        if (access == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(access);
        }
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