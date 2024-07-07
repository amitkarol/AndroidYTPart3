package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.myapplication.Api.VideoAPI;
import com.example.myapplication.Daos.VideoDao;
import com.example.myapplication.db.AppDB;
import com.example.myapplication.entities.User;
import com.example.myapplication.ViewModels.VideosViewModel;

import adapter.VideoListAdapter;

public class homescreen extends AppCompatActivity {

    private RecyclerView recyclerView;
    private VideoListAdapter videoAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private User loggedInUser;
    private Switch modeSwitch;
    private RelativeLayout homeScreenLayout;
    private SearchView searchView;

    private VideosViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen);

        loggedInUser = (User) getIntent().getSerializableExtra("user");
        if (loggedInUser == null) {
            Uri person = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.person);
            loggedInUser = new User("Test", "User", "testuser@example.com", "Password@123", "TestUser", person.toString());
        }

        // Display user photo
        ImageView imageViewPerson = findViewById(R.id.imageViewPerson);
        if (loggedInUser.getPhotoUri() != null) {
            imageViewPerson.setImageURI(Uri.parse(loggedInUser.getPhotoUri()));
        }

        viewModel = new ViewModelProvider(this).get(VideosViewModel.class);
        AppDB db = Room.databaseBuilder(getApplicationContext(), AppDB.class, "Videos")
                .build();
        VideoDao videoDao = db.videoDao();
        VideoAPI videoAPI = new VideoAPI();
        videoAPI.get();

        viewModel.getVideos().observe(this, videos -> {
            videoAdapter.setVideos(videos);
        });

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewVideos);
        videoAdapter = new VideoListAdapter(null, this, loggedInUser);
        recyclerView.setAdapter(videoAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set up SwipeRefreshLayout
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(false);
        });

        // Set an OnClickListener to the imageViewPerson
        imageViewPerson.setOnClickListener(v -> {
            if ("testuser@example.com".equals(loggedInUser.getEmail())) {
                // Redirect to login screen if the test user is connected
                Intent loginIntent = new Intent(homescreen.this, login.class);
                startActivity(loginIntent);
            } else {
                // Start the logout activity with user details as extras
                Intent logoutIntent = new Intent(homescreen.this, logout.class);
                logoutIntent.putExtra("user", loggedInUser);
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
                startActivity(uploadIntent);
            }
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
            SharedPreferences.Editor editor = getSharedPreferences("theme_prefs", MODE_PRIVATE).edit();
            editor.putBoolean("night_mode", isChecked);
            editor.apply();

            // Recreate the activity to apply the new theme
            recreate();
        });
    }


    private void filterVideos(String query) {
        if (videoAdapter != null) {
            videoAdapter.filter(query);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
