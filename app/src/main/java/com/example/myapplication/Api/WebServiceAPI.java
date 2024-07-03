package com.example.myapplication.Api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

import com.example.myapplication.entities.Video;

import java.util.List;

public interface WebServiceAPI {

    @GET("Videos")
    Call<List<Video>> getVideos();

    @POST("Videos")
    Call<Void> createPost(@Body Video video);

    @DELETE("Videos/{id}")
    Call<Void> deletePost(@Path("id") int id);
}