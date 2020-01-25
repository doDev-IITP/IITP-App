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
    @SerializedName("postLinks")
    @Expose
    private List<String> postLinks = null;
    @SerializedName("feedPoster")
    @Expose
    @Embedded(prefix = "user_")
    private DataPoster dataPoster;
    @SerializedName("eventName")
    @Expose
    private String eventName;
    @SerializedName("eventDescription")
    @Expose
    private String eventDescription;
    @SerializedName("eventImageUrl")
    @Expose
    private String eventImageUrl;
    @SerializedName("likes")
    @Expose
    private List<String> likes;

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

    public void setEventImageUrl(String eventImageUrl) {
        this.eventImageUrl = eventImageUrl;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
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

    public List<String> getLikes() {
        return likes;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
    }
}

