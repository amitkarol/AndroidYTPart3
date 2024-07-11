package com.example.myapplication;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.ViewModels.UsersViewModel;
import com.example.myapplication.entities.User;
import com.example.myapplication.utils.CurrentUser;

public class login extends BaseActivity {

    private UsersViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtil.applyTheme(this);
        setContentView(R.layout.loginscreen);

        // Button for creating an account
        Button btncreate = findViewById(R.id.create_account);
        btncreate.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        // Button for logging in
        Button btnLogin = findViewById(R.id.login);
        btnLogin.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.custom_red)));
        btnLogin.setOnClickListener(v -> {
            // Get the entered username and password
            EditText editTextUsername = findViewById(R.id.editTextUsername);
            EditText editTextPassword = findViewById(R.id.editTextPassword);

            String username = editTextUsername.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            viewModel = new ViewModelProvider(this).get(UsersViewModel.class);
            viewModel.login(username, password);
            CurrentUser currentUser = CurrentUser.getInstance();

            // Observe the user LiveData
            currentUser.getUser().observe(this, new Observer<User>() {
                @Override
                public void onChanged(@Nullable User user) {
                    if (user != null) {
                        Log.d("test1", "loggedInUser: " + user);
                        // Navigate to homescreen activity
                        Intent homescreenIntent = new Intent(login.this, homescreen.class);
                        homescreenIntent.putExtra("user", user); // Pass the entire user object
                        startActivity(homescreenIntent);
                        finish(); // Optional: finish the login activity to prevent going back to it
                    } else {
                        // Display error message
                        Toast.makeText(login.this, "User does not exist", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
    }
}
