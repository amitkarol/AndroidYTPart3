package com.example.myapplication.utils;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.entities.Token;
import com.example.myapplication.entities.User;

public class CurrentUser {
    private static CurrentUser instance;
    private MutableLiveData<User> user;
    private MutableLiveData<String> token;

    private CurrentUser() {
        user = new MutableLiveData<>();
        token = new MutableLiveData<>();
    }

    public static synchronized CurrentUser getInstance() {
        if (instance == null) {
            instance = new CurrentUser();
        }
        return instance;
    }

    public MutableLiveData<User> getUser() {
        return user;
    }

    public void setUser(User user) {
        Log.d("test1", "set user: " + user);
        this.user.postValue(user);
        Log.d("test1", "this user: " + this.user.getValue());
    }

    public MutableLiveData<String> getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token.postValue(token);
    }
}
