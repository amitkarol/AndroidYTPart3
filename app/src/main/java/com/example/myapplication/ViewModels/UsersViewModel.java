package com.example.myapplication.ViewModels;

import android.util.Log;

import androidx.lifecycle.LiveData;
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

    public void createUser(String firstName, String lastName, String email, String password, String displayName, String photo) {
        Log.d("test1", "viewmodel email" + email);
        usersRepository.createUser(firstName, lastName, email, password, displayName, photo);
        Log.d("test1", "viewmodel");
    }
}
