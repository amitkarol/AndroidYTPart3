package com.example.myapplication.Api;

import android.os.Build;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.Daos.CommentDao;
import com.example.myapplication.Daos.VideoDao;
import com.example.myapplication.db.AppDB;
import com.example.myapplication.entities.Comment;
import com.example.myapplication.entities.Video;
import com.example.myapplication.retrofit.RetrofitClient;
import com.example.myapplication.utils.CurrentUser;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CommentAPI {
    private VideoDao videoDao;
    private Retrofit retrofit;
    private WebServiceAPI webServiceAPI;
    private MutableLiveData<List<Comment>> commentsLiveData;

    public CommentAPI() {
        retrofit = RetrofitClient.getRetrofit();
        webServiceAPI = retrofit.create(WebServiceAPI.class);

        AppDB db = AppDB.getInstance();
        videoDao = db.videoDao();
        commentsLiveData = new MutableLiveData<>();
    }

    public void get(String userId, String videoId) {
        Call<List<Comment>> call = webServiceAPI.getComments(userId, videoId);
        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new Thread(() -> {
                        Video video = videoDao.get(videoId);
                        if (video != null) {
                            video.setComments(response.body());
                            videoDao.update(video);
                        }
                        commentsLiveData.postValue(response.body());
                    }).start();
                } else {
                    if (response.errorBody() != null) {
                        try {
                            Log.e("CommentAPI", "Error body: " + response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                Log.e("CommentAPI", "Failed to fetch Comments", t);
            }
        });
    }

    public void createComment(String userId, String videoId, String text, String userName, String email, String profilePic) {
        CurrentUser currentUser = CurrentUser.getInstance();
        String token = "bearer " + currentUser.getToken().getValue();
        Log.d("CommentAPI", "Creating comment: userId=" + userId + ", videoId=" + videoId + ", text=" + text);
        Call<Comment> createComment = webServiceAPI.createComment(token, userId, videoId, text, userName, email, profilePic);

        createComment.enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                Log.d("CommentAPI", "Reached API");
                Log.d("CommentAPI", "API response: " + response.isSuccessful());
                if (response.isSuccessful() && response.body() != null) {
                    new Thread(() -> {
                        Log.d("CommentAPI", "Entered thread");
                        Comment newComment = response.body();
                        Log.d("CommentAPIc", "API comment: " + newComment);

                        newComment.setVideoId(videoId);

                        Video video = videoDao.get(videoId);
                        if (video != null) {
                            List<Comment> comments = video.getComments();
                            comments.add(newComment);
                            video.setComments(comments);
                            videoDao.update(video);
                            commentsLiveData.postValue(comments);
                            Log.d("CommentAPI", "Comment inserted: " + newComment);
                        }
                    }).start();
                } else {
                    if (response.errorBody() != null) {
                        try {
                            Log.e("CommentAPI", "Error body: " + response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Log.d("CommentAPI", "Response failed API");
                }
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                Log.d("CommentAPI", "Failed API");
            }
        });
    }



    public void deleteComment(String userId, String videoId, String commentId) {
        String token = "bearer " + CurrentUser.getInstance().getToken().getValue();
        Log.d("CommentAPI", "Deleting comment: userId=" + userId + ", videoId=" + videoId + ", commentId=" + commentId);
        Call<Void> call = webServiceAPI.deleteComment(token, userId, videoId, commentId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    new Thread(() -> {
                        Video video = videoDao.get(videoId);
                        if (video != null) {
                            List<Comment> comments = video.getComments();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                comments.removeIf(comment -> comment.get_id().equals(commentId));
                            }
                            video.setComments(comments);
                            videoDao.update(video);
                            commentsLiveData.postValue(comments);
                        }
                    }).start();
                } else {
                    if (response.errorBody() != null) {
                        try {
                            Log.e("CommentAPI", "Error body: " + response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("CommentAPI", "Failed to delete Comment", t);
            }
        });
    }

    public void editComment(String userId, String videoId, String commentId, String newText) {
        String token = "bearer " + CurrentUser.getInstance().getToken().getValue();
        Log.d("token iss" ,CurrentUser.getInstance().getToken().getValue() );
        Log.d("CommentAPI", "Editing comment: userId=" + userId + ", videoId=" + videoId + ", commentId=" + commentId + ", text=" + newText);
        Call<Comment> call = webServiceAPI.editComment(token, userId, videoId, commentId, newText);
        call.enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new Thread(() -> {
                        Video video = videoDao.get(videoId);
                        if (video != null) {
                            List<Comment> comments = video.getComments();
                            for (int i = 0; i < comments.size(); i++) {
                                if (comments.get(i).get_id().equals(commentId)) {
                                    comments.set(i, response.body());
                                    break;
                                }
                            }
                            video.setComments(comments);
                            videoDao.update(video);
                            commentsLiveData.postValue(comments);
                        }
                    }).start();
                } else {
                    if (response.errorBody() != null) {
                        try {
                            Log.e("CommentAPI", "Error body: " + response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                Log.e("CommentAPI", "Failed to edit Comment", t);
            }
        });
    }

    public MutableLiveData<List<Comment>> getCommentsLiveData() {
        return commentsLiveData;
    }

}
