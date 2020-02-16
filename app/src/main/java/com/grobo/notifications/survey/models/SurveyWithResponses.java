package com.grobo.notifications.survey.models;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@Keep
public class SurveyWithResponses {

    @SerializedName("questions")
    @Expose
    private List<Question> questions;

    @SerializedName("responses")
    @Expose
    private List<Response> responses;

    @SerializedName("responses_access")
    @Expose
    private List<AccessUser> responsesAccess;

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public List<Response> getResponses() {
        return responses;
    }

    public void setResponses(List<Response> responses) {
        this.responses = responses;
    }

    public List<AccessUser> getResponsesAccess() {
        return responsesAccess;
    }

    public void setResponsesAccess(List<AccessUser> responsesAccess) {
        this.responsesAccess = responsesAccess;
    }
}