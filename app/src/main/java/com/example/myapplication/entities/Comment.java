package com.example.myapplication.entities;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Comment implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private User user;
    private String text;
    private String timestamp;
    private String photoUri;

    // Constructor
    public Comment(User user, String text, String photoUri) {
        this.user = user;
        this.text = text;
        this.photoUri = photoUri;
        this.timestamp = getCurrentTimestamp();
    }

    // Getter and setter for photoUri
    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    // Other getters and setters
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    private String getCurrentTimestamp() {
        // Implementation for getting the current timestamp
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
}
