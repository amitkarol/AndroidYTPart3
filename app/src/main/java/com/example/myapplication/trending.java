package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.ViewModels.VideosViewModel;
import com.example.myapplication.entities.User;
import com.example.myapplication.entities.Video;
import com.example.myapplication.utils.CurrentUser;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import adapter.VideoListAdapter;

public class trending extends BaseActivity {

    private RecyclerView recyclerView;
    private VideoListAdapter videoAdapter;
    private VideosViewModel viewModel;
    private User loggedInUser;
    private ShapeableImageView imageViewPerson;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trending);

        // Get logged in user
        CurrentUser currentUser = CurrentUser.getInstance();
        loggedInUser = currentUser.getUser().getValue();

        if (loggedInUser == null) {
            Uri person = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.person);
            loggedInUser = new User("Test", "User", "testuser@example.com", "Password@123", "TestUser", person.toString());
        }

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewTrendingVideos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        videoAdapter = new VideoListAdapter(null, this);
        recyclerView.setAdapter(videoAdapter);

        // Initialize ViewModel and observe video list
        viewModel = new ViewModelProvider(this).get(VideosViewModel.class);
        viewModel.getVideos().observe(this, new Observer<List<Video>>() {
            @Override
            public void onChanged(List<Video> videos) {
                videoAdapter.setVideos(videos);
            }
        });

        viewModel.getVideos();

        // Handle bottom navigation
        ImageView imageViewHome = findViewById(R.id.imageViewHome);
        imageViewHome.setOnClickListener(v -> {
            Intent homeIntent = new Intent(trending.this, homescreen.class);
            startActivity(homeIntent);
        });

        ImageView imageViewLightning = findViewById(R.id.imageViewLightning);
        imageViewLightning.setOnClickListener(v -> {
        });

        ImageView buttonUpload = findViewById(R.id.buttonUpload);
        buttonUpload.setOnClickListener(v -> {
            if ("testuser@example.com".equals(loggedInUser.getEmail())) {
                Intent loginIntent = new Intent(trending.this, login.class);
                startActivity(loginIntent);
                Toast.makeText(this, "Please log in to upload a video", Toast.LENGTH_SHORT).show();
            } else {
                Intent uploadIntent = new Intent(trending.this, uploadvideo.class);
                uploadIntent.putExtra("user", loggedInUser);
                startActivity(uploadIntent);
            }
        });

        ImageView imageViewPlay = findViewById(R.id.imageViewPlay);
        imageViewPlay.setOnClickListener(v -> {
            // Navigate to Play screen
        });

        imageViewPerson = findViewById(R.id.imageViewPerson);
        updateUserPhoto();

        imageViewPerson.setOnClickListener(v -> {
            if ("testuser@example.com".equals(loggedInUser.getEmail())) {
                // Redirect to login screen if the test user is connected
                Intent loginIntent = new Intent(trending.this, login.class);
                startActivity(loginIntent);
            } else {
                // Start the logout activity with user details as extras
                Intent logoutIntent = new Intent(trending.this, UserPage.class);
                logoutIntent.putExtra("user_email", loggedInUser.getEmail());
                startActivity(logoutIntent);
            }
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
                new LoadImageTask(imageViewPerson, R.drawable.person).execute(photoUrl);
            } else {
                imageViewPerson.setImageResource(R.drawable.person);
            }
        } catch (Exception e) {
            imageViewPerson.setImageResource(R.drawable.person);
        }
    }

    // AsyncTask to load user photo
    private static class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;
        int placeholderResId;

        public LoadImageTask(ImageView imageView, int placeholderResId) {
            this.imageView = imageView;
            this.placeholderResId = placeholderResId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            imageView.setImageResource(placeholderResId);
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream inputStream = new URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                imageView.setImageBitmap(result);
            } else {
                imageView.setImageResource(placeholderResId);
            }
        }
    }
}
