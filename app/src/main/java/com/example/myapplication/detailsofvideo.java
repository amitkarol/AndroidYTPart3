package com.example.myapplication;
import android.Manifest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.ViewModels.VideosViewModel;
import com.example.myapplication.entities.User;
import com.example.myapplication.entities.Video;
import com.example.myapplication.utils.CurrentUser;

public class detailsofvideo extends BaseActivity {

    private static final int REQUEST_PERMISSIONS = 1;
    private static final int REQUEST_MANAGE_EXTERNAL_STORAGE = 2;

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

        // Request necessary permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_MANAGE_EXTERNAL_STORAGE);
            }
        }

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        buttonSaveDetails = findViewById(R.id.buttonSaveDetails);
        buttonPickThumbnail = findViewById(R.id.buttonPickThumbnail);
        imageViewThumbnail = findViewById(R.id.imageViewThumbnail);

        // Retrieve the video URL and user details from the Intent
        Intent intent = getIntent();
        String videoUrl = intent.getStringExtra("videoUrl");
        user = (User) intent.getSerializableExtra("user");

        selectedVideo = new Video("", "", "", 0, videoUrl, user.getEmail(), 0, 0);

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

            selectedVideo.setTitle(title);
            selectedVideo.setDescription(description);
            selectedVideo.setImg(selectedImageUri.toString());

            // Initialize ViewModel and create the video
            viewModel = new ViewModelProvider(this).get(VideosViewModel.class);
            String token = "bearer " + CurrentUser.getInstance().getToken().getValue();
            viewModel.createVideo(user.getEmail(), title, description, selectedImageUri, Uri.parse(videoUrl), this, () -> {
                // Handle success
                Intent homeIntent = new Intent(detailsofvideo.this, homescreen.class);
                homeIntent.putExtra("user", user);
                startActivity(homeIntent);
                finish();
            });
        });
    }

    // Handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, continue with your action
            } else {
                Toast.makeText(this, "Permissions are required to use this feature", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MANAGE_EXTERNAL_STORAGE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // Permission granted, continue with your action
                } else {
                    Toast.makeText(this, "Manage External Storage permission is required to use this feature", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
