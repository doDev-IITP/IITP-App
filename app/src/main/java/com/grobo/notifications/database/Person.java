package com.grobo.notifications.database;

import androidx.room.Ignore;

public class Person {

    private String name;
    private String id;
    private String phone;
    private String email;
    private String image;

    @Ignore
    public Person(){}

    @Ignore
    public Person(String name, String image){
        this.name = name;
        this.image = image;
    }

    public Person(String name, String id, String phone, String email, String image){
        this.name = name;
        this.phone = phone;
        this.id = id;
        this.email = email;
        this.image = image;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
