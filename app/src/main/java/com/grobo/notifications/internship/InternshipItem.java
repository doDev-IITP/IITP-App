package com.grobo.notifications.internship;

public class InternshipItem {

    private String companyName;
    private String duration;
    private String stipend;
    private String imageUrl;
    private long lastDate;
    private String details;

    public InternshipItem(String companyName,String duration,String stipend,String imageUrl,long lastDate,String details)
    {
        this.companyName=companyName;
        this.details=details;
        this.duration=duration;
        this.imageUrl=imageUrl;
        this.lastDate=lastDate;
        this.stipend=stipend;
    }
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getStipend() {
        return stipend;
    }

    public void setStipend(String stipend) {
        this.stipend = stipend;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public long getLastDate() {
        return lastDate;
    }

    public void setLastDate(long lastDate) {
        this.lastDate = lastDate;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
