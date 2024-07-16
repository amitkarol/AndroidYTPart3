package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
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

import com.example.myapplication.Api.UserAPI;
import com.example.myapplication.ViewModels.UsersViewModel;

import java.io.ByteArrayOutputStream;

public class Displayname extends BaseActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private EditText displayNameEdittext;
    private Uri selectedImageUri;
    private UsersViewModel viewModel;
    private ImageView imageViewPhoto;

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    imageViewPhoto.setImageURI(selectedImageUri);
                    imageViewPhoto.setVisibility(View.VISIBLE);
                }
            }
    );

    private final ActivityResultLauncher<Intent> captureImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Bitmap photo = (Bitmap) result.getData().getExtras().get("data");
                    imageViewPhoto.setImageBitmap(photo);
                    imageViewPhoto.setVisibility(View.VISIBLE);
                    selectedImageUri = getImageUri(this, photo);
                    Log.d("test5", "selected image down: " + selectedImageUri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtil.applyTheme(this);
        setContentView(R.layout.displayname);

        displayNameEdittext = findViewById(R.id.editTextText2);
        imageViewPhoto = findViewById(R.id.imageViewPhoto);

        Button continueButton = findViewById(R.id.button3);
        continueButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.custom_red)));

        continueButton.setOnClickListener(v -> {
            String displayName = displayNameEdittext.getText().toString().trim();

            if (displayName.isEmpty()) {
                Toast.makeText(Displayname.this, "Please enter a display name", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedImageUri == null) {
                Log.d("test5", "selected image is null");
                Toast.makeText(Displayname.this, "Please upload a photo", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = getIntent();
            String email = intent.getStringExtra("email");
            String password = intent.getStringExtra("password");
            String firstName = intent.getStringExtra("firstName");
            String lastName = intent.getStringExtra("lastName");

            Log.d("test5", "email: " + email);
            Log.d("test5", "user photo displayname: " + selectedImageUri);
            Log.d("test5", "user photo displayname: " + selectedImageUri.toString());
            viewModel = new ViewModelProvider(this).get(UsersViewModel.class);
            viewModel.createUser(firstName, lastName, email, password, displayName, this, selectedImageUri);
//            UserAPI userAPI = new UserAPI();
//            userAPI.createUser(firstName, lastName, email, password, displayName, this, selectedImageUri);

            Intent homescreenIntent = new Intent(Displayname.this, homescreen.class);
            startActivity(homescreenIntent);
        });

        Button buttonUploadPhoto = findViewById(R.id.buttonUploadPhoto);
        buttonUploadPhoto.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.custom_red)));

        buttonUploadPhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageLauncher.launch(intent);
        });

        Button buttonTakePhoto = findViewById(R.id.buttonTakePhoto);
        buttonTakePhoto.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.custom_red)));

        buttonTakePhoto.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(Displayname.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(Displayname.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Displayname.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
            } else {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                captureImageLauncher.launch(intent);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                captureImageLauncher.launch(intent);
            } else {
                Toast.makeText(this, "Camera permission is required to take photos", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}
