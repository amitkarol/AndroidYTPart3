package com.example.myapplication.Api;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

import com.example.myapplication.entities.Comment;
import com.example.myapplication.entities.Token;
import com.example.myapplication.entities.User;
import com.example.myapplication.entities.Video;

import java.util.List;

public interface WebServiceAPI {

    @GET("/api/videos")
    Call<List<Video>> getVideos();

    @POST("/api/videos/all")
    Call<Void> createVideo(@Body Video video);

    @DELETE("/api/videos/{id}")
    Call<Void> deleteVideo(@Path("id") int id);

    @GET("/api/users")
    Call<List<User>> getUsers();

    @Multipart
    @POST("/api/users")
    Call<User> createUser(
            @Part("firstName") RequestBody firstName,
            @Part("lastName") RequestBody lastName,
            @Part("email") RequestBody email,
            @Part("password") RequestBody password,
            @Part("displayName") RequestBody displayName,
            @Part MultipartBody.Part photo
    );

    @GET("/api/users/{id}")
    Call<User> getUserByEmail(@Path("id") String id);

    @Multipart
    @PATCH("/api/users/{id}")
    Call<User> updateUser(
            @Path("id") String id,
            @Part("firstName") RequestBody firstName,
            @Part("lastName") RequestBody lastName,
            @Part("password") RequestBody password,
            @Part("displayName") RequestBody displayName,
            @Part MultipartBody.Part photo,
            @Header("authorization") String token
    );

    @GET("/api/users/{id}/videos")
    Call<List<Video>> getUserVideos(@Path("id") String id);

    @Multipart
    @POST("/api/users/{id}/videos")
    Call<Video> createVideo(
            @Path("id") String id,
            @Part("title") RequestBody title,
            @Part("description") RequestBody description,
            @Part MultipartBody.Part img,
            @Part MultipartBody.Part video,
            @Part("owner") RequestBody owner,
            @Header("authorization") String token
    );

    @Multipart
    @PATCH("/api/users/{id}/videos/{pid}")
    Call<Video> editVideo(
            @Path("id") String id,
            @Path("pid") String pid,
            @Part("title") RequestBody title,
            @Part("description") RequestBody description,
            @Part MultipartBody.Part img,
            @Header("authorization") String token
    );

    @DELETE("/api/users/{id}/videos/{pid}")
    Call<Void> deleteVideo(@Path("id") String id, @Path("pid") String pid, @Header("authorization") String token);

    @GET("/api/users/{id}/videos/{pid}/likes")
    Call<Boolean> isLiked(@Path("id") String id, @Path("pid") String pid, @Header("authorization") String token);

    @FormUrlEncoded
    @PATCH("/api/users/{id}/videos/{pid}/likes")
    Call<Video> setLikes(
            @Path("id") String id,
            @Path("pid") String pid,
            @Field("userEmail") String userEmail,
            @Header("authorization") String token
    );

    @PATCH("/api/users/{id}/videos/{pid}/views")
    Call<Video> updateViews(@Path("id") String id, @Path("pid") String pid);

    @GET("/api/users/{id}/videos/{pid}/comments")
    Call<List<Comment>> getComments(
            @Path("id") String userId,
            @Path("pid") String videoId
    );

    @FormUrlEncoded
    @POST("/api/users/{id}/videos/{pid}/comments")
    Call<Comment> createComment(
            @Header("Authorization") String token,
            @Path("id") String userId,
            @Path("pid") String videoId,
            @Field("text") String text,
            @Field("userName") String userName,
            @Field("email") String email,
            @Field("profilePic") String profilePic
    );

    @DELETE("/api/users/{id}/videos/{pid}/comments/{cid}")
    Call<Void> deleteComment(
            @Header("Authorization") String token,
            @Path("id") String userId,
            @Path("pid") String videoId,
            @Path("cid") String commentId
    );

    @FormUrlEncoded
    @PATCH("/api/users/{id}/videos/{pid}/comments/{cid}")
    Call<Comment> editComment(
            @Header("Authorization") String token,
            @Path("id") String userId,
            @Path("pid") String videoId,
            @Path("cid") String commentId,
            @Field("newText") String newText
    );
    // Token API
    @FormUrlEncoded
    @POST("/api/tokens")
    Call<Token> processLogin(@Field("email") String email, @Field("password") String password);
    @PUT("/api/users/{id}")
    Call<User> updateUser(@Header("Authorization") String token, @Path("id") String userId, @Body User user);

    @DELETE("/api/users/{id}")
    Call<Void> deleteUser( @Header("Authorization") String token, @Path("id") String userId);

    @GET("/api/videos/")
    Call<List<Video>> getTrendingVideos();
}