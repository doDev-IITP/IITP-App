package com.grobo.notifications.clubs;

import androidx.annotation.Keep;

import java.util.List;

@Keep
public class ClubPorItem {
    private String image;
    private String name;
    private String position;
    private String userId;
    private String instituteId;
    private String clubId;
    private String porId;
    private int code;
    private List<Integer> access;

    public ClubPorItem(){}

    public ClubPorItem(String name, String image, String position) {
        this.image = image;
        this.name = name;
        this.position = position;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setInstituteId(String instituteId) {
        this.instituteId = instituteId;
    }

    public String getInstituteId() {
        return instituteId;
    }

    public String getClubId() {
        return clubId;
    }

    public String getPorId() {
        return porId;
    }

    public String getUserId() {
        return userId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

    public void setPorId(String porId) {
        this.porId = porId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<Integer> getAccess() {
        return access;
    }

    public void setAccess(List<Integer> access) {
        this.access = access;
    }
}
