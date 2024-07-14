package com.example.myapplication.Api;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.Daos.VideoDao;
import com.example.myapplication.db.AppDB;
import com.example.myapplication.entities.Video;
import com.example.myapplication.retrofit.RetrofitClient;
import com.example.myapplication.utils.CurrentUser;
import com.example.myapplication.utils.FileUtils;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class VideoAPI {

    private VideoDao videoDao;
    private Retrofit retrofit;
    private WebServiceAPI webServiceAPI;

    public VideoAPI() {
        retrofit = RetrofitClient.getRetrofit();
        webServiceAPI = retrofit.create(WebServiceAPI.class);

        // Initialize videoDao
        AppDB db = AppDB.getInstance();
        videoDao = db.videoDao();
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
                            Log.d("test7: ", "res videos: " + res);
                            Log.d("testt", res.get_id());
                            videoDao.insert(new Video(res));
                        }
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

    public void createVideo(String userId, String title, String description, Uri imgUri, Uri videoUri, Context context, Runnable onSuccess) {
        CurrentUser currentUser = CurrentUser.getInstance();
        String token = "bearer " + currentUser.getToken().getValue();

        File videoFile = new File(FileUtils.getPathFromUri(context, videoUri));
        File imageFile = new File(FileUtils.getPathFromUri(context, imgUri));

        RequestBody videoRequestBody = RequestBody.create(MediaType.parse("video/*"), videoFile);
        RequestBody imageRequestBody = RequestBody.create(MediaType.parse("image/*"), imageFile);

        MultipartBody.Part videoPart = MultipartBody.Part.createFormData("video", videoFile.getName(), videoRequestBody);
        MultipartBody.Part imgPart = MultipartBody.Part.createFormData("img", imageFile.getName(), imageRequestBody);

        RequestBody titleBody = RequestBody.create(MediaType.parse("text/plain"), title);
        RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"), description);
        RequestBody ownerBody = RequestBody.create(MediaType.parse("text/plain"), userId);

        Call<Video> call = webServiceAPI.createVideo(userId, titleBody, descriptionBody, imgPart, videoPart, ownerBody, token);
        call.enqueue(new Callback<Video>() {
            @Override
            public void onResponse(Call<Video> call, Response<Video> response) {
                Log.d("VideoAPI", "API response: " + response.isSuccessful());
                if (response.isSuccessful() && response.body() != null) {
                    new Thread(() -> {
                        Video newVideo = response.body();
                        videoDao.insert(new Video(newVideo.get_id(), newVideo.getTitle(), newVideo.getDescription(),
                                newVideo.getImg(), newVideo.getVideo(), newVideo.getOwner()));
                        onSuccess.run();
                    }).start();
                } else {
                    Log.d("VideoAPI", "Response failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Video> call, Throwable t) {
                Log.d("VideoAPI", "API call failed: " + t.getMessage());
            }
        });
    }

    public void editVideo(String pid, String title, String description, String img, String owner, Runnable onSuccess) {
        CurrentUser currentUser = CurrentUser.getInstance();
        String token = "bearer " + currentUser.getToken().getValue();
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
                        onSuccess.run();
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

    public void deleteVideo(Video video, Runnable onSuccess) {
        CurrentUser currentUser = CurrentUser.getInstance();
        String token = "bearer " + currentUser.getToken().getValue();
        Log.d("test10", "video " + video.getOwner() + " " + video.get_id());
        Log.d("test10", "token " + token);
        Call<Void> deleteVideo = webServiceAPI.deleteVideo(video.getOwner(), video.get_id(), token);
        deleteVideo.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d("test10", "reached api");
                Log.d("test10", "api response: " + response.isSuccessful());
                if (response.isSuccessful()) {
                    new Thread(() -> {
                        Log.d("test10", "entered thread");
                        videoDao.delete(video);
                        onSuccess.run();
                    }).start();
                } else {
                    Log.d("test10", "response failed api: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("test3", "failed api", t);
            }
        });
    }

    public void updateViews(String id, String pid, Runnable onSuccess) {
        Call<Video> updateViews = webServiceAPI.updateViews(id, pid);
        updateViews.enqueue(new Callback<Video>() {
            @Override
            public void onResponse(Call<Video> call, Response<Video> response) {
                Log.d("test6", "reached api");
                Log.d("test6", "api response: " + response.isSuccessful());
                Log.d("test6", "api response body: " + response.body());
                if (response.isSuccessful() && response.body() != null) {
                    new Thread(() -> {
                        Log.d("test6z", "entered thread");
                        Video updatedVideo = response.body();
                        Log.d("test6", "api video: " + updatedVideo);
                        videoDao.update(updatedVideo);
                        Log.d("test6", "api update: " + updatedVideo);
                        onSuccess.run();
                    }).start();
                } else {
                    Log.d("test6", "response failed api");
                }
            }

            @Override
            public void onFailure(Call<Video> call, Throwable t) {
                Log.d("test3", "failed api");
            }
        });
    }

    public void isLiked(String id, String pid, MutableLiveData<Boolean> liked) {
        CurrentUser currentUser = CurrentUser.getInstance();
        String token = "bearer " + currentUser.getToken().getValue();
        Call<Boolean> isLiked = webServiceAPI.isLiked(id, pid, token);
        isLiked.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Log.d("test3", "reached api");
                Log.d("test3", "api response: " + response.isSuccessful());
                if (response.isSuccessful()) {
                    new Thread(() -> {
                        Log.d("test1", "entered thread");
                        Log.d("test1", "response " + response.body());
                        Log.d("test1", "response value " + response.body().booleanValue());
                        Log.d("test1", "entered thread");
                        liked.postValue(response.body());
                    }).start();
                } else {
                    Log.d("test3", "response failed api");
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.d("test3", "failed api");
            }
        });
    }

    public void setLikes(String id, String pid, String userEmail, Runnable onSuccess) {
        CurrentUser currentUser = CurrentUser.getInstance();
        String token = "bearer " + currentUser.getToken().getValue();
        Call<Video> setLikes = webServiceAPI.setLikes(id, pid, userEmail, token);
        setLikes.enqueue(new Callback<Video>() {
            @Override
            public void onResponse(Call<Video> call, Response<Video> response) {
                Log.d("test3", "reached api");
                Log.d("test3", "api response: " + response.isSuccessful());
                if (response.isSuccessful() && response.body() != null) {
                    new Thread(() -> {
                        Log.d("test1", "entered thread");
                        Video updatedVideo = response.body();
                        Log.d("test3", "api video: " + updatedVideo);
                        videoDao.update(updatedVideo);
                        Log.d("test3", "api update: " + updatedVideo);
                        onSuccess.run();
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
