package com.example.myapplication.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.myapplication.entities.Comment;
import com.example.myapplication.repositories.CommentsRepository;
import java.util.List;

public class CommentsViewModel extends ViewModel {
    private CommentsRepository repository;
    private LiveData<List<Comment>> comments;

    public CommentsViewModel() {
        repository = new CommentsRepository();
    }

    public LiveData<List<Comment>> getCommentsByVideoId(String videoId) {
        if (comments == null) {
            comments = repository.getCommentsByVideoId(videoId);
        }
        return comments;
    }

    public void fetchComments(String userId, String videoId) {
        repository.fetchComments(userId, videoId);
    }
}
