package com.grobo.notifications.network;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface AdminRoutes {

    @POST("/admin/notify")
    Call<ResponseBody> postNotification(@Header("Authorization") String credentials, @Body RequestBody body);


}
