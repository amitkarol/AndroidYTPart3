package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.entities.User;
import com.example.myapplication.entities.Video;

import java.util.List;

import adapter.VideoListAdapter;

public class UserPage extends BaseActivity {

    private ImageView imageViewUserPhoto;
    private TextView textViewDisplayName;
    private TextView textViewUserName;
    private TextView textViewNumVideos;
    private RecyclerView recyclerViewUserVideos;
    private Button buttonEdit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);

        imageViewUserPhoto = findViewById(R.id.imageViewUserPhoto);
        textViewDisplayName = findViewById(R.id.textViewDisplayName);
        textViewUserName = findViewById(R.id.textViewUserName);
        textViewNumVideos = findViewById(R.id.textViewNumVideos);
        recyclerViewUserVideos = findViewById(R.id.recyclerViewUserVideos);
        buttonEdit = findViewById(R.id.buttonEdit);

        // Get the user object from the intent
        User user = (User) getIntent().getSerializableExtra("user");

        // Set user information
        if (user != null) {
            textViewDisplayName.setText(user.getDisplayName());
            textViewUserName.setText(user.getFirstName() + " " + user.getLastName());
            textViewNumVideos.setText(user.getVideos().size() + " videos");

            // Load the user's photo
            if (user.getPhoto() != null && !user.getPhoto().isEmpty()) {
                imageViewUserPhoto.setImageURI(Uri.parse(user.getPhoto()));
            } else {
                imageViewUserPhoto.setImageResource(R.drawable.dog1); // Use a placeholder image
            }

            // Initialize RecyclerView with user videos
            List<Video> userVideos = user.getVideos();
            VideoListAdapter adapter = new VideoListAdapter(userVideos, this, user);
            recyclerViewUserVideos.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewUserVideos.setAdapter(adapter);
        }

        // Set the button edit listener
        buttonEdit.setOnClickListener(v -> {
            Intent editUserIntent = new Intent(UserPage.this, EditUserActivity.class);
            editUserIntent.putExtra("user", user);
            startActivity(editUserIntent);
        });

        // Initialize Bottom Navigation (example, you can add listeners as needed)
        ImageView imageViewHome = findViewById(R.id.imageViewHome);
        imageViewHome.setOnClickListener(v -> {
            // Navigate to Home screen
            Intent homeIntent = new Intent(UserPage.this, homescreen.class);
            startActivity(homeIntent);
        });

        ImageView imageViewLightning = findViewById(R.id.imageViewLightning);
        imageViewLightning.setOnClickListener(v -> {
            // Navigate to Lightning screen
        });

        ImageView buttonUpload = findViewById(R.id.buttonUpload);
        buttonUpload.setOnClickListener(v -> {
            // Navigate to Upload screen
        });

        ImageView imageViewPlay = findViewById(R.id.imageViewPlay);
        imageViewPlay.setOnClickListener(v -> {
            // Navigate to Play screen
        });

        ImageView imageViewPerson = findViewById(R.id.imageViewPerson);
        imageViewPerson.setOnClickListener(v -> {
            // Navigate to User Profile screen
        });
    }
}
