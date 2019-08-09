package com.grobo.notifications.network;

import com.grobo.notifications.admin.clubevents.ClubEventItem;

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

    @POST("/events")
    Call<ResponseBody> postEvent(@Header("Authorization") String credentials, @Body RequestBody body);

    @GET("/events/club/")
    Call<ClubEventItem.ClubEventSuper> getEventByClub(@Header("Authorization") String credentials);

    @GET("/events/{id}")
    Call<ClubEventItem> getEventById(@Header("Authorization") String credentials, @Path("id") String id);

    @PATCH("/events/{id}")
    Call<ResponseBody> patchEventById(@Header("Authorization") String credentials, @Body RequestBody requestBody, @Path("id") String id);

}
