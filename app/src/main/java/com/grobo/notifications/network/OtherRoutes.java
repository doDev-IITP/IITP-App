package com.grobo.notifications.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface OtherRoutes {

    @GET("/pors")
    Call<ResponseBody> getAllPORs(@Header("Authorization") String credentials);

    @GET("/pors/club/{clubId}")
    Call<ResponseBody> getPorByClub(@Header("Authorization") String credentials, @Path("clubId") String id);

    @GET("/pors/user/{userId}")
    Call<ResponseBody> getPorByUser(@Header("Authorization") String credentials, @Path("userId") String id);

    @GET("/pors/{porId}")
    Call<ResponseBody> getPorById(@Header("Authorization") String credentials, @Path("porId") String id);

}
