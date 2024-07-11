package com.example.myapplication.ViewModels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.entities.Comment;
import com.example.myapplication.repositories.CommentsRepository;

import java.util.List;

public class CommentsViewModel extends ViewModel {
    private CommentsRepository repository;

    public CommentsViewModel() {
        repository = new CommentsRepository();
    }

    public LiveData<List<Comment>> getCommentsByVideoId(String videoId) {
        return repository.getCommentsByVideoId(videoId);
    }

    public void fetchComments(String userId, String videoId) {
        repository.fetchComments(userId, videoId);
    }

    public void addComment(String userId, String videoId, String text, String userName, String email, String profilePic) {
        Log.d("CommentsViewModel", "Adding comment for videoId: " + videoId);
        repository.addComment(userId, videoId, text, userName, email, profilePic);
    }

    public void deleteComment(String userId, String videoId, String commentId) {
        Log.d("CommentsViewModel", "Deleting comment for videoId: " + videoId + ", commentId: " + commentId);
        repository.deleteComment(userId, videoId, commentId);
    }

    public void editComment(String userId, String videoId, String commentId, String text) {
        Log.d("CommentsViewModel", "Editing comment for videoId: " + videoId + ", commentId: " + commentId);
        repository.editComment(userId, videoId, commentId, text);
    }

    public void deleteAllComments() {
        repository.deleteAllComments();
    }
}
