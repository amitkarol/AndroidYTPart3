package com.example.myapplication.utils;

import com.example.myapplication.entities.User;

public class TokenManager {
    private static TokenManager instance;
    private String token;
    private User user;

    private TokenManager() {}

    public static synchronized TokenManager getInstance() {
        if (instance == null) {
            instance = new TokenManager();
        }
        return instance;
    }

    public void setToken(String token) { this.token = token; }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }

    public void clearToken() {
        token = null;
        user = null;
    }
}
