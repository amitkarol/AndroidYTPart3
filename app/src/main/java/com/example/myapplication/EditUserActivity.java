package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.ViewModels.UsersViewModel;
import com.example.myapplication.entities.User;

import java.io.InputStream;

public class EditUserActivity extends BaseActivity {
    private static final int REQUEST_IMAGE_PICK = 1;

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText displayNameEditText;
    private ImageView userPhotoImageView;
    private User loggedInUser;
    private Uri selectedImageUri;
    private UsersViewModel usersViewModel;

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
                            new LoadImageTask(userPhotoImageView).execute(baseUrl + loggedInUser.getPhoto());
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
                        if (selectedImageUri != null) {
                            loggedInUser.setPhoto(selectedImageUri.toString());
                        }

                        usersViewModel.updateUser(loggedInUser.getEmail(), loggedInUser);

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
            Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto, REQUEST_IMAGE_PICK);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            userPhotoImageView.setImageURI(selectedImageUri);
            Toast.makeText(this, "New photo selected", Toast.LENGTH_SHORT).show();
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
