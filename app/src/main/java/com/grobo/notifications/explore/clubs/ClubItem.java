package com.grobo.notifications.explore.clubs;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "clubs")
public class ClubItem {

    @PrimaryKey
    private int id;
    private String name;
    private String bio;
    private String description;
    private List<String> coordinators;
    private List<String> subCoordinators;
    private String website;
    private boolean followed = false;
    private List<String> events;
    private List<String> pages;
    private String image;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public List<String> getCoordinators() {
        return coordinators;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<String> getEvents() {
        return events;
    }

    public List<String> getPages() {
        return pages;
    }

    public List<String> getSubCoordinators() {
        return subCoordinators;
    }

    public boolean isFollowed() {
        return followed;
    }

    public String getBio() {
        return bio;
    }

    public String getWebsite() {
        return website;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setEvents(List<String> events) {
        this.events = events;
    }

    public void setFollowed(boolean followed) {
        this.followed = followed;
    }

    public void setPages(List<String> pages) {
        this.pages = pages;
    }

    public void setSubCoordinators(List<String> subCoordinators) {
        this.subCoordinators = subCoordinators;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setCoordinators(List<String> coordinators) {
        this.coordinators = coordinators;
    }
}
