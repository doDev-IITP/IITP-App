package com.grobo.notifications.network;

import com.grobo.notifications.admin.clubevents.ClubEventItem;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface EventsRoutes {
    @GET("/events")
    Call<ClubEventItem.ClubEventSuper> getAllEvents(@Header("Authorization") String credentials);

    @GET("/events/date/{from}/{to}")
    Call<List<ClubEventItem>> getEventsByDate(@Header("Authorization") String credentials, @Path("from") long from, @Path("to") long to);

    @POST("/events")
    Call<ResponseBody> postEvent(@Header("Authorization") String credentials, @Body RequestBody body);

    @GET("/events/club/{clubId}")
    Call<ClubEventItem.ClubEventSuper> getEventsByClub(@Header("Authorization") String credentials, @Path("clubId") String id);

    @GET("/events/{id}")
    Call<ClubEventItem> getEventById(@Header("Authorization") String credentials, @Path("id") String id);

    @PATCH("/events/{id}")
    Call<ResponseBody> patchEventById(@Header("Authorization") String credentials, @Body RequestBody requestBody, @Path("id") String id);
}
