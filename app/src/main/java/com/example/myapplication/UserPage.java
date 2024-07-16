package com.example.myapplication;

import android.content.Intent;

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

import com.example.myapplication.ViewModels.VideosViewModel;
import com.example.myapplication.entities.User;
import com.example.myapplication.entities.Video;
import com.example.myapplication.ViewModels.UsersViewModel;
import com.example.myapplication.utils.CurrentUser;
import com.example.myapplication.utils.ImageLoader;

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
    private Button buttonLogout;
    private UsersViewModel usersViewModel;
    private VideosViewModel videosViewModel;
    private User user;
    private List<Video> userVideos;
    private ImageView imageViewPerson;

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
        buttonLogout = findViewById(R.id.buttonLogout);
        imageViewPerson = findViewById(R.id.imageViewPerson);

        usersViewModel = new ViewModelProvider(this).get(UsersViewModel.class);
        videosViewModel = new ViewModelProvider(this).get(VideosViewModel.class);

        userVideos = new ArrayList<>();

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
                        updateUserInfo();


                        // Check the condition and set the visibility of buttonEdit and buttonLogout
                        if (CurrentUser.getInstance().getUser().getValue() != null &&
                                CurrentUser.getInstance().getUser().getValue().getEmail() != null &&
                                CurrentUser.getInstance().getUser().getValue().getEmail().equals(user.getEmail())) {
                            buttonEdit.setVisibility(View.VISIBLE); // Show the button
                            buttonLogout.setVisibility(View.VISIBLE); // Show the button
                        } else {
                            buttonEdit.setVisibility(View.GONE); // Hide the button
                            buttonLogout.setVisibility(View.GONE); // Hide the button
                        }

                        observeUserVideos(user.getEmail());

                    } else {
                        Log.d("UserPage", "Loaded user is null");
                    }
                }
            });
        } else {
            Log.d("UserPage", "Email is null");
        }

        // check if the logged user is the same user of this page

        buttonEdit.setOnClickListener(v -> {
            if (user != null) {
                Intent editUserIntent = new Intent(UserPage.this, EditUserActivity.class);
                editUserIntent.putExtra("user_email", user.getEmail());
                startActivityForResult(editUserIntent, 1);
            } else {
                Log.d("UserPage", "Edit button clicked but user is null");
            }
        });

        buttonLogout.setOnClickListener(v -> {
            if (CurrentUser.getInstance().getUser().getValue() == null || CurrentUser.getInstance().getUser().getValue().getEmail().equals("test@gmail.com")) {
                // If no user is logged in, redirect to login activity
                Intent loginIntent = new Intent(UserPage.this, login.class);
                startActivity(loginIntent);
            } else {
                // Logout the user
                CurrentUser.getInstance().setUser(null);
                CurrentUser.getInstance().setToken(null);
                Intent logoutIntent = new Intent(UserPage.this, homescreen.class);
                startActivity(logoutIntent);
                finish();
            }
        });
        // Initialize Bottom Navigation (example, you can add listeners as needed)
        ImageView imageViewHome = findViewById(R.id.imageViewHome);
        imageViewHome.setOnClickListener(v -> {
            Intent homeIntent = new Intent(UserPage.this, homescreen.class);
            startActivity(homeIntent);
        });

        ImageView imageViewLightning = findViewById(R.id.imageViewLightning);
        imageViewLightning.setOnClickListener(v -> {
            Intent trendingIntent = new Intent(UserPage.this, trending.class);
            startActivity(trendingIntent);
        });

        ImageView buttonUpload = findViewById(R.id.buttonUpload);
        buttonUpload.setOnClickListener(v -> {
            Intent uploadIntent = new Intent(UserPage.this, uploadvideo.class);
            startActivity(uploadIntent);
        });

        ImageView imageViewPlay = findViewById(R.id.imageViewPlay);
        imageViewPlay.setOnClickListener(v -> {
            // Navigate to Play screen
        });

        ImageView imageViewPerson = findViewById(R.id.imageViewPerson);
        imageViewPerson.setOnClickListener(v -> {
            if (CurrentUser.getInstance().getUser().getValue() == null )
            {
                    Intent loginIntent = new Intent(UserPage.this,login.class);
                    startActivity(loginIntent);
            } else if ( CurrentUser.getInstance().getUser().getValue().getEmail().equals("test@gmail.com")) {
                Intent loginIntent = new Intent(UserPage.this,login.class);
                startActivity(loginIntent);
            } else
                if (!CurrentUser.getInstance().getUser().getValue().getEmail().equals(user.getEmail())) {
                Intent logoutIntent = new Intent(UserPage.this, UserPage.class);
                logoutIntent.putExtra("user_email", CurrentUser.getInstance().getUser().getValue().getEmail());
                startActivity(logoutIntent);
            }
        });

        // Load current user photo for bottom navigation
        loadCurrentUserPhoto();

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
                            user = updatedUser;
                            Log.d("UserPage", "User updated: " + user);
                            updateUserInfo();
                        }
                    }
                });
            }
        }
    }

    private void updateUserInfo() {
        textViewDisplayName.setText(user.getDisplayName());
        textViewUserName.setText(user.getFirstName() + " " + user.getLastName());

        if (userVideos != null) {
            textViewNumVideos.setText(userVideos.size() + " videos");
        } else {
            textViewNumVideos.setText("0 videos");
        }

        // Load the user's photo
        if (user.getPhoto() != null && !user.getPhoto().isEmpty()) {
            String baseUrl = getResources().getString(R.string.BaseUrl);
            Log.d("uri p", baseUrl + "/" + user.getPhoto());
            new ImageLoader.LoadImageTask(imageViewUserPhoto ,R.drawable.dog3).execute(baseUrl + user.getPhoto());
        } else {
            imageViewUserPhoto.setImageResource(R.drawable.dog3); // Use a placeholder image
        }

        // Initialize RecyclerView with user videos
        VideoListAdapter adapter = new VideoListAdapter(userVideos, UserPage.this);
        recyclerViewUserVideos.setLayoutManager(new LinearLayoutManager(UserPage.this));
        recyclerViewUserVideos.setAdapter(adapter);

        if (userVideos.isEmpty()) {
            recyclerViewUserVideos.setVisibility(View.GONE);
            textViewNumVideos.setText("No videos available.");
        } else {
            recyclerViewUserVideos.setVisibility(View.VISIBLE);
        }
    }

    private void observeUserVideos(String userEmail) {
        videosViewModel.getUserVideos(userEmail).observe(this, new Observer<List<Video>>() {
            @Override
            public void onChanged(List<Video> videos) {
                if (videos != null) {
                    userVideos = videos;
                    Log.d("user videos", "videos: " + userVideos);
                    updateUserInfo();
                }
            }
        });
    }

    private void loadCurrentUserPhoto() {
        User currentUser = CurrentUser.getInstance().getUser().getValue();
        if (currentUser != null && currentUser.getPhoto() != null && !currentUser.getPhoto().isEmpty()) {
            String baseUrl = getResources().getString(R.string.BaseUrl);
            new ImageLoader.LoadImageTask(imageViewPerson, R.drawable.dog3).execute(baseUrl + currentUser.getPhoto());
        } else {
            imageViewPerson.setImageResource(R.drawable.person); // Use a placeholder image
        }
    }


}
