package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.ViewModels.UsersViewModel;
import com.example.myapplication.entities.User;
import com.example.myapplication.utils.ImageLoader;

import java.io.ByteArrayOutputStream;

public class EditUserActivity extends BaseActivity {
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private static final int REQUEST_IMAGE_PICK = 1;

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText displayNameEditText;
    private ImageView userPhotoImageView;
    private User loggedInUser;
    private Uri selectedImageUri;
    private UsersViewModel usersViewModel;

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    userPhotoImageView.setImageURI(selectedImageUri);
                    userPhotoImageView.setVisibility(View.VISIBLE);
                }
            }
    );

    private final ActivityResultLauncher<Intent> captureImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Bitmap photo = (Bitmap) result.getData().getExtras().get("data");
                    userPhotoImageView.setImageBitmap(photo);
                    userPhotoImageView.setVisibility(View.VISIBLE);
                    selectedImageUri = getImageUri(this, photo);
                    Log.d("EditUserActivity", "selected image: " + selectedImageUri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtil.applyTheme(this);
        setContentView(R.layout.activity_edit_user);

        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        displayNameEditText = findViewById(R.id.displayNameEditText);
        userPhotoImageView = findViewById(R.id.userPhotoImageView);
        Button saveUserButton = findViewById(R.id.saveUserButton);
        Button changePhotoButton = findViewById(R.id.changePhotoButton);
        Button deleteUserButton = findViewById(R.id.deleteUserButton);

        usersViewModel = new ViewModelProvider(this).get(UsersViewModel.class);

        // Retrieve data from the intent
        String email = getIntent().getStringExtra("user_email");
        Log.d("useredit", "email: " + email);

        // Load user information
        if (email != null) {
            usersViewModel.getUserByEmail(email).observe(this, new Observer<User>() {
                @Override
                public void onChanged(User loadedUser) {
                    if (loadedUser != null) {
                        loggedInUser = loadedUser;
                        firstNameEditText.setText(loggedInUser.getFirstName());
                        lastNameEditText.setText(loggedInUser.getLastName());
                        displayNameEditText.setText(loggedInUser.getDisplayName());
                        if (loggedInUser.getPhoto() != null) {
                            String baseUrl = getResources().getString(R.string.BaseUrl);
                            new ImageLoader.LoadImageTask(userPhotoImageView, R.drawable.dog3).execute(baseUrl + loggedInUser.getPhoto());
                            //new ImageLoader.LoadImageTask(userPhotoImageView, R.drawable.placeholder_thumbnail).execute(baseUrl + loggedInUser.getPhoto());
                        } else {
                            userPhotoImageView.setImageResource(R.drawable.placeholder_thumbnail);
                        }
                    }
                }
            });
        }

        // Set the save button listener
        saveUserButton.setOnClickListener(v -> {
            // Show confirmation dialog
            new AlertDialog.Builder(this)
                    .setTitle("Confirm Save")
                    .setMessage("Are you sure you want to save the changes?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        // Get the new details from the fields
                        String newFirstName = firstNameEditText.getText().toString().trim();
                        String newLastName = lastNameEditText.getText().toString().trim();
                        String newDisplayName = displayNameEditText.getText().toString().trim();

                        // Update the user object
                        loggedInUser.setFirstName(newFirstName);
                        loggedInUser.setLastName(newLastName);
                        loggedInUser.setDisplayName(newDisplayName);

                        // Handle photo update only if selectedImageUri is not null
                        if (selectedImageUri == null && loggedInUser.getPhoto() != null) {
                            selectedImageUri = Uri.parse(loggedInUser.getPhoto());
                        }

                        Log.d("useredit", "photo: " + selectedImageUri);
                        Log.d("useredit", "user: " + loggedInUser.getEmail());
                        usersViewModel.updateUser(newFirstName, newLastName, email, loggedInUser.getPassword(), newDisplayName, this, selectedImageUri);

                        // Show a confirmation message
                        Toast.makeText(this, "User details updated", Toast.LENGTH_SHORT).show();

                        // Navigate back to the UserPage
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("updated_user_email", loggedInUser.getEmail());
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        });

        // Set the delete button listener
        deleteUserButton.setOnClickListener(v -> {
            // Show confirmation dialog
            new AlertDialog.Builder(this)
                    .setTitle("Confirm Delete")
                    .setMessage("Are you sure you want to delete this user?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        // Delete the user on the server
                        usersViewModel.deleteUser(loggedInUser.getEmail());

                        // Show a confirmation message
                        Toast.makeText(this, "User deleted", Toast.LENGTH_SHORT).show();

                        // Navigate back to the homescreen
                        Intent homeIntent = new Intent(this, homescreen.class);
                        startActivity(homeIntent);
                        finish();
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        });

        // Set the change photo button listener
        changePhotoButton.setOnClickListener(v -> {
            // Show options to pick or capture a photo
            CharSequence[] options = {"Choose from Gallery", "Take a Photo"};
            AlertDialog.Builder builder = new AlertDialog.Builder(EditUserActivity.this);
            builder.setTitle("Change Photo");
            builder.setItems(options, (dialog, which) -> {
                if (which == 0) {
                    // Choose from gallery
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    pickImageLauncher.launch(pickPhoto);
                } else if (which == 1) {
                    // Take a photo
                    if (ContextCompat.checkSelfPermission(EditUserActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                            || ContextCompat.checkSelfPermission(EditUserActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(EditUserActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
                    } else {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        captureImageLauncher.launch(intent);
                    }
                }
            });
            builder.show();
        });

        // Request focus and show keyboard when EditText is clicked
        View.OnFocusChangeListener onFocusChangeListener = (v, hasFocus) -> {
            if (hasFocus) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
            }
        };

        firstNameEditText.setOnFocusChangeListener(onFocusChangeListener);
        lastNameEditText.setOnFocusChangeListener(onFocusChangeListener);
        displayNameEditText.setOnFocusChangeListener(onFocusChangeListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            userPhotoImageView.setImageURI(selectedImageUri);
            runOnUiThread(() -> Toast.makeText(this, "New photo selected", Toast.LENGTH_SHORT).show());
        }
    }

    public Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}
