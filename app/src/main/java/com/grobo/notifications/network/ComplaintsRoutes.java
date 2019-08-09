package com.grobo.notifications.network;

import com.grobo.notifications.services.complaints.ComplaintItem;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ComplaintsRoutes {

    @GET("/complaints")
    Call<ComplaintItem.ComplaintsSuper> getAllComplaints(@Header("Authorization") String credentials);

    @POST("/complaints")
    Call<ResponseBody> postComplaint(@Header("Authorization") String credentials, @Body RequestBody body);

    @GET("/complaints/user")
    Call<ComplaintItem.ComplaintsSuper> getComplaintsByUser(@Header("Authorization") String credentials);

    @GET("/complaints/{id}")
    Call<ComplaintItem> getComplaintById(@Header("Authorization") String credentials, @Path("id") String id);

    @PATCH("/complaints/{id}")
    Call<ResponseBody> patchComplaintById(@Header("Authorization") String credentials, @Body RequestBody requestBody, @Path("id") String id);


}
