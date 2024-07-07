package com.example.myapplication.repositories;

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

    public void createUser(String firstName, String lastName, String email, String password, String displayName, String photo) {
        userAPI.createUser(firstName, lastName, email, password, displayName, photo);
    }
}
