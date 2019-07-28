package com.grobo.notifications.clubs;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@Entity(tableName = "clubs")
public class ClubItem {

    @PrimaryKey
    @NonNull
    @SerializedName("_id")
    @Expose
    private String id = "new_club";
    @SerializedName("events")
    @Expose
    private List<String> events = null;
    @SerializedName("followers")
    @Expose
    private Integer followers;
    @SerializedName("coordinators")
    @Expose
    private List<String> coordinators = null;
    @SerializedName("subCoordinators")
    @Expose
    private List<String> subCoordinators = null;
    @SerializedName("pages")
    @Expose
    private List<String> pages = null;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("bio")
    @Expose
    private String bio;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("website")
    @Expose
    private String website;
    @SerializedName("image")
    @Expose
    private String image;

    private boolean followed;

    public void setFollowed(boolean followed) {
        this.followed = followed;
    }

    public boolean isFollowed() {
        return followed;
    }

    public List<String> getEvents() {
        return events;
    }

    public void setEvents(List<String> events) {
        this.events = events;
    }

    public Integer getFollowers() {
        return followers;
    }

    public void setFollowers(Integer followers) {
        this.followers = followers;
    }

    public List<String> getCoordinators() {
        return coordinators;
    }

    public void setCoordinators(List<String> coordinators) {
        this.coordinators = coordinators;
    }

    public List<String> getSubCoordinators() {
        return subCoordinators;
    }

    public void setSubCoordinators(List<String> subCoordinators) {
        this.subCoordinators = subCoordinators;
    }

    public List<String> getPages() {
        return pages;
    }

    public void setPages(List<String> pages) {
        this.pages = pages;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public class Clubs {

        @SerializedName("clubs")
        @Expose
        private List<ClubItem> clubs = null;

        public List<ClubItem> getClubs() {
            return clubs;
        }

        public void setClubs(List<ClubItem> clubs) {
            this.clubs = clubs;
        }

    }
}