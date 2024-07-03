package com.example.myapplication.entities;

import android.util.Log;
import com.example.myapplication.R;
import java.util.ArrayList;
import java.util.List;

public class UserManager {

    private static UserManager instance;
    private List<user> userList;

    private UserManager() {
        userList = new ArrayList<>();
        initializeSampleUsers();
    }

    public static synchronized UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    public void addUser(user user) {
        userList.add(user);
    }

    public List<user> getUserList() {
        return userList;
    }

    public user validateUser(String username, String password) {
        for (user user : userList) {
            if (user.getEmail().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    public boolean isAlreadyExists(String username) {
        for (user user : userList) {
            if (user.getEmail().equals(username)) {
                return true;
            }
        }
        return false;
    }

    private void initializeSampleUsers() {
        String personUri = "android.resource://" + "com.example.myapplication" + "/" + R.drawable.person;
        String maayanUri = "android.resource://" + "com.example.myapplication" + "/" + R.drawable.maayan;
        String idanUri = "android.resource://" + "com.example.myapplication" + "/" + R.drawable.idan;
        String hemiUri = "android.resource://" + "com.example.myapplication" + "/" + R.drawable.hemi;
        String amitUri = "android.resource://" + "com.example.myapplication" + "/" + R.drawable.amit;

        user user1 = new user("Test", "User", "testuser@example.com", "Password@123", "TestUser", personUri, new ArrayList<>());
        user user2 = new user("Maayan", "Zahavi", "maayan@gmail.com", "Haha1234!", "MaayanZ", maayanUri, new ArrayList<>());
        user user3 = new user("Idan", "Zahavi", "idan@gmail.com", "Blabla1234!", "Stam", idanUri, new ArrayList<>());
        user user4 = new user("Hemi", "The king", "hemi@gmail.com", "1234Haha!", "Hemi", hemiUri, new ArrayList<>());
        user user5 = new user("Amit", "Karol", "amit@gmail.com", "amit1234!", "Amit K", amitUri, new ArrayList<>());

        userList.add(user1);
        userList.add(user2);
        userList.add(user3);
        userList.add(user4);
        userList.add(user5);
    }
}
