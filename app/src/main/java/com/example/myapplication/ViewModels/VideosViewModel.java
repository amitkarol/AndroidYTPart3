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

    public LiveData<List<Video>> getVideos() {
        return videos;
    }

    public void createVideo(String title, String description, String img, String video, String owner) {
        Log.d("test3", "viewmodel start video: " + title);
        videoRepository.createVideo(title, description, img, video, owner);
        Log.d("test3", "viewmodel end  video: " + title);
    }

    public void editVideo(String id, String title, String description, String img, String owner) {
        Log.d("test3", "viewmodel start video: " + title);
        videoRepository.editVideo(id, title, description, img, owner);
        Log.d("test3", "viewmodel end  video: " + title);
    }

    public void deleteVideo(Video video) {
        Log.d("test10", "reached viewmodel start");
        Log.d("test10", "reached viewmodel start: " + video);
        videoRepository.deleteVideo(video);
        Log.d("test10", "reached viewmodel end");
    }

    public void updateViews(String id, String pid) {
        Log.d("test6", "updateViews viewmodel start");
        videoRepository.updateViews(id, pid);
        Log.d("test6", "updateViews viewmodel end");
    }

    public Boolean isLiked(String id, String pid) {
        return videoRepository.isLiked(id, pid);
    }

    public void setLikes(String id, String pid, String userEmail) {
        videoRepository.setLikes(id, pid, userEmail);
    }
}
