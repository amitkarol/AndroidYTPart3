package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.myapplication.Api.VideoAPI;
import com.example.myapplication.entities.User;
import com.example.myapplication.ViewModels.VideosViewModel;
import com.example.myapplication.entities.Video;
import com.example.myapplication.utils.CurrentUser;
import com.example.myapplication.utils.ImageLoader;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import adapter.VideoListAdapter;

public class homescreen extends BaseActivity {

    private static final int REQUEST_UPLOAD_VIDEO = 1;
    private static final int REQUEST_WATCH_VIDEO = 2;

    private RecyclerView recyclerView;
    private VideoListAdapter videoAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private User loggedInUser;
    private Switch modeSwitch;
    private RelativeLayout homeScreenLayout;
    private SearchView searchView;
    private VideosViewModel viewModel;
    private ShapeableImageView imageViewPerson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen);

        CurrentUser currentUser = CurrentUser.getInstance();
        // Observe the user LiveData
        currentUser.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                loggedInUser = user;
                // Update UI or perform other actions based on the new loggedInUser
                updateUserPhoto();
            }
        });

        if (loggedInUser == null) {
            Uri person = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.person);
            loggedInUser = new User("Test", "User", "testuser@example.com", "Password@123", "TestUser", person.toString());
        }

        // Display user photo
        imageViewPerson = findViewById(R.id.imageViewPerson);
        updateUserPhoto();

        viewModel = new ViewModelProvider(this).get(VideosViewModel.class);
        VideoAPI videoAPI = new VideoAPI();
        videoAPI.get();

        viewModel.getVideos().observe(this, new Observer<List<Video>>() {
            @Override
            public void onChanged(@Nullable List<Video> videos) {
                videoAdapter.setVideos(videos);
            }
        });

//        viewModel.getVideos().observe(this, videos -> {
//            videoAdapter.setVideos(videos);
//        });

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewVideos);
        videoAdapter = new VideoListAdapter(null, this);
        recyclerView.setAdapter(videoAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set up SwipeRefreshLayout
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(false);
            refreshVideos();
        });

        // Set an OnClickListener to the imageViewPerson
        imageViewPerson.setOnClickListener(v -> {
            if ("testuser@example.com".equals(loggedInUser.getEmail())) {
                // Redirect to login screen if the test user is connected
                Intent loginIntent = new Intent(homescreen.this, login.class);
                startActivity(loginIntent);
            } else {
                // Start the logout activity with user details as extras
                Intent logoutIntent = new Intent(homescreen.this, UserPage.class);
                logoutIntent.putExtra("user_email", loggedInUser.getEmail());
                startActivity(logoutIntent);
            }
        });

        // Click the button to upload video and continue to the page of upload video
        ImageView buttonUpload = findViewById(R.id.buttonUpload);
        buttonUpload.setOnClickListener(v -> {
            if ("testuser@example.com".equals(loggedInUser.getEmail())) {
                // Redirect to login screen if the test user is connected
                Intent loginIntent = new Intent(homescreen.this, login.class);
                startActivity(loginIntent);
                Toast.makeText(this, "Please log in to upload a video", Toast.LENGTH_SHORT).show();
            } else {
                Intent uploadIntent = new Intent(this, uploadvideo.class);
                uploadIntent.putExtra("user", loggedInUser);
                startActivityForResult(uploadIntent, REQUEST_UPLOAD_VIDEO);
            }
        });

        ImageView imageViewLightning = findViewById(R.id.imageViewLightning);
        imageViewLightning.setOnClickListener(v -> {
            Intent trendingIntent = new Intent(homescreen.this, trending.class);
            startActivity(trendingIntent);
        });

        // Initialize UI elements for manual theme change
        homeScreenLayout = findViewById(R.id.homeScreenLayout);

        // Initialize SearchView
        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterVideos(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterVideos(newText);
                return true;
            }
        });

        // Initialize and handle theme change switch
        modeSwitch = findViewById(R.id.switch1);
        SharedPreferences preferences = getSharedPreferences("theme_prefs", MODE_PRIVATE);
        boolean isNightMode = preferences.getBoolean("night_mode", false);
        modeSwitch.setChecked(isNightMode);

        modeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Save the new theme preference
            ThemeUtil.setNightMode(this, isChecked);

            // Recreate the activity to apply the new theme
            recreate();
        });
    }

    private void updateUserPhoto() {
        try {
            if (loggedInUser != null && loggedInUser.getEmail() != null && loggedInUser.getEmail().equals("testuser@example.com")) {
                Uri personUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.person);
                imageViewPerson.setImageURI(personUri);
            } else if (loggedInUser != null && loggedInUser.getPhoto() != null) {
                String baseUrl = getResources().getString(R.string.BaseUrl);
                String photoUrl = baseUrl + loggedInUser.getPhoto();
                new ImageLoader.LoadImageTask(imageViewPerson, R.drawable.person).execute(photoUrl);
            } else {
                imageViewPerson.setImageResource(R.drawable.person);
            }
        } catch (Exception e) {
            Log.e("ImageViewError", "Error setting image", e);
            imageViewPerson.setImageResource(R.drawable.person);
        }
    }

    private void filterVideos(String query) {
        if (videoAdapter != null) {
            Log.d("Filter", "Filtering videos with query: " + query);
            videoAdapter.filter(query);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshVideos();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQUEST_UPLOAD_VIDEO || requestCode == REQUEST_WATCH_VIDEO) && resultCode == RESULT_OK) {
            refreshVideos();
        }
    }

    private void refreshVideos() {
        viewModel.getVideos().observe(this, videos -> {
            videoAdapter.setVideos(videos);
        });
    }


}
