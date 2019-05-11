package com.grobo.notifications.explore.services.maintenance;

public class MaintenanceItem {

    private String category;
    private String problem;
    private String imageUrl;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategory() {
        return category;
    }

    public String getProblem() {
        return problem;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

}
