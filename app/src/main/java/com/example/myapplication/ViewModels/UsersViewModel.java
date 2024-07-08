package com.example.myapplication.ViewModels;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.entities.User;
import com.example.myapplication.repositories.UsersRepository;

import java.util.List;

public class UsersViewModel extends ViewModel {
    private UsersRepository usersRepository;
   // private LiveData<List<User>> users;


    public UsersViewModel() {
        usersRepository = new UsersRepository();
        //users = usersRepository.getAll();
    }
//
//    public LiveData<List<User>> getUsers() {
//        return users;
//    }

//    public LiveData<List<User>> get() {
//        return users;
//    }

    public void createUser(String firstName, String lastName, String email, String password, String displayName, String photo) {
        usersRepository.createUser(firstName, lastName, email, password, displayName, photo);
    }
}
