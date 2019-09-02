package com.grobo.notifications.todolist;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.yalantis.beamazingtoday.interfaces.BatModel;

@Entity(tableName = "todo")
public class Goal implements BatModel {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private boolean isChecked;
    private long dateAdded;

    public Goal(String name, long dateAdded) {
        this.name = name;
        this.dateAdded = dateAdded;
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

    @Override
    public boolean isChecked() {
        return isChecked;
    }

    @Override
    public String getText() {
        return getName();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }
}
