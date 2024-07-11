package com.example.myapplication.repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.Api.CommentAPI;
import com.example.myapplication.Daos.CommentDao;
import com.example.myapplication.Daos.VideoDao;
import com.example.myapplication.db.AppDB;
import com.example.myapplication.entities.Comment;
import com.example.myapplication.entities.Video;

import java.util.List;

public class CommentsRepository {
    private VideoDao videoDao;
    private CommentDao commentDao;
    private CommentAPI commentAPI;
    private MutableLiveData<List<Comment>> commentsLiveData;

    public CommentsRepository() {
        AppDB db = AppDB.getInstance();
        videoDao = db.videoDao();
        commentDao = db.CommentDao();
        commentAPI = new CommentAPI();
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

    public void addComment(String userId, String videoId, String text, String userName, String email, String profilePic) {
        Log.d("CommentsRepository", "Adding comment: userId=" + userId + ", videoId=" + videoId);
        commentAPI.createComment(userId, videoId, text, userName, email, profilePic);
        commentAPI.getCommentsLiveData().observeForever(comments -> {
            new Thread(() -> {
                Video video = videoDao.get(videoId);
                if (video != null) {
                    video.setComments(comments);
                    videoDao.update(video);

                    for (Comment comment : comments) {
                        comment.setVideoId(videoId);
                        commentDao.insert(comment);
                    }

                    commentsLiveData.postValue(comments);
                    Log.d("CommentsRepository", "Comments updated: " + comments.size());
                }
            }).start();
        });
    }

    public void deleteComment(String userId, String videoId, String commentId) {
        Log.d("CommentsRepository", "Deleting comment: userId=" + userId + ", videoId=" + videoId + ", commentId=" + commentId);
        commentAPI.deleteComment(userId, videoId, commentId);
        commentAPI.getCommentsLiveData().observeForever(comments -> {
            new Thread(() -> {
                Video video = videoDao.get(videoId);
                if (video != null) {
                    video.setComments(comments);
                    videoDao.update(video);

                    for (Comment comment : comments) {
                        comment.setVideoId(videoId);
                        commentDao.delete(comment);
                    }

                    commentsLiveData.postValue(comments);
                    Log.d("CommentsRepository", "Comments updated after deletion: " + comments.size());
                }
            }).start();
        });
    }

    public void editComment(String userId, String videoId, String commentId, String text) {
        Log.d("CommentsRepository", "Editing comment: userId=" + userId + ", videoId=" + videoId + ", commentId=" + commentId);
        commentAPI.editComment(userId, videoId, commentId, text);
        commentAPI.getCommentsLiveData().observeForever(comments -> {
            new Thread(() -> {
                Video video = videoDao.get(videoId);
                if (video != null) {
                    video.setComments(comments);
                    videoDao.update(video);

                    for (Comment comment : comments) {
                        comment.setVideoId(videoId);
                        commentDao.update(comment);
                    }

                    commentsLiveData.postValue(comments);
                    Log.d("CommentsRepository", "Comments updated after editing: " + comments.size());
                }
            }).start();
        });
    }

    public void deleteAllComments() {
        new Thread(() -> {
            commentDao.deleteAll();
            Log.d("CommentsRepository", "Deleted all comments from Room database");
        }).start();
    }
}
