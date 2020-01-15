package com.grobo.notifications.network;

import com.grobo.notifications.database.Person;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface UserRoutes {

    //users
    @GET("/users")
    Call<List<Person>> getAllUsers(@Header("Authorization") String credentials);

    @POST("/users/signIn")
    Call<Person> login(@Body RequestBody body);

    @POST("/users/signUp")
    Call<Person> register(@Body RequestBody body);

    @POST("/users/activate")
    Call<Person> verifyOtp(@Body RequestBody body);

    @POST("/users/forgotpwd")
    Call<ResponseBody> forgotPassword(@Body RequestBody body);

    @POST("/users/resetpwd")
    Call<ResponseBody> resetPassword(@Body RequestBody body);

//    @GET("/users/batch/{year}")
//    Call<List<Person>> getUsersByBatch(@Header("Authorization") String credentials, @Path("year") String batch);
//
//    @GET("/users/branch/{br}")
//    Call<List<Person>> getUsersByBranch(@Header("Authorization") String credentials, @Path("br") String branch);
//
//    @GET("/users/batchAndBranch/{year}/{br}")
//    Call<List<Person>> getUsersByBatchNBranch(@Header("Authorization") String credentials, @Path("year") String batch, @Path("br") String branch);
//
//    @GET("/users/instituteId/{id}")
//    Call<Person> getUserByInstituteId(@Header("Authorization") String credentials, @Path("id") String instituteId);

}