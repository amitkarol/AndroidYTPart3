package com.example.myapplication.Api;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.Daos.VideoDao;
import com.example.myapplication.MyApplication;
import com.example.myapplication.R;
import com.example.myapplication.db.AppDB;
import com.example.myapplication.entities.Video;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VideoAPI {

    private VideoDao videoDao;
    private Retrofit retrofit;
    private WebServiceAPI webServiceAPI;
    private MutableLiveData<List<Video>> videosLiveData;

    public VideoAPI() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(40, TimeUnit.SECONDS)
                .readTimeout(40, TimeUnit.SECONDS)
                .build();
        retrofit = new Retrofit.Builder()
                .baseUrl(MyApplication.context.getString(R.string.BaseUrl))
                .client(client)
                .callbackExecutor(Executors.newSingleThreadExecutor())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        webServiceAPI = retrofit.create(WebServiceAPI.class);

        // Initialize videoDao
        AppDB db = AppDB.getInstance();
        videoDao = db.videoDao();

        // Initialize LiveData
        videosLiveData = new MutableLiveData<>();
    }

    public void get() {
        Call<List<Video>> call = webServiceAPI.get();
        call.enqueue(new Callback<List<Video>>() {
            @Override
            public void onResponse(Call<List<Video>> call, Response<List<Video>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new Thread(() -> {
                        List<Video> videos = response.body();
                        for (Video res : videos) {
                            videoDao.insert(new Video(res.getTitle(), res.getDescription(),
                                    res.getImg(), res.getVideo(), res.getOwner()));
                        }
                        // Update LiveData with the new list of videos
                        videosLiveData.postValue(videos);
                    }).start();
                } else {
                    // Handle unsuccessful response
                }
            }

            @Override
            public void onFailure(Call<List<Video>> call, Throwable t) {
                // Handle the failure case
                // For example, you could log the error or notify the user
                // Log.e("VideoAPI", "Failed to fetch Videos", t);
            }
        });
    }
}
