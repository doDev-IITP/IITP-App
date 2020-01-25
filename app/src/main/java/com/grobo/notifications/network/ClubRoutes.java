package com.grobo.notifications.network;

import com.grobo.notifications.clubs.ClubItem;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ClubRoutes {

    @GET("/clubs/v2")
    Call<List<ClubItem>> getAllClubs(@Header("Authorization") String credentials);

    @POST("/clubs")
    Call<ResponseBody> addClub(@Body RequestBody body);

    @GET("/clubs/v2/{id}")
    Call<ClubItem> getClubById(@Header("Authorization") String credentials, @Path("id") String clubId);

    @DELETE("/clubs/{id}")
    void deleteClubById(@Header("Authorization") String credentials, @Path("id") String clubId);

}
