package com.example.myapplication.viewmodels;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.entities.Video;
import com.example.myapplication.repositories.VideosRepository;

import java.util.List;

public class VideosViewModel extends ViewModel {
    private VideosRepository repository;
    private LiveData<List<Video>> videos;


    public VideosViewModel() {
        repository = new VideosRepository();
        videos = repository.getAll();
    }

    public LiveData<List<Video>> getVideos() {
        return videos;
    }

    public LiveData<List<Video>> get() {
        return videos;
    }
}
