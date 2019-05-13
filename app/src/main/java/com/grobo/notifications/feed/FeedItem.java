package com.grobo.notifications.feed;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@Entity(tableName = "feed")
public class FeedItem {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @SerializedName("eventId")
    @Expose
    private long eventId;
    @SerializedName("guests")
    @Expose
    private List<String> guests = null;
    @SerializedName("coordinators")
    @Expose
    private List<String> coordinators = null;
    @SerializedName("postLinks")
    @Expose
    private List<String> postLinks = null;
    @SerializedName("feedPoster")
    @Expose
    private String feedPoster;
    @SerializedName("eventDate")
    @Expose
    private long eventDate;
    @SerializedName("eventVenue")
    @Expose
    private String eventVenue;
    @SerializedName("eventName")
    @Expose
    private String eventName;
    @SerializedName("eventDescription")
    @Expose
    private String eventDescription;
    @SerializedName("eventImageUrl")
    @Expose
    private String eventImageUrl;

    private boolean interested = false;

    public void setFeedPoster(String feedPoster) {
        this.feedPoster = feedPoster;
    }

    public String getFeedPoster() {
        return feedPoster;
    }

    public void setGuests(List<String> guests) {
        this.guests = guests;
    }

    public void setPostLinks(List<String> postLinks) {
        this.postLinks = postLinks;
    }

    public void setCoordinators(List<String> coordinators) {
        this.coordinators = coordinators;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public boolean isInterested() {
        return interested;
    }

    public void setInterested(boolean interested) {
        this.interested = interested;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public String getEventImageUrl() {
        return eventImageUrl;
    }

    public String getEventVenue() {
        return eventVenue;
    }

    public void setEventImageUrl(String eventImageUrl) {
        this.eventImageUrl = eventImageUrl;
    }

    public void setEventVenue(String eventVenue) {
        this.eventVenue = eventVenue;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setEventDate(long eventDate) {
        this.eventDate = eventDate;
    }

    public long getEventDate() {
        return eventDate;
    }

    public List<String> getCoordinators() {
        return coordinators;
    }

    public List<String> getGuests() {
        return guests;
    }

    public List<String> getPostLinks() {
        return postLinks;
    }

    public class FeedItemSuper {

        @SerializedName("latestFeeds")
        @Expose
        private List<FeedItem> latestFeeds = null;

        public List<FeedItem> getLatestFeeds() {
            return latestFeeds;
        }

        public void setLatestFeeds(List<FeedItem> latestFeeds) {
            this.latestFeeds = latestFeeds;
        }
    }
}
