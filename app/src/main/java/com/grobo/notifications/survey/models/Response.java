package com.grobo.notifications.survey.models;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@Keep
public class Response {

    @SerializedName("_id")
    @Expose
    private String id;

    @SerializedName("response_user")
    @Expose
    private String responseUser;

    @SerializedName("response_details")
    @Expose
    private List<Answer> responseDetails;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResponseUser() {
        return responseUser;
    }

    public void setResponseUser(String responseUser) {
        this.responseUser = responseUser;
    }

    public List<Answer> getResponseDetails() {
        return responseDetails;
    }

    public void setResponseDetails(List<Answer> responseDetails) {
        this.responseDetails = responseDetails;
    }
}