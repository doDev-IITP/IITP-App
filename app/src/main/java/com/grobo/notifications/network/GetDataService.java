package com.grobo.notifications.network;

import com.grobo.notifications.database.Person;
import com.grobo.notifications.explore.services.lostandfound.LostAndFoundItem;
import com.grobo.notifications.feed.FeedItem;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface GetDataService {


    //users

    @GET("/users")
    Call<List<Person>> getAllUsers(@Header("Authorization") String credentials);

    @POST("/users/signIn")
    Call<Person> login(@Body RequestBody params);

    @POST("/users/signUp")
    Call<Person> register(@Body RequestBody params);

    @GET("/users/batch/{year}")
    Call<List<Person>> getUsersByBatch(@Header("Authorization") String credentials, @Path("year") String batch);

    @GET("/users/branch/{br}")
    Call<List<Person>> getUsersByBranch(@Header("Authorization") String credentials, @Path("br") String branch);

    @GET("/users/batchAndBranch/{year}/{br}")
    Call<List<Person>> getUsersByBatchNBranch(@Header("Authorization") String credentials, @Path("year") String batch, @Path("br") String branch);

    @GET("/users/instituteId/{id}")
    Call<Person> getUserByInstituteId (@Header("Authorization") String credentials, @Path("id") String instituteId);


    //feeds
    @GET("/feeds")
    Call<List<FeedItem>> getAllFeeds (@Header("Authorization") String credentials);

    @Headers("Content-Type: application/json")
    @POST("/feeds")
    Call<FeedItem> postFeed (@Header("Authorization") String credentials, @Body String rawJsonString);

    @GET("/feeds/{id}")
    Call<FeedItem> getFeedByEventId (@Header("Authorization") String credentials, @Path("id") long eventId);

    @DELETE("/feeds/{id}")
    void deleteFeedByEventId (@Header("Authorization") String credentials, @Path("id") long eventId);

    //for new feed added
    @GET("/feeds/latestFeed/{timestamp}")
    Call<FeedItem.FeedItemSuper> getNewFeed (@Header("Authorization") String credentials, @Path("timestamp") long eventId);

    //for later events
    @GET("/feeds/timestamp/{timestamp}")
    Call<List<FeedItem>> getNewEvents (@Header("Authorization") String credentials, @Path("timestamp") long eventDate);


    //lostnfounds
    @GET("/lostnfounds")
    Call<List<LostAndFoundItem>> getAllLostNFound (@Header("Authorization") String credentials);

    @Headers("Content-Type: application/json")
    @POST("/feeds")
    Call<LostAndFoundItem> postLostNFound (@Header("Authorization") String credentials, @Body String rawJsonString);

    @GET("/feeds/{id}")
    Call<LostAndFoundItem> getLostNFoundById (@Header("Authorization") String credentials, @Path("id") int id);

    @DELETE("/feeds/{id}")
    void deleteLostNFoundById (@Header("Authorization") String credentials, @Path("id") int id);

    @Headers("Content-Type: application/json")
    @PATCH("/feeds/{id}")
    Call<LostAndFoundItem> patchLostNFound (@Header("Authorization") String credentials, @Body String rawJsonString, @Path("id") int id);


}

//public interface GitHubService {
//    @GET("users/{user}/repos")
//    Call<List<Repo>> listRepos(@Path("user") String user);
//    @GET("group/{id}/users")
//    Call<List<User>> groupList(@Path("id") int groupId);
//}



//public interface ServiceApi
//{
//    @POST("prefix/user/{login}")
//    Call<ResponseBody> login(@Path("login") String postfix, @Body RequestBody params);
//}
//    In your calling point create a RequestBody, stating it's MediaType, and using JSONObject to convert your Map to the proper format:
//
//        Map<String, Object> jsonParams = new ArrayMap<>();
////put something inside the map, could be null
//        jsonParams.put("code", some_code);
//
//        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),(new JSONObject(jsonParams)).toString());
////serviceCaller is the interface initialized with retrofit.create...
//        Call<ResponseBody> response = serviceCaller.login("loginpostfix", body);
//
//        response.enqueue(new Callback<ResponseBody>()
//        {
//@Override
//public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> rawResponse)
//        {
//        try
//        {
//        //get your response....
//        Log.d(TAG, "RetroFit2.0 :RetroGetLogin: " + rawResponse.body().string());
//        }
//        catch (Exception e)
//        {
//        e.printStackTrace();
//        }
//        }
//
//@Override
//public void onFailure(Call<ResponseBody> call, Throwable throwable)
//        {
//        // other stuff...
//        }
//        });