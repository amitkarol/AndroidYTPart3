package com.example.myapplication;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.ViewModels.UsersViewModel;

public class Username extends AppCompatActivity {
    private EditText emailEditText;
    private UsersViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtil.applyTheme(this);
        setContentView(R.layout.username);

        emailEditText = findViewById(R.id.editTextText2);

        Button secbtnName = findViewById(R.id.second_button);
        secbtnName.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.custom_red)));

        viewModel = new ViewModelProvider(this).get(UsersViewModel.class);

        secbtnName.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();

            if (!isValidEmail(email)) {
                // Display error message for invalid email format
                Toast.makeText(this, "Invalid email format. Please enter a valid email address.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if email already exists
            viewModel.checkEmailExists(email).observe(this, exists -> {
                if (exists) {
                    Toast.makeText(Username.this, "Email already taken. Try another one.", Toast.LENGTH_SHORT).show();
                } else {
                    // Proceed to next screen
                    Intent intent = new Intent(this, password.class);
                    intent.putExtra("firstName", getIntent().getStringExtra("firstName"));
                    intent.putExtra("lastName", getIntent().getStringExtra("lastName"));
                    intent.putExtra("email", email);
                    startActivity(intent);
                }
            });
        });
    }

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
