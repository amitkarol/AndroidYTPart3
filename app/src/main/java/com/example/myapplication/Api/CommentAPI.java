package com.example.myapplication.Api;

import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.Daos.CommentDao;
import com.example.myapplication.MyApplication;
import com.example.myapplication.R;
import com.example.myapplication.db.AppDB;
import com.example.myapplication.entities.Comment;
import com.example.myapplication.retrofit.RetrofitClient;

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

    public void get() {
        Call<List<Comment>> call = webServiceAPI.getComments();
        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new Thread(() -> {
                        List<Comment> comments = response.body();
                        for (Comment res : comments) {
                            commentDao.insert(new Comment(res.getEmail(), res.getDisplayName(),
                                    res.getText(), res.getPhotoUri()));
                        }
                        // Update LiveData with the new list of comments
                        commentsLiveData.postValue(comments);
                    }).start();
                } else {
                    // Handle unsuccessful response
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                // Handle the failure case
                // For example, you could log the error or notify the comment
                // Log.e("CommentAPI", "Failed to fetch Comments", t);
            }
        });
    }
}
