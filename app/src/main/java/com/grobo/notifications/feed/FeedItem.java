package com.grobo.notifications.feed;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@Keep
@Entity(tableName = "feed")
public class FeedItem {

    @PrimaryKey()
    @SerializedName("_id")
    @Expose
    @NonNull
    private String id = "new_feed";

    @SerializedName("eventId")
    @Expose
    private long eventId;
    @SerializedName("coordinators")
    @Expose
    private List<String> coordinators = null;
    @SerializedName("postLinks")
    @Expose
    private List<String> postLinks = null;
    @SerializedName("dataPoster")
    @Expose
    @Embedded(prefix = "user_")
    private DataPoster dataPoster;
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

    public void setDataPoster(DataPoster dataPoster) {
        this.dataPoster = dataPoster;
    }

    public DataPoster getDataPoster() {
        return dataPoster;
    }

    public void setPostLinks(List<String> postLinks) {
        this.postLinks = postLinks;
    }

    public void setCoordinators(List<String> coordinators) {
        this.coordinators = coordinators;
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

    public List<String> getPostLinks() {
        return postLinks;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Keep
    public class FeedItemSuper1 {

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

