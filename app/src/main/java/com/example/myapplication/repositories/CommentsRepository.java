package com.example.myapplication.repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.myapplication.Api.CommentAPI;
import com.example.myapplication.Daos.VideoDao;
import com.example.myapplication.db.AppDB;
import com.example.myapplication.entities.Comment;
import com.example.myapplication.entities.Video;
import java.util.List;

public class CommentsRepository {
    private VideoDao videoDao;
    private MutableLiveData<List<Comment>> commentsLiveData;

    public CommentsRepository() {
        AppDB db = AppDB.getInstance();
        videoDao = db.videoDao();
        commentsLiveData = new MutableLiveData<>();
    }

    public LiveData<List<Comment>> getCommentsByVideoId(String videoId) {
        new Thread(() -> {
            Video video = videoDao.get(videoId);
            if (video != null) {
                commentsLiveData.postValue(video.getComments());
            } else {
                Log.d("CommentsRepository", "Video not found");
            }
        }).start();
        return commentsLiveData;
    }

    public void fetchComments(String userId, String videoId) {
        CommentAPI commentAPI = new CommentAPI();
        commentAPI.get(userId, videoId);

        // Update the local LiveData after fetching comments
        commentAPI.getCommentsLiveData().observeForever(comments -> {
            new Thread(() -> {
                Video video = videoDao.get(videoId);
                if (video != null) {
                    video.setComments(comments);
                    videoDao.update(video);
                    commentsLiveData.postValue(video.getComments());
                    Log.d("CommentsRepository", "Updated LiveData with comments: " + video.getComments().size());
                }
            }).start();
        });
    }
}
