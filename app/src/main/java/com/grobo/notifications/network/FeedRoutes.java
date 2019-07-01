package com.grobo.notifications.network;

import com.grobo.notifications.feed.FeedItem;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface FeedRoutes {


    //feeds
    @GET("/feeds")
    Call<List<FeedItem>> getAllFeeds(@Header("Authorization") String credentials);

    @POST("/feeds")
    Call<Void> postFeed(@Header("Authorization") String credentials, @Body RequestBody body);

    @GET("/feeds/{id}")
    Call<FeedItem> getFeedByEventId(@Header("Authorization") String credentials, @Path("id") long eventId);

    @DELETE("/feeds/{id}")
    void deleteFeedByEventId(@Header("Authorization") String credentials, @Path("id") long eventId);

    //for new feed added
    @GET("/feeds/latestFeed/{timestamp}")
    Call<FeedItem.FeedItemSuper1> getNewFeed(@Header("Authorization") String credentials, @Path("timestamp") long eventId);

    //for later events
    @GET("/feeds/timestamp/{timestamp}")
    Call<List<FeedItem>> getNewEvents(@Header("Authorization") String credentials, @Path("timestamp") long eventDate);



}
