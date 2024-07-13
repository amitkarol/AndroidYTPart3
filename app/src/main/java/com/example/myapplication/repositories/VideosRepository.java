package com.example.myapplication.repositories;

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
            Log.d("user videos", "repository getUserVideos");
            MutableLiveData<List<Video>> userVideos = new MutableLiveData<>();
            new Thread(() -> {
                Log.d("user videos", "repository getUserVideos in thread");
                List<Video> videos = videoDao.getUserVideos(id);
                Log.d("user videos", "repository getUserVideos after dao: " +videos);
                userVideos.postValue(videos);
                Log.d("user videos", "repository getUserVideos after dao: " + userVideos.getValue());
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

    public  LiveData<List<Video>> getUserVideos(String id) {
        Log.d("user videos", "repository before:");
        return videoListData.getUserVideos(id);
    }

    public void createVideo(String title, String description, String img, String video, String owner) {
        Log.d("test3", "repository start video: " + title);
        videoAPI.createVideo(title, description, img, video, owner, () -> videoListData.refreshData());
        Log.d("test3", "repository end video: " + title);
    }

    public void editVideo(String id, String title, String description, String img, String owner) {
        Log.d("test3", "repository start video: " + title);
        videoAPI.editVideo(id, title, description, img, owner, () -> videoListData.refreshData());
        Log.d("test3", "repository end video: " + title);
    }

    public void deleteVideo(Video video) {
        Log.d("test10", "reached repository start");
        Log.d("test10", "reached repository start: " + video);
        videoAPI.deleteVideo(video, () -> videoListData.refreshData());
        Log.d("test10", "reached repository end");
    }

    public void updateViews(String id, String pid) {
        Log.d("test6", "updateViews repository start");
        videoAPI.updateViews(id, pid, () -> videoListData.refreshData());
        Log.d("test6", "updateViews repository end");
    }

    public Boolean isLiked(String id, String pid) {
        MutableLiveData<Boolean> isLiked = new MutableLiveData<>(false);
        videoAPI.isLiked(id, pid, isLiked);
        return isLiked.getValue();
    }

    public void setLikes(String id, String pid, String userEmail) {
        videoAPI.setLikes(id, pid, userEmail, () -> videoListData.refreshData());
    }
}
