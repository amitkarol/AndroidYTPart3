package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication.entities.User;

public class EditUserActivity extends BaseActivity {
    private static final int REQUEST_IMAGE_PICK = 1;

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText displayNameEditText;
    private ImageView userPhotoImageView;
    private User loggedInUser;
    private Uri selectedImageUri;

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

        // Retrieve data from the intent
        Intent intent = getIntent();
        loggedInUser = (User) intent.getSerializableExtra("user");

        // Populate fields if data is available
        if (loggedInUser != null) {
            firstNameEditText.setText(loggedInUser.getFirstName());
            lastNameEditText.setText(loggedInUser.getLastName());
            displayNameEditText.setText(loggedInUser.getDisplayName());
            if (loggedInUser.getPhoto() != null) {
                userPhotoImageView.setImageURI(Uri.parse(loggedInUser.getPhoto()));
            } else {
                userPhotoImageView.setImageResource(R.drawable.placeholder_thumbnail);
            }
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

                        // Show a confirmation message
                        Toast.makeText(this, "User details updated", Toast.LENGTH_SHORT).show();

                        // Navigate back to the homescreen
                        Intent homeIntent = new Intent(this, homescreen.class);
                        homeIntent.putExtra("user", loggedInUser);
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
}
