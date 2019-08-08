package com.grobo.notifications.services.maintenance;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.grobo.notifications.feed.FeedPoster;

import java.util.List;

public class MaintenanceItem {

    public class MaintenanceSuper {

        @SerializedName("maintenances")
        @Expose
        private List<MaintenanceItem> maintenances = null;

        public List<MaintenanceItem> getMaintenances() {
            return maintenances;
        }

        public void setMaintenances(List<MaintenanceItem> maintenances) {
            this.maintenances = maintenances;
        }

    }

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("maintenancePoster")
    @Expose
    private FeedPoster maintenancePoster;
    @SerializedName("category")
    @Expose
    private int category;

    /*  <item>Water Cooler</item>
        <item>Pest Control</item>
        <item>Washroom problem</item>
        <item>Cleaning</item> */

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

    public FeedPoster getMaintenancePoster() {
        return maintenancePoster;
    }

    public void setMaintenancePoster(FeedPoster maintenancePoster) {
        this.maintenancePoster = maintenancePoster;
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
}
