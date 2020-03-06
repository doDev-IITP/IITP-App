package com.grobo.notifications.network;

import com.grobo.notifications.feed.DataPoster;
import com.grobo.notifications.feed.FeedItems;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface FeedRoutes {

    //feeds
    @GET("/feeds")
    Call<FeedItems> getAllFeeds();

    @POST("/feeds")
    Call<ResponseBody> postFeed(@Header("Authorization") String credentials, @Body RequestBody body);

    @GET("/feeds/{id}")
    Call<ResponseBody> getFeedById(@Header("Authorization") String credentials, @Path("id") String id);

    @PATCH("/feeds/{id}")
    Call<ResponseBody> editFeedById(@Header("Authorization") String credentials, @Path("id") String id, @Body RequestBody body);

    @DELETE("/feeds/{id}")
    void deleteFeedByEventId(@Header("Authorization") String credentials, @Path("id") long eventId);

    //for new feed added
    @GET("/feeds/latestFeed/{timestamp}")
    Call<FeedItems> getNewFeed(@Path("timestamp") long eventId);

    @POST("/feeds/react/{feedId}")
    Call<ResponseBody> reactOnFeed(@Header("Authorization") String credentials, @Path("feedId") String feedId);

    @GET("/feeds/react/{feedId}")
    Call<List<DataPoster>> getFeedReacts(@Header("Authorization") String credentials, @Path("feedId") String feedId);
}
