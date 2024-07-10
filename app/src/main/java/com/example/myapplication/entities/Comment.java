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
    private String userName;
    private String text;
    private String date;
    private String profilePic;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    // Constructor
    public Comment(String email, String userName, String text, String profilePic) {
        this.email = email;
        this.userName = userName;
        this.text = text;
        this.profilePic = profilePic;
        this.date = getCurrentdate();
    }



    // Other getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getprofilePic() {
        return profilePic;
    }

    public void setprofilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setuserName(String userName) {
        this.userName = userName;
    }

    public String getuserName() {
        return userName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getdate() {
        return date;
    }

    public void setdate(String date) {
        this.date = date;
    }

    private String getCurrentdate() {
        // Implementation for getting the current date
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
}
