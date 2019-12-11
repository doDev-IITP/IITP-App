package com.grobo.notifications.network;

import com.grobo.notifications.Mess.MessModel;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MessRoutes {

    @GET("/mess/{studentMongoId}")
    Call<MessModel> getMessData(@Path("studentMongoId") String studentMongoId);

    @POST("/mess/")
    Call<ResponseBody> selectMess(@Header("Authorization") String credentials, @Body RequestBody body);

}
