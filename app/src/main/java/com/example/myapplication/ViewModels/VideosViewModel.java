package com.example.myapplication.ViewModels;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
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

    public Video getVideoById (String id) {
        return videoRepository.getVideoById(id);
    }

    public void createVideo(String userId, String title, String description, Uri imgUri, Uri videoUri, Context context, Runnable onSuccess) {
        videoRepository.createVideo(userId, title, description, imgUri, videoUri, context, onSuccess);
    }

    public void editVideo(String id, String title, String description, Uri imgUri, String owner, Context context, Runnable onSuccess) {
        Log.d("editVideo", "ViewModel editVideo called with id: " + id + ", title: " + title);
        videoRepository.editVideo(id, title, description, imgUri, owner, context, onSuccess);
    }

    public void deleteVideo(Video video, Runnable onSuccess) {
        videoRepository.deleteVideo(video, onSuccess);
    }

    public void updateViews(String id, String pid) {
        Log.d("test6", "updateViews viewmodel start");
        videoRepository.updateViews(id, pid);
        Log.d("test6", "updateViews viewmodel end");
    }

    public LiveData<Boolean> isLiked(String id, String pid) {
        MutableLiveData<Boolean> isLiked = new MutableLiveData<>(false);
        videoRepository.isLiked(id, pid, isLiked);
        return isLiked;
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
