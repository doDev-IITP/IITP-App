package com.grobo.notifications.feed;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@Keep
public class FeedItems {

    @SerializedName("feeds")
    @Expose
    private List<FeedItem> feeds = null;

    public List<FeedItem> getFeeds() {
        return feeds;
    }

    public void setFeeds(List<FeedItem> feeds) {
        this.feeds = feeds;
    }
}