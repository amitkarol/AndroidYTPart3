package com.example.myapplication.Api;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.Daos.VideoDao;
import com.example.myapplication.MyApplication;
import com.example.myapplication.R;
import com.example.myapplication.db.AppDB;
import com.example.myapplication.entities.User;
import com.example.myapplication.entities.Video;
import com.example.myapplication.retrofit.RetrofitClient;
import com.example.myapplication.utils.CurrentUser;

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
        retrofit = RetrofitClient.getRetrofit();
        webServiceAPI = retrofit.create(WebServiceAPI.class);

        // Initialize videoDao
        AppDB db = AppDB.getInstance();
        videoDao = db.videoDao();

        // Initialize LiveData
        videosLiveData = new MutableLiveData<>();
    }

    public void get() {
        Call<List<Video>> call = webServiceAPI.getVideos();
        call.enqueue(new Callback<List<Video>>() {
            @Override
            public void onResponse(Call<List<Video>> call, Response<List<Video>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new Thread(() -> {
                        List<Video> videos = response.body();
                        for (Video res : videos) {
                            Log.d("test2: ", "res videos: " + res.getVideo());
                            Log.d("testt", res.get_id());
                            videoDao.insert(new Video(res.get_id(), res.getTitle(), res.getDescription(),
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
                Log.e("VideoAPI", "Failed to fetch Videos", t);
            }
        });

    }

    public void createVideo(String title, String description, String img, String video, String owner) {
        CurrentUser currentUser = CurrentUser.getInstance();
        String token = "bearer " +  currentUser.getToken().getValue();
        Log.d("test3", "token is " + token);
        Call<Video> createVideo = webServiceAPI.createVideo(owner, title, description, img, video, owner, token);
        createVideo.enqueue(new Callback<Video>() {
            @Override
            public void onResponse(Call<Video> call, Response<Video> response) {
                Log.d("test3", "reached api");
                Log.d("test3", "api response: " + response.isSuccessful());
                if (response.isSuccessful() && response.body() != null) {
                    new Thread(() -> {
                        Log.d("test1", "entered thread");
                        Video newVideo = response.body();
                        Log.d("test3", "api video: " + newVideo);
                        videoDao.insert(new Video(newVideo.get_id(), newVideo.getTitle(), newVideo.getDescription(),
                                newVideo.getImg(), newVideo.getVideo(), newVideo.getOwner()));
                        Log.d("test3", "api insert: " + newVideo);
                    }).start();
                } else {
                    Log.d("test3", "response failed api");
                }
            }

            @Override
            public void onFailure(Call<Video> call, Throwable t) {
                Log.d("test3", "failed api");
            }
        });
    }

    public void editVideo(String pid, String title, String description, String img, String owner) {
        CurrentUser currentUser = CurrentUser.getInstance();
        String token = "bearer " +  currentUser.getToken().getValue();
        Log.d("test3", "token is " + token);
        Call<Video> editVideo = webServiceAPI.editVideo(owner, pid, title, description, img, token);
        editVideo.enqueue(new Callback<Video>() {
            @Override
            public void onResponse(Call<Video> call, Response<Video> response) {
                Log.d("test3", "reached api");
                Log.d("test3", "api response: " + response.isSuccessful());
                if (response.isSuccessful() && response.body() != null) {
                    new Thread(() -> {
                        Log.d("test1", "entered thread");
                        Video newVideo = response.body();
                        Log.d("test3", "api video: " + newVideo);
                        videoDao.insert(new Video(newVideo.get_id(), newVideo.getTitle(), newVideo.getDescription(),
                                newVideo.getImg(), newVideo.getVideo(), newVideo.getOwner()));
                        Log.d("test3", "api insert: " + newVideo);
                    }).start();
                } else {
                    Log.d("test3", "response failed api");
                }
            }

            @Override
            public void onFailure(Call<Video> call, Throwable t) {
                Log.d("test3", "failed api");
            }
        });
    }
}
