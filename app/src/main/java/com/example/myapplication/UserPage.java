package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
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
import com.example.myapplication.utils.CurrentUser;

import java.io.InputStream;
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
    private User user;
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
                startActivityForResult(editUserIntent, 1); // 1 is requestCode for editing user
            } else {
                Log.d("UserPage", "Edit button clicked but user is null");
            }
        });

        // Set the button logout listener
        buttonLogout.setOnClickListener(v -> {
            Uri person = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.person);
            User loggedInUser = new User("Test", "User", "testuser@example.com", "Password@123", "TestUser", person.toString());
            CurrentUser.getInstance().setUser(loggedInUser);
            CurrentUser.getInstance().setToken(null);
            Intent logoutIntent = new Intent(UserPage.this, homescreen.class);
            startActivity(logoutIntent);
            finish();
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
            Intent trendingIntent = new Intent(UserPage.this , trending.class);
            startActivity(trendingIntent);
         });

        ImageView buttonUpload = findViewById(R.id.buttonUpload);
        buttonUpload.setOnClickListener(v -> {
            // Navigate to Upload screen
            Intent uploadIntent = new Intent(UserPage.this, uploadvideo.class);
            startActivity(uploadIntent);
        });

        ImageView imageViewPlay = findViewById(R.id.imageViewPlay);
        imageViewPlay.setOnClickListener(v -> {
            // Navigate to Play screen
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

        if (user.getVideos() != null) {
            textViewNumVideos.setText(user.getVideos().size() + " videos");
        } else {
            textViewNumVideos.setText("0 videos");
        }

        // Load the user's photo
        if (user.getPhoto() != null && !user.getPhoto().isEmpty()) {
            String baseUrl = getResources().getString(R.string.BaseUrl);
            Log.d("uri p", baseUrl + "/" + user.getPhoto());
            new LoadImageTask(imageViewUserPhoto).execute(baseUrl + user.getPhoto());
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
    }

    private void loadCurrentUserPhoto() {
        User currentUser = CurrentUser.getInstance().getUser().getValue();
        if (currentUser != null && currentUser.getPhoto() != null && !currentUser.getPhoto().isEmpty()) {
            String baseUrl = getResources().getString(R.string.BaseUrl);
            new LoadImageTask(imageViewPerson).execute(baseUrl + currentUser.getPhoto());
        } else {
            imageViewPerson.setImageResource(R.drawable.person); // Use a placeholder image
        }
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
                bitmap = rotateBitmap(bitmap, 90); // Rotate the bitmap by 90 degrees
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

        private Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
            if (bitmap != null) {
                Matrix matrix = new Matrix();
                matrix.postRotate(degrees);
                return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            }
            return bitmap;
        }
    }
}
