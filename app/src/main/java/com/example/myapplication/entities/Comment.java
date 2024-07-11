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

    private String _id; // MongoDB ID
    private String email;
    private String userName;
    private String text;
    private String date;
    private String profilePic;
    private String videoId;

    public Comment(String email, String userName, String text, String profilePic) {
        this.email = email;
        this.userName = userName;
        this.text = text;
        this.profilePic = profilePic;
        this.date = getCurrentDate();
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    private String getCurrentDate() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
}
