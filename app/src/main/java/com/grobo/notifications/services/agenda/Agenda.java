package com.grobo.notifications.services.agenda;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.grobo.notifications.feed.DataPoster;

@Keep
public class Agenda {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("liked")
    @Expose
    private boolean liked;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("problem")
    @Expose
    private String problem;
    @SerializedName("poster")
    @Expose
    private DataPoster poster;
    @SerializedName("imageUrl")
    @Expose
    private String imageUrl;
    @SerializedName("likesCount")
    @Expose
    private int likesCount;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public DataPoster getPoster() {
        return poster;
    }

    public void setPoster(DataPoster poster) {
        this.poster = poster;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }
}