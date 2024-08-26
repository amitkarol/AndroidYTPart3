package com.example.myapplication.repositories;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.Api.VideoAPI;
import com.example.myapplication.Daos.VideoDao;
import com.example.myapplication.db.AppDB;
import com.example.myapplication.entities.Video;

import java.util.List;

public class VideosRepository {
    private VideoDao dao;
    private static VideoListData videoListData;
    private VideoAPI videoAPI;

    public VideosRepository() {
        AppDB db = AppDB.getInstance();
        dao = db.videoDao();
        videoListData = new VideoListData(dao);
        videoAPI = new VideoAPI();
    }

    static class VideoListData extends MutableLiveData<List<Video>> {

        private final VideoDao videoDao;

        public VideoListData(VideoDao videoDao) {
            super();
            this.videoDao = videoDao;
        }

        @Override
        protected void onActive() {
            super.onActive();
            refreshData();
        }

        public void refreshData() {
            new Thread(() -> {
                List<Video> videos = videoDao.index();
                postValue(videos);
            }).start();
        }

        public LiveData<List<Video>> getUserVideos(String id) {
            MutableLiveData<List<Video>> userVideos = new MutableLiveData<>();
            new Thread(() -> {
                List<Video> videos = videoDao.getUserVideos(id);
                userVideos.postValue(videos);
            }).start();
            return userVideos;
        }
    }

    public LiveData<List<Video>> getAll() {
        return videoListData;
    }

    public LiveData<List<Video>> getVideos() {
        return videoListData;
    }

    public Video getVideoById (String id) {
        return videoAPI.getVideoById(id);
    }

    public  LiveData<List<Video>> getUserVideos(String id) {
        Log.d("uservideos", "repository before:");
        return videoListData.getUserVideos(id);
    }

    public void createVideo(String userId, String title, String description, Uri imgUri, Uri videoUri, Context context, Runnable onSuccess) {
        videoAPI.createVideo(userId, title, description, imgUri, videoUri, context, onSuccess);
    }

    public void editVideo(String id, String title, String description, Uri imgUri, String owner, Context context, Runnable onSuccess) {
        videoAPI.editVideo(id, title, description, imgUri, owner, context, onSuccess);
        Log.d("edittest", "repository");
        videoListData.refreshData();
    }

    public void deleteVideo(Video video, Runnable onSuccess) {
        videoAPI.deleteVideo(video, onSuccess);
        videoListData.refreshData();
    }

    public void updateViews(String id, String pid) {
        Log.d("test6", "updateViews repository start");
        videoAPI.updateViews(id, pid, () -> videoListData.refreshData());
        Log.d("test6", "updateViews repository end");
    }

    public void isLiked(String id, String pid, MutableLiveData<Boolean> isLiked) {
        videoAPI.isLiked(id, pid, isLiked);
    }

    public void setLikes(String id, String pid, String userEmail) {
        videoAPI.setLikes(id, pid, userEmail, () -> videoListData.refreshData());
    }

    public LiveData<List<Video>> getTrendingVideos() {
        MutableLiveData<List<Video>> trendingVideos = new MutableLiveData<>();
        videoAPI.getTrendingVideos(trendingVideos);
        return trendingVideos;
    }

    public LiveData<List<Video>> getRecommendedVideos(String userEmail, String id , String pid) {
        Log.d("ViewVideo" , "repo before");
        MutableLiveData<List<Video>> recommendedVideos = new MutableLiveData<>();
        videoAPI.getRecommendedVideos(userEmail, id, pid ,recommendedVideos);
        Log.d("ViewVideo" , "repo after " + recommendedVideos.getValue());
        return recommendedVideos;
    }

//    public LiveData<Video> getVideoById(String id) {
//        MutableLiveData<Video> videoData = new MutableLiveData<>();
//        new Thread(() -> {
//            Video video = dao.get(id);
//            videoData.postValue(video);
//        }).start();
//        return videoData;
//    }
}
