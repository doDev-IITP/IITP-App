package com.grobo.notifications.network;

import com.grobo.notifications.clubs.ClubItem;
import com.grobo.notifications.feed.FeedItem;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ClubRoutes {

    @GET("/clubs")
    Call<ClubItem.Clubs> getAllClubs();

    @POST("/clubs")
    Call<Void> addClub(@Body RequestBody body);

    @GET("/clubs/{id}")
    Call<FeedItem> getClubById(@Path("id") String clubId);

    @DELETE("/feeds/{id}")
    void deleteClubById(@Header("Authorization") String credentials, @Path("id") String clubId);

}
