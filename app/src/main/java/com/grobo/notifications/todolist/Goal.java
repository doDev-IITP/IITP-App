package com.grobo.notifications.todolist;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "todo")
public class Goal {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private boolean isChecked;
    private long timestamp;
    private long alarm;

    public Goal(String name, long timestamp) {
        this.name = name;
        this.timestamp = timestamp;
    }

    @Ignore
    public Goal(){
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public String getText() {
        return getName();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getAlarm() {
        return alarm;
    }

    public void setAlarm(long alarm) {
        this.alarm = alarm;
    }
}
