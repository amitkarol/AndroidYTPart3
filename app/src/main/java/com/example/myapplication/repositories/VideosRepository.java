package com.example.myapplication.repositories;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.Api.VideoAPI;
import com.example.myapplication.Daos.VideoDao;
import com.example.myapplication.R;
import com.example.myapplication.db.AppDB;
import com.example.myapplication.entities.User;
import com.example.myapplication.entities.UserManager;
import com.example.myapplication.entities.Video;

import java.util.LinkedList;
import java.util.List;

public class VideosRepository {
    private VideoDao dao;
    private static VideoListData videoListData;
    VideoAPI videoAPI;

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
            new Thread(() -> {
                List<Video> videos = videoDao.index();
                postValue(videos);
            }).start();
        }
    }

    public LiveData<List<Video>> getAll() {
        return videoListData;
    }

    public void createVideo(String title, String description, String img, String video, String owner) {
        Log.d("test3", "repository start video: " + title);
        videoAPI.createVideo(title, description, img, video, owner);
        Log.d("test3", "repository end video: " + title);
    }
}
