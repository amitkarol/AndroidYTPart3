package com.example.myapplication.ViewModels;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.entities.Comment;
import com.example.myapplication.repositories.CommentsRepository;

import java.util.List;

public class CommentsViewModel extends ViewModel {
    private CommentsRepository repository;
    private LiveData<List<Comment>> comments;


    public CommentsViewModel() {
        repository = new CommentsRepository();
        comments = repository.getAll();
    }

    public LiveData<List<Comment>> getComments() {
        return comments;
    }

    public LiveData<List<Comment>> get() {
        return comments;
    }
}
