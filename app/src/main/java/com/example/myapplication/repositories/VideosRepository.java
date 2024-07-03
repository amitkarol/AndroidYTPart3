package com.example.myapplication.repositories;

import static androidx.compose.runtime.SnapshotIntStateKt.setValue;

import com.example.myapplication.Daos.VideoDao;
import com.example.myapplication.Api.VideoAPI;

import java.util.LinkedList;

public class VideosRepository {
    private VideoDao dao;
    private videoListData videoListData;
    private VideoAPI api;

    public videosRepository() {
        LocalDatabase db = LocalDatabase.getInstance();
        dao = db.VideoDao();
        videoListData = new videoListData();
        api = new videoAPI(videoListData, dao);
    }

    class videoListData extends MutableLiveData<List<video>> {
        public videoListData() {
            super();
            setValue(new LinkedList<>());
        }

        @Override
        protected void onActive() {
            super.onActive();
            new Thread(() -> {
                videoListData.videoValue(dao.get());
            }).start();
        }
    }

    public LiveData<List<video>> getAll() {
        return videoListData;
    }

    public void add(final video video) {
        api.createvideo(video);
    }

    public void delete(final video video) {
        api.deletevideo(video.getId());
    }

    public void reload() {
        api.get();
    }
}
