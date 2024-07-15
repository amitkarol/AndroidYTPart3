package com.example.myapplication.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.ViewModels.CommentsViewModel;
import com.example.myapplication.entities.Comment;
import com.example.myapplication.entities.User;
import com.example.myapplication.entities.Video;
import com.example.myapplication.login;
import com.example.myapplication.utils.CurrentUser;

import java.util.ArrayList;
import java.util.List;

import adapter.CommentsAdapter;

public class Comments extends DialogFragment {
    private RecyclerView commentsRecyclerView;
    private CommentsAdapter commentsAdapter;
    private List<Comment> commentList = new ArrayList<>();
    private Video currentVideo;
    private User loggedInUser;
    private EditText commentEditText;
    private Button addCommentButton;
    private CommentsViewModel commentsViewModel;

    public Comments(Video currentVideo) {
        this.currentVideo = currentVideo;
        this.loggedInUser = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.comments, container, false);

        CurrentUser currentUser = CurrentUser.getInstance();
        currentUser.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                loggedInUser = user;
                Log.d("test1", "loggedInUser: " + loggedInUser);
                setupCommentsAdapter();
            }
        });

        commentsViewModel = new ViewModelProvider(this).get(CommentsViewModel.class);

        commentsRecyclerView = view.findViewById(R.id.commentsRecyclerView);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Button closeButton = view.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> dismiss());

        commentEditText = view.findViewById(R.id.commentEditText);
        addCommentButton = view.findViewById(R.id.addCommentButton);
        addCommentButton.setOnClickListener(v -> addComment());

        commentsViewModel.getCommentsByVideoId(String.valueOf(currentVideo.get_id())).observe(getViewLifecycleOwner(), new Observer<List<Comment>>() {
            @Override
            public void onChanged(List<Comment> comments) {
                commentList = comments;
                if (commentsAdapter != null) {
                    commentsAdapter.setComments(comments);
                }
            }
        });

        commentsViewModel.fetchComments(currentVideo.getOwner(), String.valueOf(currentVideo.get_id()));

        return view;
    }

    private void setupCommentsAdapter() {
        if (loggedInUser != null) {
            commentsAdapter = new CommentsAdapter(getActivity(), commentList, loggedInUser);
            commentsRecyclerView.setAdapter(commentsAdapter);
        }
    }

    private void addComment() {
        if (loggedInUser == null) {
            redirectToLogin();
            return;
        }

        String commentText = commentEditText.getText().toString().trim();
        if (!commentText.isEmpty()) {
            String userName = loggedInUser.getDisplayName();
            String email = loggedInUser.getEmail();
            String profilePic = loggedInUser.getPhoto();

            Comment newComment = new Comment(email, userName, commentText, profilePic);
            newComment.setVideoId(String.valueOf(currentVideo.get_id()));

            // הוספת התגובה למאגר
            commentsViewModel.addComment(loggedInUser.getEmail(), String.valueOf(currentVideo.get_id()), commentText, userName, email, profilePic);
            Log.d("addcomment", "Adding comment to video: " + currentVideo.get_id());

            // עדכון ה-Adapter עם התגובה החדשה
            commentsAdapter.addComment(newComment);
            commentEditText.setText("");
        } else {
            Toast.makeText(getContext(), "Comment cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void redirectToLogin() {
        Intent loginIntent = new Intent(getActivity(), login.class);
        startActivity(loginIntent);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
            params.gravity = Gravity.BOTTOM;
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            getDialog().getWindow().setAttributes(params);
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
    }
}
