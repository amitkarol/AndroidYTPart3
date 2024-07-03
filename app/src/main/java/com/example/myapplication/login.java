package com.example.myapplication;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.entities.UserManager;
import com.example.myapplication.entities.user;

public class login extends BaseActivity {

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

            // Validate the username and password
            user userFound = UserManager.getInstance().validateUser(username, password);

            if (userFound != null) {
                Intent homescreenIntent = new Intent(login.this, homescreen.class);
                homescreenIntent.putExtra("user", userFound); // Pass the entire user object
                startActivity(homescreenIntent);
            } else {
                // Display error message
                Toast.makeText(this, "User does not exist", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
