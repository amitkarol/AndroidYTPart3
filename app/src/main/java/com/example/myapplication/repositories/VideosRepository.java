package com.example.myapplication.repositories;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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

    public VideosRepository() {
        AppDB db = AppDB.getInstance();
        dao = db.videoDao();
        videoListData = new VideoListData(dao);
    }

    static class VideoListData extends MutableLiveData<List<Video>> {

        private final VideoDao videoDao;

        public VideoListData(VideoDao videoDao) {
            super();
            this.videoDao = videoDao;
            List<Video> videos = new LinkedList<>();
            UserManager userManager = UserManager.getInstance();

            User maayan = userManager.validateUser("maayan@gmail.com", "Haha1234!");

            int videoResource = R.raw.video1;
            String videoUrl = "android.resource://" + "com.example.myapplication" + "/" + videoResource;

            int photoResource = R.drawable.dior;
            String photoUrl = "android.resource://" + "com.example.myapplication" + "/" + photoResource;

            String title = "Dior gallery - Paris";
            String description = "So beautiful";
            User owner = maayan;
            int views = 1000;
            int likes = 3;

            Video newVideo = new Video(title, description, photoUrl, photoResource, videoUrl, owner.getEmail(), views, likes);
            videos.add(newVideo);
            setValue(videos);
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
}
