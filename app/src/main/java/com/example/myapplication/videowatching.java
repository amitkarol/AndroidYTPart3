package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.VideoView;
import com.google.android.material.imageview.ShapeableImageView;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.FragmentActivity;
import com.example.myapplication.Fragments.ShareFragment;
import com.example.myapplication.entities.VideoManager;
import com.example.myapplication.entities.video;
import com.example.myapplication.entities.user;
import com.example.myapplication.Fragments.Comments;

public class videowatching extends FragmentActivity {

    private TextView titleTextView;
    private TextView descriptionTextView;
    private TextView channelTextView;
    private TextView viewCountTextView;
    private VideoView videoView;
    private Button likeButton;
    private Button shareButton;
    private Button commentsButton;
    private Button editButton;
    private ImageButton pauseResumeButton;
    private video currentVideo;
    private user loggedInUser;
    private GestureDetectorCompat gestureDetector;
    private ShapeableImageView userPhotoImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.videowatching);

        // Initialize GestureDetector
        gestureDetector = new GestureDetectorCompat(this, new GestureListener());

        // Initialize views
        titleTextView = findViewById(R.id.titleTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        channelTextView = findViewById(R.id.channelTextView);
        viewCountTextView = findViewById(R.id.viewCountTextView);
        videoView = findViewById(R.id.videoView);
        likeButton = findViewById(R.id.likeButton);
        shareButton = findViewById(R.id.shareButton);
        commentsButton = findViewById(R.id.commentsButton);
        editButton = findViewById(R.id.editButton);
        pauseResumeButton = findViewById(R.id.pauseResumeButton);
        userPhotoImageView = findViewById(R.id.userPhotoImageView);

        // Apply theme to all relevant views
        applyThemeToViews();

        // Get the data passed from homescreen activity
        Intent intent = getIntent();
        if (intent != null) {
            Uri data = intent.getData();
            if (data != null) {
                // Handle deep link
                String videoId = data.getQueryParameter("id");
                currentVideo = getVideoById(videoId);
            } else {
                // Handle normal intent
                String title = intent.getStringExtra("title");
                currentVideo = getVideoByTitle(title);
            }

            loggedInUser = (user) intent.getSerializableExtra("user");

            if (currentVideo != null) {
                // Set data to views
                titleTextView.setText(currentVideo.getTitle());
                descriptionTextView.setText(currentVideo.getDescription());
                viewCountTextView.setText("Views " + currentVideo.getViewCount());
                channelTextView.setText(currentVideo.getUser().getEmail());
                videoView.setVideoPath(currentVideo.getVideoUrl());
                videoView.start();

                // Set user photo
                Uri photoUri = Uri.parse(currentVideo.getUser().getPhotoUri());
                userPhotoImageView.setImageURI(photoUri);

                // Update view count
                currentVideo.incrementViewCount();
                viewCountTextView.setText("Views " + currentVideo.getViewCount());
                updateLikeButton();
            }
        }

        // Like button listener
        likeButton.setOnClickListener(v -> {
            if (loggedInUser.getEmail().equals("testuser@example.com")) {
                redirectToLogin();
                return;
            }
            if (currentVideo != null && loggedInUser != null) {
                if (currentVideo.hasLiked(loggedInUser.getEmail())) {
                    currentVideo.unlikeVideo(loggedInUser.getEmail());
                } else {
                    currentVideo.likeVideo(loggedInUser.getEmail());
                }
                updateLikeButton();
            }
        });

        // Edit button listener
        editButton.setOnClickListener(v -> {
            if (loggedInUser.getEmail().equals("testuser@example.com")) {
                redirectToLogin();
                return;
            }
            if (currentVideo != null) {
                Intent editIntent = new Intent(videowatching.this, EditVideoActivity.class);
                editIntent.putExtra("video", currentVideo);
                editIntent.putExtra("user", loggedInUser);
                startActivity(editIntent);
            }
        });

        // Pause/Resume button listener
        pauseResumeButton.setOnClickListener(v -> {
            if (videoView.isPlaying()) {
                videoView.pause();
                pauseResumeButton.setImageResource(R.drawable.play); // Use the resume drawable
            } else {
                videoView.start();
                pauseResumeButton.setImageResource(R.drawable.pause); // Use the pause drawable
            }
        });

        // Comments button listener
        commentsButton.setOnClickListener(v -> {
            Comments commentsFragment = new Comments(currentVideo, loggedInUser);
            commentsFragment.show(getSupportFragmentManager(), "CommentsFragment");
        });

        // Share button listener
        shareButton.setOnClickListener(v -> {
            if (currentVideo != null) {
                ShareFragment shareFragment = new ShareFragment(currentVideo);
                shareFragment.show(getSupportFragmentManager(), "ShareFragment");
            }
        });
    }

    private void applyThemeToViews() {
        int textColor = ThemeUtil.isNightMode(this) ? getResources().getColor(R.color.white) : getResources().getColor(R.color.black);

        titleTextView.setTextColor(textColor);
        descriptionTextView.setTextColor(textColor);
        channelTextView.setTextColor(textColor);
        viewCountTextView.setTextColor(textColor);
        likeButton.setTextColor(textColor);
        shareButton.setTextColor(textColor);
        commentsButton.setTextColor(textColor);
        editButton.setTextColor(textColor);
        pauseResumeButton.setColorFilter(textColor); // Adjust color filter for the ImageButton
    }

    private void updateLikeButton() {
        if (currentVideo != null && loggedInUser != null) {
            boolean hasLiked = currentVideo.hasLiked(loggedInUser.getEmail());
            int likeCount = currentVideo.getLikeCount();
            likeButton.setText(likeCount + " likes");
            likeButton.setCompoundDrawablesWithIntrinsicBounds(hasLiked ? R.drawable.unlike : R.drawable.like, 0, 0, 0);
        }
    }

    private video getVideoById(String id) {
        for (video video : VideoManager.getInstance().getVideoList()) {
            if (video.getId().equals(id)) {
                return video;
            }
        }
        return null;
    }

    private video getVideoByTitle(String title) {
        for (video video : VideoManager.getInstance().getVideoList()) {
            if (video.getTitle().equals(title)) {
                return video;
            }
        }
        return null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float diffY = e2.getY() - e1.getY();
            if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffY > 0) {
                    onSwipeDown();
                }
                return true;
            }
            return false;
        }
    }

    private void onSwipeDown() {
        // Go back to the previous activity
        finish();
    }

    private void redirectToLogin() {
        Intent loginIntent = new Intent(videowatching.this, login.class);
        startActivity(loginIntent);
    }
}
