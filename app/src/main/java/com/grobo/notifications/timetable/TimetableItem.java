package com.grobo.notifications.timetable;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Keep
public class TimetableItem {

    @Expose
    @SerializedName("time")
    private String time;
    @Expose
    @SerializedName("subject")
    private String subject;
    @Expose
    @SerializedName("room")
    private String room;
    @Expose
    @SerializedName("subjectName")
    private String subjectName;

    public TimetableItem(){}

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }
}
