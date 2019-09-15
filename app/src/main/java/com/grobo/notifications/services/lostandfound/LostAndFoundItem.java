package com.grobo.notifications.services.lostandfound;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.grobo.notifications.feed.FeedPoster;

import java.util.List;

@Keep
public class LostAndFoundItem {

    @Keep
    public class LostNFoundSuper {

        @SerializedName("lostnfounds")
        @Expose
        private List<LostAndFoundItem> lostnfounds = null;

        public List<LostAndFoundItem> getLostnfounds() {
            return lostnfounds;
        }

        public void setLostnfounds(List<LostAndFoundItem> lostnfounds) {
            this.lostnfounds = lostnfounds;
        }

    }

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("lostStatus")
    @Expose
    private int lostStatus;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("place")
    @Expose
    private String place;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("contact")
    @Expose
    private String contact;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("lostnfoundPoster")
    @Expose
    private FeedPoster lostnfoundPoster;
    @SerializedName("image")
    @Expose
    private String image;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getLostStatus() {
        return lostStatus;
    }

    public void setLostStatus(Integer lostStatus) {
        this.lostStatus = lostStatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public FeedPoster getLostnfoundPoster() {
        return lostnfoundPoster;
    }

    public void setLostnfoundPoster(FeedPoster lostnfoundPoster) {
        this.lostnfoundPoster = lostnfoundPoster;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setLostStatus(int lostStatus) {
        this.lostStatus = lostStatus;
    }
}
