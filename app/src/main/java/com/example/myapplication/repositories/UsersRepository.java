package com.example.myapplication.repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.Api.UserAPI;
import com.example.myapplication.Daos.UserDao;
import com.example.myapplication.db.AppDB;
import com.example.myapplication.entities.User;

import java.util.List;

public class UsersRepository {
    private UserDao dao;
    private static UserListData userListData;
    private UserAPI userAPI;

    public UsersRepository() {
        Log.d("test1", "repository builder");
        AppDB db = AppDB.getInstance();
        dao = db.userDao();
        userListData = new UserListData(dao);
        userAPI = new UserAPI();
    }

    static class UserListData extends MutableLiveData<List<User>> {

        private final UserDao userDao;

        public UserListData(UserDao userDao) {
            super();
            this.userDao = userDao;
        }

        @Override
        protected void onActive() {
            super.onActive();
            new Thread(() -> {
                List<User> users = userDao.index();
                postValue(users);
            }).start();
        }
    }

    public LiveData<List<User>> getAll() {
        return userListData;
    }

    public LiveData<User> getUserByEmail(String email) {
        MutableLiveData<User> userLiveData = new MutableLiveData<>();
        userAPI.getUserByEmail(userLiveData, email);
        return userLiveData;
    }

    public void login(String email, String password) {
        userAPI.login(email, password);
    }

    public void createUser(String firstName, String lastName, String email, String password, String displayName, String photo) {
        Log.d("test5", "user photo viewmodel " + photo);
        userAPI.createUser(firstName, lastName, email, password, displayName, photo);
    }
    public LiveData<Boolean> checkEmailExists(String email) {
        MutableLiveData<Boolean> emailExists = new MutableLiveData<>();
        userAPI.checkEmailExists(email, emailExists);
        return emailExists;
    }


    public void updateUser(String email, User user) {
        userAPI.updateUser(email, user);
    }

    public void deleteUser(String email) {
        userAPI.deleteUser(email);
    }
}
