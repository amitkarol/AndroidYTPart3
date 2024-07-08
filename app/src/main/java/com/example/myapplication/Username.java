package com.example.myapplication;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.entities.UserManager;

public class Username extends BaseActivity {
    private EditText emailEditText;
    private UserManager usermanager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtil.applyTheme(this);
        setContentView(R.layout.username);

        emailEditText = findViewById(R.id.editTextText2);

        Button secbtnName = findViewById(R.id.second_button);
        secbtnName.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.custom_red)));

        secbtnName.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            usermanager = UserManager.getInstance();
            if (usermanager.isAlreadyExists(email)) {
                Toast.makeText(Username.this, "email already taken. try another one", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidEmail(email)) {
                // Display error message for invalid email format
                Toast.makeText(this, "Invalid email format. Please enter a valid email address.", Toast.LENGTH_SHORT).show();
            } else {
                // Proceed to next screen
                Intent intent = new Intent(this, password.class);
                intent.putExtra("firstName", getIntent().getStringExtra("firstName"));
                intent.putExtra("lastName", getIntent().getStringExtra("lastName"));
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });

    }

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}
