package com.grobo.notifications.database;

public class PersonHandles {

    private String fb;
    private String twitter;
    private String insta;
    private String github;
    private String email;
    private String phone;

    public PersonHandles(){}

    public PersonHandles (String phone, String email){
        this.email = email;
        this.phone = phone;
    }

    public PersonHandles (String phone, String email, String github){
        this.github = github;
        this.email = email;
        this.phone = phone;
    }

    public PersonHandles (String phone, String email, String fb, String twitter, String insta){
        this.fb = fb;
        this.email = email;
        this.twitter = twitter;
        this.insta = insta;
        this.phone = phone;
    }

    public PersonHandles (String phone, String email, String fb, String twitter, String insta, String github){
        this.fb = fb;
        this.email = email;
        this.twitter = twitter;
        this.insta = insta;
        this.github = github;
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public String getFb() {
        return fb;
    }

    public String getGithub() {
        return github;
    }

    public String getInsta() {
        return insta;
    }

    public String getPhone() {
        return phone;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFb(String fb) {
        this.fb = fb;
    }

    public void setGithub(String github) {
        this.github = github;
    }

    public void setInsta(String insta) {
        this.insta = insta;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }
}
