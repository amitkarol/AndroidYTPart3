package com.example.myapplication.ViewModels;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.entities.User;
import com.example.myapplication.repositories.UsersRepository;

import java.util.List;

public class UsersViewModel extends ViewModel {
    private UsersRepository usersRepository;
    private LiveData<List<User>> users;


    public UsersViewModel() {
        Log.d("test1", "viewmodel builder");
        usersRepository = new UsersRepository();
        Log.d("test1", "viewmodel builder end");
        users = usersRepository.getAll();
    }

    public LiveData<List<User>> getUsers() {
        return users;
    }

    public LiveData<List<User>> get() {
        return users;
    }

    public LiveData<User> getUserByEmail(String email) {
        return usersRepository.getUserByEmail(email);
    }

    public void login(String email, String password) {
        usersRepository.login(email, password);
    }

    public void createUser(String firstName, String lastName, String email, String password, String displayName, Context context, Uri photo) {
        Log.d("test1", "viewmodel email" + email);
        Log.d("test5", "user photo viewmodel " + photo);
        usersRepository.createUser(firstName, lastName, email, password, displayName, context, photo);
        Log.d("test1", "viewmodel");
    }

    public LiveData<Boolean> checkEmailExists(String email) {
        return usersRepository.checkEmailExists(email);
    }

    public void updateUser(String email, User user) {
        usersRepository.updateUser(email, user);
    }

    public void deleteUser(String email) {
        usersRepository.deleteUser(email);
    }
}
