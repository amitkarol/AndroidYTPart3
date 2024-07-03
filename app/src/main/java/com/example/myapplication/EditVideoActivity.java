package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication.entities.VideoManager;
import com.example.myapplication.entities.video;
import com.example.myapplication.entities.user;

public class EditVideoActivity extends BaseActivity {
    private static final int REQUEST_IMAGE_PICK = 1;

    private EditText titleEditText;
    private EditText descriptionEditText;
    private ImageView thumbnailImageView;
    private video currentVideo;
    private user loggedInUser;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtil.applyTheme(this);
        setContentView(R.layout.edit_video);

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
        currentVideo = (video) intent.getSerializableExtra("video");
        loggedInUser = (user) intent.getSerializableExtra("user");
        video originVideo = new video(currentVideo);

        // Populate fields if data is available
        if (currentVideo != null) {
            titleEditText.setText(currentVideo.getTitle());
            descriptionEditText.setText(currentVideo.getDescription());
            if (currentVideo.getThumbnailUrl() != null) {
                thumbnailImageView.setImageURI(Uri.parse(currentVideo.getThumbnailUrl()));
            } else {
                thumbnailImageView.setImageResource(R.drawable.placeholder_thumbnail);
            }
        }

        // Set the save button listener
        saveButton.setOnClickListener(v -> {
            // Show confirmation dialog
            new AlertDialog.Builder(this)
                    .setTitle("Confirm Save")
                    .setMessage("Are you sure you want to save the changes?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        // Get the new title and description from the fields
                        String newTitle = titleEditText.getText().toString().trim();
                        String newDescription = descriptionEditText.getText().toString().trim();

                        // Update the video object
                        currentVideo.setTitle(newTitle);
                        currentVideo.setDescription(newDescription);
                        if (selectedImageUri != null) {
                            currentVideo.setThumbnailUrl(selectedImageUri.toString());
                        }

                        // Update the video in VideoManager
                        VideoManager.getInstance().updateVideo(currentVideo, originVideo);

                        // Show a confirmation message
                        Toast.makeText(this, "Video updated", Toast.LENGTH_SHORT).show();

                        // Navigate back to the homescreen
                        Intent homeIntent = new Intent(this, homescreen.class);
                        homeIntent.putExtra("user", loggedInUser);
                        startActivity(homeIntent);
                        finish();
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
                        // Remove the video from VideoManager
                        VideoManager.getInstance().removeVideo(currentVideo);

                        // Show a confirmation message
                        Toast.makeText(this, "Video deleted", Toast.LENGTH_SHORT).show();

                        // Navigate back to the homescreen
                        Intent homeIntent = new Intent(this, homescreen.class);
                        homeIntent.putExtra("user", loggedInUser);
                        startActivity(homeIntent);
                        finish();
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
            Toast.makeText(this, "New thumbnail selected", Toast.LENGTH_SHORT).show();
        }
    }
}
