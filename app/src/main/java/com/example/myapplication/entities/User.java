package com.example.myapplication.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.myapplication.converters.VideoConverter;

@Entity
public class User implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String displayName;
    private String photo;
    private String token = null;

    @TypeConverters(VideoConverter.class)
    private List<Video> Videos;



    // Constructor
    public User(String firstName, String lastName, String email, String password, String displayName, String photo, List<Video> Videos) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.displayName = displayName;
        this.photo = photo;
        this.Videos = Videos;
    }

    @Ignore
    public User(String firstName, String lastName, String email, String password, String displayName, String photo) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.displayName = displayName;
        this.photo = photo;
        this.Videos = new ArrayList<>();
    }

    // Getters and setters

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public List<Video> getVideos() {
        return Videos;
    }

    public void setVideos(List<Video> Videos) {
        this.Videos = Videos;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    // toString method for printing user details
    @Override
    public String toString() {
        return "user{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", displayName='" + displayName + '\'' +
                ", photoUri='" + photo + '\'' +
                ", videos=" + Videos +
                '}';
    }
}
