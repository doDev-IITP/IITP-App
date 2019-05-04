package com.grobo.notifications.feed;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;

@Entity(tableName = "feed")
public class FeedItem {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private long eventId;
    private String eventName;
    private String eventDescription;

    private long eventDate;
    private String eventVenue;

    private String eventImageUrl;
    private boolean interested = false;

    private ArrayList<String> coordinators;
    private ArrayList<String> guests;
    private ArrayList<String> postLinks;

    @Ignore
    public FeedItem() {
    }

    public FeedItem(long eventId, String eventName, String eventDescription, long eventDate, String eventVenue, String eventImageUrl, boolean interested, ArrayList<String> coordinators, ArrayList<String> guests, ArrayList<String> postLinks) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventDate = eventDate;
        this.eventVenue = eventVenue;
        this.eventImageUrl = eventImageUrl;
        this.interested = interested;
        this.coordinators = coordinators;
        this.guests = guests;
        this.postLinks = postLinks;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getEventDate() {
        return eventDate;
    }

    public void setEventDate(long eventDate) {
        this.eventDate = eventDate;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventName() {
        return eventName;
    }

    public int getId() {
        return id;
    }

    public void setEventVenue(String eventVenue) {
        this.eventVenue = eventVenue;
    }

    public void setEventImageUrl(String eventImageUrl) {
        this.eventImageUrl = eventImageUrl;
    }

    public String getEventVenue() {
        return eventVenue;
    }

    public String getEventImageUrl() {
        return eventImageUrl;
    }

    public ArrayList<String> getCoordinators() {
        return coordinators;
    }

    public ArrayList<String> getGuests() {
        return guests;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setCoordinators(ArrayList<String> coordinators) {
        this.coordinators = coordinators;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public void setGuests(ArrayList<String> guests) {
        this.guests = guests;
    }

    public void setInterested(boolean interested) {
        this.interested = interested;
    }

    public boolean isInterested() {
        return interested;
    }

    public ArrayList<String> getPostLinks() {
        return postLinks;
    }

    public void setPostLinks(ArrayList<String> postLinks) {
        this.postLinks = postLinks;
    }

}
