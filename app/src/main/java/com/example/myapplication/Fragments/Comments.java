package com.example.myapplication.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.entities.Comment;
import com.example.myapplication.entities.user;
import com.example.myapplication.entities.video;
import com.example.myapplication.login;

import adapter.CommentsAdapter;

import java.util.List;

public class Comments extends DialogFragment {

    private RecyclerView commentsRecyclerView;
    private CommentsAdapter commentsAdapter;
    private List<Comment> commentList;
    private video currentVideo;
    private user loggedInUser;
    private EditText commentEditText;
    private Button addCommentButton;

    public Comments(video currentVideo, user loggedInUser) {
        this.currentVideo = currentVideo;
        this.loggedInUser = loggedInUser;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.comments, container, false);

        commentsRecyclerView = view.findViewById(R.id.commentsRecyclerView);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Button closeButton = view.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> dismiss());

        // Initialize with comments from the video
        commentList = currentVideo.getComments();

        commentsAdapter = new CommentsAdapter(getActivity(), commentList, loggedInUser);
        commentsRecyclerView.setAdapter(commentsAdapter);

        // Initialize comment input fields
        commentEditText = view.findViewById(R.id.commentEditText);
        addCommentButton = view.findViewById(R.id.addCommentButton);

        addCommentButton.setOnClickListener(v -> addComment());

        return view;
    }

    private void addComment() {
        if (loggedInUser.getEmail().equals("testuser@example.com")) {
            redirectToLogin();
            return;
        }

        String commentText = commentEditText.getText().toString().trim();
        if (!commentText.isEmpty()) {
            Comment newComment = new Comment(loggedInUser, commentText, loggedInUser.getPhotoUri());
            currentVideo.addComment(newComment);
            commentsAdapter.notifyItemInserted(commentList.size() - 1);
            commentsRecyclerView.scrollToPosition(commentList.size() - 1);
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
        // Set the dialog to appear in a specific area of the screen
        if (getDialog() != null && getDialog().getWindow() != null) {
            WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
            params.gravity = Gravity.BOTTOM;
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT; // Adjust this as needed
            getDialog().getWindow().setAttributes(params);
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
