package com.example.myapplication.entities;

import android.os.Build;

import com.example.myapplication.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserManager {

    private static UserManager instance;
    private List<User> userList;

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

    public void addUser(User user) {
        userList.add(user);
    }

    public List<User> getUserList() {
        return userList;
    }

    public User validateUser(String username, String password) {
        for (User user : userList) {
            if (user.getEmail().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    public boolean isAlreadyExists(String username) {
        for (User user : userList) {
            if (user.getEmail().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public User getUserByEmail(String email) {
        Optional<User> user = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            user = userList.stream()
                    .filter(u -> u.getEmail().equalsIgnoreCase(email))
                    .findFirst();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return user.orElse(null);
        }
        return null;
    }

    private void initializeSampleUsers() {
        String personUri = "android.resource://" + "com.example.myapplication" + "/" + R.drawable.person;
        String maayanUri = "android.resource://" + "com.example.myapplication" + "/" + R.drawable.maayan;
        String idanUri = "android.resource://" + "com.example.myapplication" + "/" + R.drawable.idan;
        String hemiUri = "android.resource://" + "com.example.myapplication" + "/" + R.drawable.hemi;
        String amitUri = "android.resource://" + "com.example.myapplication" + "/" + R.drawable.amit;

        User user1 = new User("Test", "User", "testuser@example.com", "Password@123", "TestUser", personUri, new ArrayList<>());
        User user2 = new User("Maayan", "Zahavi", "maayan@gmail.com", "Haha1234!", "MaayanZ", maayanUri, new ArrayList<>());
        User user3 = new User("Idan", "Zahavi", "idan@gmail.com", "Blabla1234!", "Stam", idanUri, new ArrayList<>());
        User user4 = new User("Hemi", "The king", "hemi@gmail.com", "1234Haha!", "Hemi", hemiUri, new ArrayList<>());
        User user5 = new User("Amit", "Karol", "amit@gmail.com", "amit1234!", "Amit K", amitUri, new ArrayList<>());

        userList.add(user1);
        userList.add(user2);
        userList.add(user3);
        userList.add(user4);
        userList.add(user5);
    }
}
