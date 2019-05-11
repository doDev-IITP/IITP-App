package com.grobo.notifications.explore.services.maintenance;

public class MaintenanceItem {

    private int id;
    private String category;      /*<item>Carpenter</item>
                                    <item>Water Cooler</item>
                                    <item>Pest Control</item>
                                    <item>Washroom problem</item>
                                    <item>Cleaning</item> */
    private String problem;     //description of problem
    private String imageUrl;
    private String status;

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

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
