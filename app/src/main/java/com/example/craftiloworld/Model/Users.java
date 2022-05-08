package com.example.craftiloworld.Model;

public class Users {

    private String name;
    private String email;
    private String bio;
    private String imageurl;
    private String id;

    public Users() {
    }

    public Users(String name, String email, String bio, String imageurl, String id) {
        this.name = name;
        this.email = email;
        this.bio = bio;
        this.imageurl = imageurl;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
