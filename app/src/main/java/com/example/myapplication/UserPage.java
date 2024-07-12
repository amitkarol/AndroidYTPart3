package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.entities.User;
import com.example.myapplication.entities.Video;
import com.example.myapplication.ViewModels.UsersViewModel;

import java.util.ArrayList;
import java.util.List;

import adapter.VideoListAdapter;

public class UserPage extends BaseActivity {

    private ImageView imageViewUserPhoto;
    private TextView textViewDisplayName;
    private TextView textViewUserName;
    private TextView textViewNumVideos;
    private RecyclerView recyclerViewUserVideos;
    private Button buttonEdit;
    private UsersViewModel usersViewModel;
    private User user;

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

        usersViewModel = new ViewModelProvider(this).get(UsersViewModel.class);

        // Get the email from the intent
        String email = getIntent().getStringExtra("user_email");
        Log.d("UserPage", "Received email: " + email);

        if (email != null) {
            Log.d("UserPage", "Attempting to load user with email: " + email);
            usersViewModel.getUserByEmail(email).observe(this, new Observer<User>() {
                @Override
                public void onChanged(User loadedUser) {
                    if (loadedUser != null) {
                        user = loadedUser;
                        Log.d("UserPage", "User loaded: " + user);
                        textViewDisplayName.setText(user.getDisplayName());
                        textViewUserName.setText(user.getFirstName() + " " + user.getLastName());

                        if (user.getVideos() != null) {
                            textViewNumVideos.setText(user.getVideos().size() + " videos");
                        } else {
                            textViewNumVideos.setText("0 videos");
                        }

                        // Load the user's photo
                        if (user.getPhoto() != null && !user.getPhoto().isEmpty()) {
                            Log.d("uri p" , user.getPhoto());
                            String baseUrl = getResources().getString(R.string.BaseUrl);
                            imageViewUserPhoto.setImageURI(Uri.parse(baseUrl + user.getPhoto()));
                        } else {
                            imageViewUserPhoto.setImageResource(R.drawable.dog1); // Use a placeholder image
                        }

                        // Initialize RecyclerView with user videos
                        List<Video> userVideos = user.getVideos() != null ? user.getVideos() : new ArrayList<>();
                        VideoListAdapter adapter = new VideoListAdapter(userVideos, UserPage.this, user);
                        recyclerViewUserVideos.setLayoutManager(new LinearLayoutManager(UserPage.this));
                        recyclerViewUserVideos.setAdapter(adapter);

                        if (userVideos.isEmpty()) {
                            recyclerViewUserVideos.setVisibility(View.GONE);
                            textViewNumVideos.setText("No videos available.");
                        } else {
                            recyclerViewUserVideos.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Log.d("UserPage", "Loaded user is null");
                    }
                }
            });
        } else {
            Log.d("UserPage", "Email is null");
        }

        // Set the button edit listener
        buttonEdit.setOnClickListener(v -> {
            if (user != null) {
                Intent editUserIntent = new Intent(UserPage.this, EditUserActivity.class);
                editUserIntent.putExtra("user_email", user.getEmail());
                startActivityForResult(editUserIntent, 1); // 1 is requestCode for editing user
            } else {
                Log.d("UserPage", "Edit button clicked but user is null");
            }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            String email = data.getStringExtra("updated_user_email");
            if (email != null) {
                usersViewModel.getUserByEmail(email).observe(this, new Observer<User>() {
                    @Override
                    public void onChanged(User updatedUser) {
                        if (updatedUser != null) {
                            usersViewModel.updateUser(updatedUser.getEmail(), updatedUser);
                        }
                    }
                });
            }
        }
    }
}
