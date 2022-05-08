package com.example.craftiloworld.Model;

public class NewsUploads {

    private String category;
    private String date;
    private String description;
    private String groupId;
    private String heading;
    private String imageUrl;
    private String postId;
    private String reporterId;

    public NewsUploads() {
    }

    public NewsUploads(String category, String date, String description, String groupId, String heading, String imageUrl, String postId, String reporterId) {
        this.category = category;
        this.date = date;
        this.description = description;
        this.groupId = groupId;
        this.heading = heading;
        this.imageUrl = imageUrl;
        this.postId = postId;
        this.reporterId = reporterId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getReporterId() {
        return reporterId;
    }

    public void setReporterId(String reporterId) {
        this.reporterId = reporterId;
    }
}