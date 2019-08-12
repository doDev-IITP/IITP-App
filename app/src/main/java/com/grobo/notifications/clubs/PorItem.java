package com.grobo.notifications.clubs;

public class PorItem {
    private String image;
    private String name;
    private String position;
    private String userId;
    private String instituteId;
    private String clubId;
    private String porId;
    private int access;

    public PorItem(){}

    public PorItem(String name, String image, String position) {
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

    public int getAccess() {
        return access;
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

    public void setAccess(int access) {
        this.access = access;
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
}
