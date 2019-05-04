package com.grobo.notifications.timetable;

public class TimetableItem {

    private String time;
    private String subject;
    private String room;
    private String subjectName;

    public TimetableItem(){}

    public TimetableItem(String time, String subject, String room, String subjectName){
        this.time = time;
        this.subject = subject;
        this.room = room;
        this.subjectName = subjectName;
    }

    public TimetableItem(String time, String subject){
        this.time = time;
        this.subject = subject;
    }

    public String getsubject() {
        return subject;
    }

    public void setsubject(String subject) {
        this.subject = subject;
    }

    public String gettime() {
        return time;
    }

    public void settime(String time) {
        this.time = time;
    }

    public String getroom() {
        return room;
    }

    public String getsubjectName() {
        return subjectName;
    }

    public void setroom(String room) {
        this.room = room;
    }

    public void setsubjectName(String subjectName) {
        this.subjectName = subjectName;
    }
}
