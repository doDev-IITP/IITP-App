package com.grobo.notifications.services.lostandfound;

public class LostAndFoundItem {

    public static final int ITEM_LOST = 1;
    public static final int ITEM_FOUND = 2;
    public static final int ITEM_RECOVERED = 3;

    private int id;
    private int lostStatus;      //can take 1,2 and 3 from above
    private String name;
    private String place;       //where lost/found
    private String time;
    private String date;        //when lost/found
    private String description;
    private String contact;     //of person who is adding this
    private String address;     //where item can be recovered

    public int getLostStatus() {
        return lostStatus;
    }

    public String getAddress() {
        return address;
    }

    public String getContact() {
        return contact;
    }

    public String getDate() {
        return date;
    }

    public String getPlace() {
        return place;
    }

    public String getTime() {
        return time;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setLostStatus(int lostStatus) {
        this.lostStatus = lostStatus;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
