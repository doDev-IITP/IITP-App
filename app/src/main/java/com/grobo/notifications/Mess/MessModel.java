package com.grobo.notifications.Mess;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.List;

@IgnoreExtraProperties
public class MessModel {

    private boolean full;
    private List<Integer> meals;
    private List<Timestamp> days;
    private Timestamp timestamp;
    private String documentId;

    public void setFull(boolean full) {
        this.full = full;
    }

    public boolean isFull() {
        return full;
    }

    public List<Timestamp> getDays() {
        return days;
    }

    public List<Integer> getMeals() {
        return meals;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setDays(List<Timestamp> days) {
        this.days = days;
    }

    public void setMeals(List<Integer> meals) {
        this.meals = meals;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}
