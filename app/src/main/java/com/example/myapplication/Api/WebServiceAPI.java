package com.example.myapplication.Api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import com.example.myapplication.entities.Video;

import java.util.List;

public interface WebServiceAPI {

    @GET("Videos")
    Call<List<Video>> getVideos();

    @POST("Videos")
    Call<Void> createVideo(@Body Video video);

    @DELETE("Videos/{id}")
    Call<Void> deleteVideo(@Path("id") int id);
}