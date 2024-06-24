package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.myapplication.entities.user;

public class uploadvideo extends BaseActivity {

    private static final int REQUEST_VIDEO_PICK = 101;
    private VideoView videoView;
    private Uri selectedVideoUri;
    private user loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtil.applyTheme(this);
        setContentView(R.layout.uploadvideo);

        // Get the logged-in user from the intent
        loggedInUser = (user) getIntent().getSerializableExtra("user");

        Button closeButton = findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, homescreen.class);
            intent.putExtra("user", loggedInUser);
            startActivity(intent);
        });

        Button uploadButton = findViewById(R.id.uploadVideoButton);
        uploadButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.custom_red)));
        uploadButton.setOnClickListener(v -> openGallery());

        videoView = findViewById(R.id.videoView);
        videoView.setZOrderOnTop(true);

        Button continueButton = findViewById(R.id.continueButton);
        continueButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.custom_red)));
        continueButton.setOnClickListener(v -> {
            if (selectedVideoUri != null) {
                Log.d("UploadVideo", "Continue button clicked, videoUri: " + selectedVideoUri);
                Intent intent = new Intent(this, detailsofvideo.class);
                intent.putExtra("videoUrl", selectedVideoUri.toString());
                intent.putExtra("user", loggedInUser);
                startActivity(intent);
            } else {
                Log.d("UploadVideo", "No video selected");
                Toast.makeText(this, "Please select a video first", Toast.LENGTH_SHORT).show();
            }
        });

        // Initialize the VideoView
        videoView.setOnPreparedListener(mediaPlayer -> {
            mediaPlayer.setLooping(true);
            videoView.start();
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_VIDEO_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_VIDEO_PICK && resultCode == Activity.RESULT_OK && data != null) {
            selectedVideoUri = data.getData();
            if (selectedVideoUri != null) {
                videoView.setVideoURI(selectedVideoUri);
                videoView.requestFocus();
                videoView.start();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoView.isPlaying()) {
            videoView.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (selectedVideoUri != null) {
            videoView.setVideoURI(selectedVideoUri);
            videoView.start();
        }
    }
}
