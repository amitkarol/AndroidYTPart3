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

    private String  email;
    private String displayName;
    private String text;
    private String timestamp;
    private String photoUri;

    // Constructor
    public Comment(String email, String displayName, String text, String photoUri) {
        this.email = email;
        this.displayName = displayName;
        this.text = text;
        this.photoUri = photoUri;
        this.timestamp = getCurrentTimestamp();
    }


    // Other getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
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
