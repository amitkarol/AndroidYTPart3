package com.example.myapplication.Api;
import com.example.myapplication.MyApplication;
import com.example.myapplication.R;
import com.example.myapplication.entities.Video;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

public class VideoAPI {

    private Retrofit retrofit;
    private WebServiceAPI webServiceAPI;

    public VideoAPI() {

        retrofit = new Retrofit.Builder()
                .baseUrl(MyApplication.context.getString(R.string.BaseUrl))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        webServiceAPI = retrofit.create(WebServiceAPI.class);
    }

    public void get() {
        Call<List<Video>> call = webServiceAPI.getVideos();
        call.enqueue(new Callback<List<Video>>() {
            @Override
            public void onResponse(Call<List<Video>> call, Response<List<Video>> response) {
               List<Video> videos = response.body();
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
