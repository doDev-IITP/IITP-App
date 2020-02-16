package com.grobo.notifications.survey.models;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@Keep
public class Question {

    @SerializedName("_id")
    @Expose
    private String id;

    @SerializedName("question_text")
    @Expose
    private String text;

    @SerializedName("question_description")
    @Expose
    private String description;

    @SerializedName("question_type")
    @Expose
    private Integer questionType;

    @SerializedName("question_options")
    @Expose
    private List<String> options;

    @SerializedName("required")
    @Expose
    private Boolean required;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getQuestionType() {
        return questionType;
    }

    public void setQuestionType(Integer questionType) {
        this.questionType = questionType;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }
}