package com.example.myapplication.Api;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.Daos.VideoDao;
import com.example.myapplication.db.AppDB;
import com.example.myapplication.entities.Comment;
import com.example.myapplication.entities.Video;
import com.example.myapplication.retrofit.RetrofitClient;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CommentAPI {
    private VideoDao videoDao;
    private Retrofit retrofit;
    private WebServiceAPI webServiceAPI;
    private MutableLiveData<List<Comment>> commentsLiveData;

    public CommentAPI() {
        retrofit = RetrofitClient.getRetrofit();
        webServiceAPI = retrofit.create(WebServiceAPI.class);

        AppDB db = AppDB.getInstance();
        videoDao = db.videoDao();
        commentsLiveData = new MutableLiveData<>();
    }

    public void get(String userId, String videoId) {
        Call<List<Comment>> call = webServiceAPI.getComments(userId, videoId);
        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new Thread(() -> {
                        Video video = videoDao.get(videoId);
                        if (video != null) {
                            video.setComments(response.body());
                            videoDao.update(video);
                        }
                        commentsLiveData.postValue(response.body());
                    }).start();
                } else {
                    if (response.errorBody() != null) {
                        try {
                            Log.e("CommentAPI", "Error body: " + response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                Log.e("CommentAPI", "Failed to fetch Comments", t);
            }
        });
    }

    public MutableLiveData<List<Comment>> getCommentsLiveData() {
        return commentsLiveData;
    }
}
