package com.grobo.notifications.network;

import com.grobo.notifications.services.maintenance.MaintenanceItem;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MaintenanceRoutes {

    @GET("/maintenances")
    Call<MaintenanceItem.MaintenanceSuper> getAllMaintenance(@Header("Authorization") String credentials);

    @POST("/maintenances")
    Call<ResponseBody> postMaintenance(@Header("Authorization") String credentials, @Body RequestBody body);

    @GET("/maintenances/user")
    Call<MaintenanceItem.MaintenanceSuper> getMaintenanceByUser(@Header("Authorization") String credentials);

    @GET("/maintenances/{id}")
    Call<MaintenanceItem> getMaintenanceById(@Header("Authorization") String credentials, @Path("id") String id);

    @PATCH("/maintenances/{id}")
    Call<ResponseBody> patchMaintenanceById(@Header("Authorization") String credentials, @Body RequestBody requestBody, @Path("id") String id);


}
