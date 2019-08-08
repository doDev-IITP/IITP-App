package com.grobo.notifications.clubs;

public class PorItem {
    private String imageurl;
    private String name;
    private String position;

    public PorItem(String name,String imageurl,String position)
    {
        this.imageurl=imageurl;
        this.name=name;
        this.position=position;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getImageurl() {
        return imageurl;
    }

    public String getPosition() {
        return position;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
