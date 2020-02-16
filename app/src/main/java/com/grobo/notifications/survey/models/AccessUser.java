package com.grobo.notifications.survey.models;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.grobo.notifications.feed.DataPoster;

@Keep
public class AccessUser {

    @SerializedName("user")
    @Expose
    private DataPoster user;

    @SerializedName("edit_access")
    @Expose
    private Boolean editAccess;


    public DataPoster getUser() {
        return user;
    }

    public void setUser(DataPoster user) {
        this.user = user;
    }

    public Boolean getEditAccess() {
        return editAccess;
    }

    public void setEditAccess(Boolean editAccess) {
        this.editAccess = editAccess;
    }
}