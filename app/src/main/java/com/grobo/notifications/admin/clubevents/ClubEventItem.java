package com.grobo.notifications.admin.clubevents;

import androidx.annotation.NonNull;

public class ClubEventItem {

    @NonNull
    private String id = "new_feed";

    private long date;
    private String venue;
    private String name;
    private String description;
    private String imageUrl;

    private boolean interested = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getVenue() {
        return venue;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setInterested(boolean interested) {
        this.interested = interested;
    }

    public boolean isInterested() {
        return interested;
    }
}
