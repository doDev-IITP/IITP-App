package com.grobo.notifications.network;

import com.grobo.notifications.survey.models.DetailedSurvey;
import com.grobo.notifications.survey.models.Question;
import com.grobo.notifications.survey.models.Survey;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface SurveyRoutes {

    @GET("/surveys")
    Call<List<Survey>> getAllSurveys(@Header("Authorization") String credentials);

    @POST("/surveys")
    Call<ResponseBody> postNewSurvey(@Header("Authorization") String credentials, @Body RequestBody body);

    @GET("/surveys/{id}")
    Call<DetailedSurvey> getSurveyById(@Header("Authorization") String credentials, @Path("id") String surveyId);

    @GET("/surveys/{id}/questions")
    Call<List<Question>> getSurveyQuestions(@Header("Authorization") String credentials, @Path("id") String surveyId);

    @POST("/surveys/{id}/questions")
    Call<ResponseBody> addQuestionToSurvey(@Header("Authorization") String credentials, @Path("id") String surveyId, @Body RequestBody body);

    @GET("/surveys/{id}/responses")
    Call<List<Question>> getSurveyResponses(@Header("Authorization") String credentials, @Path("id") String surveyId);

    @POST("/surveys/{id}/responses")
    Call<ResponseBody> fillSurvey(@Header("Authorization") String credentials, @Path("id") String surveyId, @Body RequestBody body);


}
