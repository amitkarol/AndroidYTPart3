package com.example.myapplication.ViewModels;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.entities.User;
import com.example.myapplication.repositories.UsersRepository;

import java.util.List;

public class UsersViewModel extends ViewModel {
    private UsersRepository repository;
    private LiveData<List<User>> users;


    public UsersViewModel() {
        repository = new UsersRepository();
        users = repository.getAll();
    }

    public LiveData<List<User>> getUsers() {
        return users;
    }

    public LiveData<List<User>> get() {
        return users;
    }
}
