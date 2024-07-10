package com.example.myapplication.Api;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.Daos.CommentDao;
import com.example.myapplication.MyApplication;
import com.example.myapplication.R;
import com.example.myapplication.db.AppDB;
import com.example.myapplication.entities.Comment;
import com.example.myapplication.retrofit.RetrofitClient;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CommentAPI {
    private CommentDao commentDao;
    private Retrofit retrofit;
    private WebServiceAPI webServiceAPI;
    private MutableLiveData<List<Comment>> commentsLiveData;

    public CommentAPI() {
        retrofit = RetrofitClient.getRetrofit();
        webServiceAPI = retrofit.create(WebServiceAPI.class);

        // Initialize commentDao
        AppDB db = AppDB.getInstance();
        commentDao = db.CommentDao();

        // Initialize LiveData
        commentsLiveData = new MutableLiveData<>();
    }

    public void get(String userId , String videoId) {
        Call<List<Comment>> call = webServiceAPI.getComments( userId , videoId);
        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("CommentAPI", "Successfully fetched comments: " + response.body().size());
                    new Thread(() -> {
                        List<Comment> comments = response.body();
                        for (Comment res : comments) {
                            Log.d("CommentAPI", "Inserting comment: " + res.getText());
                            commentDao.insert(new Comment(res.getEmail(), res.getuserName(),
                                    res.getText(), res.getprofilePic()));
                        }
                        // Update LiveData with the new list of comments
                        commentsLiveData.postValue(comments);
                        Log.d("CommentAPI", "Comments posted to LiveData");
                    }).start();
                } else {
                    Log.e("CommentAPI", "Response unsuccessful or empty. Code: " + response.code());
                    Log.e("CommentAPI", "Response message: " + response.message());
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


}
