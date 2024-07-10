package com.example.myapplication.Api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import com.example.myapplication.entities.Comment;
import com.example.myapplication.entities.Token;
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

    @FormUrlEncoded
    @POST("/api/users")
    Call<User> createUser(@Field("firstName") String firstName, @Field("lastName") String lastName,
                                     @Field("email") String email, @Field("password") String password,
                                     @Field("displayName") String displayName, @Field("photo") String photo);

    @FormUrlEncoded
    @POST("/api/users/{id}/videos")
    Call<Video> createVideo(@Field("title") String title, @Field("description") String description,
                          @Field("img") String img, @Field("video") String video,
                          @Field("owner") String owner);

    @GET("/api/users/{id}/videos/{pid}/comments")
    Call<List<Comment>> getComments(
            @Path("id") String userId,
            @Path("pid") String videoId
    );

    // Token API
    @FormUrlEncoded
    @POST("/api/tokens")
    Call<Token> processLogin(@Field("email") String email, @Field("password") String password);
}