package com.grobo.notifications.network;

import com.grobo.notifications.services.agenda.AgendaItems;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AgendaRoutes {

    @GET("/agendas")
    Call<AgendaItems> getAllAgendas(@Header("Authorization") String credentials);

    @POST("/agendas")
    Call<ResponseBody> postAgenda(@Header("Authorization") String credentials, @Body RequestBody body);

    @POST("/agendas/react/{agendaId}")
    Call<ResponseBody> reactOnAgenda(@Header("Authorization") String credentials, @Path("agendaId") String agendaId);

    @GET("/agendas/react/{agendaId}")
    Call<ResponseBody> getAgendaReacts(@Header("Authorization") String credentials, @Path("agendaId") String agendaId);

}
