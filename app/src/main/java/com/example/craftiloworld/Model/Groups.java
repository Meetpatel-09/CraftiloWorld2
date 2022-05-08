package com.example.craftiloworld.Model;

public class Groups {

    private String groupname;
    private String admin;
    private String description;
    private String imageurl;
    private String groupid;

    public Groups() {
    }

    public Groups(String groupname, String admin, String description, String imageurl, String groupid) {
        this.groupname = groupname;
        this.admin = admin;
        this.description = description;
        this.imageurl = imageurl;
        this.groupid = groupid;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }
}
