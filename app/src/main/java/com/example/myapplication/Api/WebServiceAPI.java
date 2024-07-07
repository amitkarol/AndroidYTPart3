package com.example.myapplication.Api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import com.example.myapplication.entities.Comment;
import com.example.myapplication.entities.User;
import com.example.myapplication.entities.Video;

import java.util.List;

public interface WebServiceAPI {

    @GET("/api/videos")
    Call<List<Video>> getVideos();

    @POST("/api/videos")
    Call<Void> createVideo(@Body Video video);

    @DELETE("/api/videos/{id}")
    Call<Void> deleteVideo(@Path("id") int id);

    @GET("/api/users")
    Call<List<User>> getUsers();

    @GET("/api/users/{id}/videos/{pid}/comments")
    Call<List<Comment>> getComments();
}