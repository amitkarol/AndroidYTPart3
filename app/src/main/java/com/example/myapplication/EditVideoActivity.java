package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.ViewModels.VideosViewModel;
import com.example.myapplication.entities.Video;
import com.example.myapplication.entities.User;
import com.example.myapplication.entities.Video;
import com.example.myapplication.utils.ImageLoader;

import java.io.InputStream;
import java.net.URL;

public class EditVideoActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_PICK = 1;

    private EditText titleEditText;
    private EditText descriptionEditText;
    private ImageView thumbnailImageView;
    private Video currentVideo;
    private User loggedInUser;
    private Uri selectedImageUri;
    private VideosViewModel viewModel;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtil.applyTheme(this);
        setContentView(R.layout.edit_video);
        viewModel = new ViewModelProvider(this).get(VideosViewModel.class);

        // Initialize views
        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        thumbnailImageView = findViewById(R.id.thumbnailImageView);
        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.custom_red)));
        Button deleteButton = findViewById(R.id.deleteButton);
        Button pickThumbnailButton = findViewById(R.id.pickThumbnailButton);

        // Retrieve data from the intent
        Intent intent = getIntent();
        currentVideo = (Video) intent.getSerializableExtra("video");
        loggedInUser = (User) intent.getSerializableExtra("user");

        // Populate fields if data is available
        if (currentVideo != null) {
            titleEditText.setText(currentVideo.getTitle());
            descriptionEditText.setText(currentVideo.getDescription());
            String baseUrl = getResources().getString(R.string.BaseUrl);
            String imageUrl = baseUrl + currentVideo.getImg();
            Log.d("imageofvideo", imageUrl);
            if (currentVideo.getImg() != null) {
                new ImageLoader.LoadImageTask(thumbnailImageView, R.drawable.placeholder_thumbnail).execute(imageUrl);
            } else {
                thumbnailImageView.setImageResource(R.drawable.placeholder_thumbnail);
            }
        }

        // Set the save button listener
        // In EditVideoActivity.java
        saveButton.setOnClickListener(v -> {
            // Show confirmation dialog
            new AlertDialog.Builder(this)
                    .setTitle("Confirm Save")
                    .setMessage("Are you sure you want to save the changes?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        // Get the new title and description from the fields
                        String newTitle = titleEditText.getText().toString().trim();
                        String newDescription = descriptionEditText.getText().toString().trim();

                        // Update the video
                        viewModel.editVideo(currentVideo.get_id(), newTitle, newDescription,
                                selectedImageUri, loggedInUser.getEmail(), this, () -> {
                                    // Show a confirmation message on the main thread
                                    runOnUiThread(() -> Toast.makeText(this, "Video updated", Toast.LENGTH_SHORT).show());
                                    Log.d("editVideo", "Video updated successfully");

                                    // Navigate back to the homescreen
                                    Intent homeIntent = new Intent(EditVideoActivity.this, homescreen.class);
                                    homeIntent.putExtra("user", loggedInUser);
                                    homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    startActivity(homeIntent);
                                    finish();
                                });
                        Log.d("editVideo", "Edit video method called");
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        });

        // Set the delete button listener
        deleteButton.setOnClickListener(v -> {
            // Show confirmation dialog for deletion
            new AlertDialog.Builder(this)
                    .setTitle("Confirm Delete")
                    .setMessage("Are you sure you want to delete this video?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        viewModel.deleteVideo(currentVideo, () -> {
                            // Show a confirmation message on the main thread
                            runOnUiThread(() -> Toast.makeText(this, "Video deleted", Toast.LENGTH_SHORT).show());

                            // Navigate back to the homescreen
                            Intent homeIntent = new Intent(EditVideoActivity.this, homescreen.class);
                            Log.d("edittest", "1111");

                            homeIntent.putExtra("user", loggedInUser);
                            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(homeIntent);
                            finish();
                        });
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        });

        // Set the pick thumbnail button listener
        pickThumbnailButton.setOnClickListener(v -> {
            Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto, REQUEST_IMAGE_PICK);
        });

        // Request focus and show keyboard when EditText is clicked
        View.OnFocusChangeListener onFocusChangeListener = (v, hasFocus) -> {
            if (hasFocus) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
            }
        };

        titleEditText.setOnFocusChangeListener(onFocusChangeListener);
        descriptionEditText.setOnFocusChangeListener(onFocusChangeListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            thumbnailImageView.setImageURI(selectedImageUri);
            runOnUiThread(() -> Toast.makeText(this, "New thumbnail selected", Toast.LENGTH_SHORT).show());
        }
    }
}
