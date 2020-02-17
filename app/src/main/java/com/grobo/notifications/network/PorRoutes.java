package com.grobo.notifications.network;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PorRoutes {

    @GET("/pors")
    Call<ResponseBody> getAllPORs(@Header("Authorization") String credentials);

    @POST("/pors")
    Call<Void> claimPor(@Header("Authorization") String credentials, @Body RequestBody body);

    @GET("/pors/club/{clubId}")
    Call<ResponseBody> getPorByClub(@Header("Authorization") String credentials, @Path("clubId") String id);

    @GET("/pors/user/{userId}")
    Call<ResponseBody> getPorByUser(@Header("Authorization") String credentials, @Path("userId") String id);

    @GET("/pors/{porId}")
    Call<ResponseBody> getPorById(@Header("Authorization") String credentials, @Path("porId") String id);



}
