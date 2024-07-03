package com.example.myapplication.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class user implements Serializable {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String displayName;
    private String photoUri;
    private List<video> videos;

    // Constructor
    public user(String firstName, String lastName, String email, String password, String displayName, String photoUri, List<video> videos) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.displayName = displayName;
        this.photoUri = photoUri;
        this.videos = videos;
    }
    public user(String firstName, String lastName, String email, String password, String displayName, String photoUri) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.displayName = displayName;
        this.photoUri = photoUri;
        this.videos = new ArrayList<>();
    }

    // Getters and setters
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

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public List<video> getVideos() {
        return videos;
    }

    public void setVideos(List<video> videos) {
        this.videos = videos;
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
                ", photoUri='" + photoUri + '\'' +
                ", videos=" + videos +
                '}';
    }
}
