package com.example.myapplication.ViewModels;


import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.entities.Video;
import com.example.myapplication.repositories.VideosRepository;

import java.util.List;

public class VideosViewModel extends ViewModel {
    private VideosRepository videoRepository;
    private LiveData<List<Video>> videos;


    public VideosViewModel() {
        videoRepository = new VideosRepository();
        videos = videoRepository.getAll();
    }

  public LiveData<List<Video>> getVideos() {return videos;
      }

    public LiveData<List<Video>> get() {
        return videos;
    }

    public void createVideo(String title, String description, String img, String video, String owner) {
        Log.d("test3", "viewmodel start video: " + title);
        videoRepository.createVideo(title, description, img, video, owner);
        Log.d("test3", "viewmodel end  video: " + title);
    }
}
