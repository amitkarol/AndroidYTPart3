package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.ViewModelProvider;


import com.example.myapplication.ViewModels.UsersViewModel;
import com.example.myapplication.ViewModels.VideosViewModel;
import com.example.myapplication.entities.User;
import com.example.myapplication.entities.Video;
import com.example.myapplication.entities.VideoManager;

public class detailsofvideo extends BaseActivity {

    private EditText editTextTitle;
    private EditText editTextDescription;
    private Button buttonSaveDetails;
    private Button buttonPickThumbnail;
    private ImageView imageViewThumbnail;

    private Video selectedVideo;
    private User user;
    private Uri selectedImageUri;
    
    private VideosViewModel viewModel;

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    imageViewThumbnail.setImageURI(selectedImageUri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtil.applyTheme(this);
        setContentView(R.layout.detailsofvideo);

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        buttonSaveDetails = findViewById(R.id.buttonSaveDetails);
        buttonPickThumbnail = findViewById(R.id.buttonPickThumbnail);
        imageViewThumbnail = findViewById(R.id.imageViewThumbnail);

        // Retrieve the video URL and user details from the Intent
        Intent intent = getIntent();
        String videoUrl = intent.getStringExtra("videoUrl");
        user = (User) intent.getSerializableExtra("user");

        selectedVideo = new Video( "", "", "", 0, videoUrl, user.getEmail(), 0, 0);

        buttonPickThumbnail.setOnClickListener(v -> {
            Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageLauncher.launch(pickPhoto);
        });

        buttonSaveDetails.setOnClickListener(v -> {
            // Save video details
            String title = editTextTitle.getText().toString();
            String description = editTextDescription.getText().toString();

            if (title.isEmpty() || description.isEmpty() || selectedImageUri == null) {
                Toast.makeText(this, "Please enter title, description, and select a thumbnail", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d("test3", "detailsInfo user: " + user);
            selectedVideo.setTitle(title);
            selectedVideo.setDescription(description);
            selectedVideo.setImg(selectedImageUri.toString());

            // 
            viewModel = new ViewModelProvider(this).get(VideosViewModel.class);
            viewModel.createVideo(title, description, selectedImageUri.toString(), selectedVideo.toString(), user.getEmail());
            
            // Add the video to VideoManager
            VideoManager.getInstance().addVideo(selectedVideo);

            // Save the video details and go back to the home screen
            Intent homeIntent = new Intent(detailsofvideo.this, homescreen.class);
            homeIntent.putExtra("user", user);
            startActivity(homeIntent);
            finish();
        });
    }
}