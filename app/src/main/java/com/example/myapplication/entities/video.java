package com.example.myapplication.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class video implements Serializable {
    private String id;
    private String title;
    private String description;
    private String thumbnailUrl; // For external images
    private int thumbnailResId; // For drawable resource IDs
    private String videoUrl;
    private user user; // Add this line
    private int viewCount;
    private int likeCount;
    private Map<String, Boolean> userLikes; // Track likes per user
    private List<Comment> comments;

    public video(String id, String title, String description, String thumbnailUrl, int thumbnailResId, String videoUrl, user user, int viewCount, int likeCount) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.thumbnailResId = thumbnailResId;
        this.videoUrl = videoUrl;
        this.user = user; // Add this line
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.userLikes = new HashMap<>();
        this.comments = new ArrayList<>();
    }

    // Copy constructor
    public video(video original) {
        this.id = original.id;
        this.title = original.title;
        this.description = original.description;
        this.thumbnailUrl = original.thumbnailUrl;
        this.thumbnailResId = original.thumbnailResId;
        this.videoUrl = original.videoUrl;
        this.user = original.user;
        this.likeCount = original.likeCount;
        this.viewCount = original.viewCount;
        this.comments = new ArrayList<>(original.comments);
    }

    // Getters and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public int getThumbnailResId() {
        return thumbnailResId;
    }

    public void setThumbnailResId(int thumbnailResId) {
        this.thumbnailResId = thumbnailResId;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public user getUser() { // Add this method
        return user;
    }

    public void setUser(user user) { // Add this method
        this.user = user;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public Map<String, Boolean> getUserLikes() {
        return userLikes;
    }

    public void setUserLikes(Map<String, Boolean> userLikes) {
        this.userLikes = userLikes;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

    public void likeVideo(String username) {
        if (!userLikes.containsKey(username) || !userLikes.get(username)) {
            userLikes.put(username, true);
            this.likeCount++;
        }
    }

    public void unlikeVideo(String username) {
        if (userLikes.containsKey(username) && userLikes.get(username)) {
            userLikes.put(username, false);
            this.likeCount--;
        }
    }

    public boolean hasLiked(String username) {
        return userLikes.containsKey(username) && userLikes.get(username);
    }

    @Override
    public String toString() {
        return "video{" +
                "id='" + id + '\'' +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", thumbnailUrl='" + thumbnailUrl + '\'' +
                ", thumbnailResId=" + thumbnailResId +
                ", videoUrl='" + videoUrl + '\'' +
                ", user=" + user + // Add this line
                ", viewCount=" + viewCount +
                ", likeCount=" + likeCount +
                ", userLikes=" + userLikes +
                ", comments=" + comments +
                '}';
    }
}
