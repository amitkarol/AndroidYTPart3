package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.ViewModels.UsersViewModel;
import com.example.myapplication.ViewModels.VideosViewModel;
import com.example.myapplication.entities.User;
import com.example.myapplication.entities.Video;
import com.google.android.material.imageview.ShapeableImageView;
import com.example.myapplication.Fragments.ShareFragment;
import com.example.myapplication.Fragments.Comments;

import java.io.InputStream;

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
    private Video currentVideo;
    private User loggedInUser;
    private UsersViewModel userViewModel;
    private VideosViewModel videoViewModel;
    private GestureDetectorCompat gestureDetector;
    private ShapeableImageView userPhotoImageView;
    private Boolean hasLiked;
    private int likes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.videowatching);
        videoViewModel = new ViewModelProvider(this).get(VideosViewModel.class);

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
            currentVideo = (Video) intent.getSerializableExtra("video");
            loggedInUser = (User) intent.getSerializableExtra("user");
            hasLiked = videoViewModel.isLiked(currentVideo.getOwner(), currentVideo.get_id());
            likes = currentVideo.getLikes();

            if (currentVideo != null) {
                userViewModel = new ViewModelProvider(this).get(UsersViewModel.class);
                userViewModel.getUserByEmail(currentVideo.getOwner()).observe(this, new Observer<User>() {
                    @Override
                    public void onChanged(User owner) {
                        if (owner != null) {
                            // Update the UI with the owner's information
                            channelTextView.setText(owner.getEmail());
                            String photoUriString = owner.getPhoto();
                            Log.d("test5", "video photo " + photoUriString);
                            if (photoUriString != null) {
                                String baseUrl = getResources().getString(R.string.BaseUrl);
                                new LoadImageTask(userPhotoImageView).execute(baseUrl + "/" + photoUriString);
                            } else {
                                userPhotoImageView.setImageResource(R.drawable.dog1);
                            }
                        } else {
                            Log.d("test5", "Owner not found");
                        }
                    }
                });

                // Set data to views
                titleTextView.setText(currentVideo.getTitle());
                descriptionTextView.setText(currentVideo.getDescription());
                viewCountTextView.setText("Views " + (currentVideo.getViews() + 1));

                String videoUrl = getResources().getString(R.string.BaseUrl) + currentVideo.getVideo();
                Log.d("test5", videoUrl);
                Log.d("videowatching", videoUrl);
                videoView.setVideoPath(videoUrl);
                videoView.start();

                // Update view count
                videoViewModel.updateViews(currentVideo.getOwner(), currentVideo.get_id());
                updateLikeButton();
            }
        }

        // Like button listener
        likeButton.setOnClickListener(v -> {
            if (loggedInUser == null || loggedInUser.getEmail().equals("testuser@example.com")) {
                redirectToLogin();
                return;
            }
            if (currentVideo != null && loggedInUser != null) {
                videoViewModel.setLikes(currentVideo.getOwner(), currentVideo.get_id(), loggedInUser.getEmail());
                updateLikeButton();
            }
        });

        // Edit button listener
        editButton.setOnClickListener(v -> {

            if (loggedInUser == null || loggedInUser.getEmail().equals("testuser@example.com")) {
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
                pauseResumeButton.setImageResource(R.drawable.play);
            } else {
                videoView.start();
                pauseResumeButton.setImageResource(R.drawable.pause);
            }
        });

        // Comments button listener
        commentsButton.setOnClickListener(v -> {
            Comments commentsFragment = new Comments(currentVideo);
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
        Log.d("like", "in function");
        if (currentVideo != null && loggedInUser != null) {
            likeButton.setText(hasLiked ? likes-- + " likes " : likes++ + " likes ");
            likeButton.setCompoundDrawablesWithIntrinsicBounds(hasLiked ? R.drawable.unlike : R.drawable.like, 0, 0, 0);
            hasLiked = !hasLiked;
        }
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

    private static class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView;

        public LoadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream input = new java.net.URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(input);
                input.close();
            } catch (Exception e) {
                Log.e("LoadImageTask", "Error loading image", e);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                imageView.setImageBitmap(result);
            } else {
                imageView.setImageResource(R.drawable.dog1); // Use a placeholder image
            }
        }
    }
}
