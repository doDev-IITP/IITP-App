package com.grobo.notifications.admin.clubevents;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@Keep
@Entity(tableName = "events")
public class ClubEventItem {

    @Keep
    public class ClubEventSuper {

        @SerializedName("events")
        @Expose
        private List<ClubEventItem> events = null;

        public List<ClubEventItem> getEvents() {
            return events;
        }

        public void setEvents(List<ClubEventItem> events) {
            this.events = events;
        }

    }

    @SerializedName("coordinators")
    @Expose
    private List<String> coordinators = null;
    @SerializedName("postLinks")
    @Expose
    private List<String> postLinks = null;
    @PrimaryKey
    @SerializedName("_id")
    @Expose
    @NonNull
    private String id = "";
    @SerializedName("relatedClub")
    @Expose
    @Embedded(prefix = "club_")
    private RelatedClub relatedClub;
    @SerializedName("venue")
    @Expose
    private String venue;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("date")
    @Expose
    private long date;
    @SerializedName("imageUrl")
    @Expose
    private String imageUrl;

    public List<String> getCoordinators() {
        return coordinators;
    }

    public void setCoordinators(List<String> coordinators) {
        this.coordinators = coordinators;
    }

    public List<String> getPostLinks() {
        return postLinks;
    }

    public void setPostLinks(List<String> postLinks) {
        this.postLinks = postLinks;
    }

    public @NonNull
    String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public RelatedClub getRelatedClub() {
        return relatedClub;
    }

    public void setRelatedClub(RelatedClub relatedClub) {
        this.relatedClub = relatedClub;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

}
