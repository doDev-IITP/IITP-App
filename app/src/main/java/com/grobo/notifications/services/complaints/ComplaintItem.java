package com.grobo.notifications.services.complaints;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.grobo.notifications.feed.FeedPoster;

import java.util.List;

@Keep
public class ComplaintItem {

    @Keep
    public class ComplaintsSuper {

        @SerializedName("complaints")
        @Expose
        private List<ComplaintItem> complaints = null;

        public List<ComplaintItem> getComplaints() {
            return complaints;
        }

        public void setComplaints(List<ComplaintItem> complaints) {
            this.complaints = complaints;
        }

    }

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("complaintPoster")
    @Expose
    private FeedPoster complaintPoster;
    @SerializedName("category")
    @Expose
    private int category;

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("problem")
    @Expose
    private String problem;
    @SerializedName("imageUrl")
    @Expose
    private String imageUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public FeedPoster getComplaintPoster() {
        return complaintPoster;
    }

    public void setComplaintPoster(FeedPoster complaintPoster) {
        this.complaintPoster = complaintPoster;
    }
}
