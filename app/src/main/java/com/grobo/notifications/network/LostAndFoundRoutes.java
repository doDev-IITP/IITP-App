package com.grobo.notifications.network;

import com.grobo.notifications.services.lostandfound.LostAndFoundItem;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface LostAndFoundRoutes {

    @GET("/lostnfounds")
    Call<LostAndFoundItem.LostNFoundSuper> getAllLostNFound(@Header("Authorization") String credentials);

    @POST("/lostnfounds")
    Call<ResponseBody> postLostNFound(@Header("Authorization") String credentials, @Body RequestBody body);

    @GET("/lostnfounds/user")
    Call<LostAndFoundItem.LostNFoundSuper> getLostNFoundByUser(@Header("Authorization") String credentials);

    @GET("/lostnfounds/{id}")
    Call<LostAndFoundItem> getLostNFoundById(@Header("Authorization") String credentials, @Path("id") String id);

    @PATCH("/lostnfounds/{id}")
    Call<LostAndFoundItem> patchLostNFoundById(@Header("Authorization") String credentials, @Body RequestBody requestBody, @Path("id") String id);

}
