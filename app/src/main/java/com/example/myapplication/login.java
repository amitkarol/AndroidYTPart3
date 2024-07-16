package com.example.myapplication;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.myapplication.ViewModels.UsersViewModel;
import com.example.myapplication.utils.CurrentUser;

public class login extends BaseActivity {

    private UsersViewModel viewModel;
    private boolean isLoginAttempted = false;
    private EditText editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtil.applyTheme(this);
        setContentView(R.layout.loginscreen);

        // Initialize views
        EditText editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        Button btnLogin = findViewById(R.id.login);
        Button btnCreate = findViewById(R.id.create_account);
        CheckBox showPasswordCheckBox = findViewById(R.id.showPasswordCheckBox);

        // Set create account button listener
        btnCreate.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        // Set login button listener
        btnLogin.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.custom_red)));
        btnLogin.setOnClickListener(v -> {
            // Get the entered username and password
            String username = editTextUsername.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            viewModel = new ViewModelProvider(this).get(UsersViewModel.class);
            viewModel.login(username, password);
            CurrentUser currentUser = CurrentUser.getInstance();

            // Observe the user LiveData
            currentUser.getUser().observe(this, user -> {
                if (user != null && isLoginAttempted) {
                    isLoginAttempted = false;
                    // Navigate to homescreen activity
                    Intent homescreenIntent = new Intent(login.this, homescreen.class);
                    homescreenIntent.putExtra("user", user);
                    startActivity(homescreenIntent);
                    finish();
                } else if (user == null && isLoginAttempted) {
                    isLoginAttempted = false;
                    // Show toast if user does not exist
                    showLoginFailedToast();
                }
            });

            // Set login attempt flag
            isLoginAttempted = true;
        });

        // Set show password CheckBox listener
        showPasswordCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                editTextPassword.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                editTextPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            editTextPassword.setSelection(editTextPassword.getText().length()); // Move cursor to the end of the text
        });
    }

    private void showLoginFailedToast() {
        Toast.makeText(this, "Login Failed. User does not exist. Please check your credentials or create a new account.", Toast.LENGTH_LONG).show();
    }
}
