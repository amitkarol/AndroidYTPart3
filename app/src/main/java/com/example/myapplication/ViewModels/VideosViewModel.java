package com.example.myapplication.ViewModels;

import android.content.Context;
import android.net.Uri;
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

    public  LiveData<List<Video>> getUserVideos(String id) {
        Log.d("user videos", "viewmodel before:");
        return videoRepository.getUserVideos(id);
    }

    public void createVideo(String userId, String title, String description, Uri imgUri, Uri videoUri, Context context, Runnable onSuccess) {
        videoRepository.createVideo(userId, title, description, imgUri, videoUri, context, onSuccess);
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

    public LiveData<List<Video>> getTrendingVideos() {
        return videoRepository.getTrendingVideos();
    }


//    public LiveData<Video> getVideoById(String id) {
//        return videoRepository.getVideoById(id);
//    }
}
