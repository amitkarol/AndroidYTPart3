package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.text.InputType;
import android.widget.Toast;
import java.util.regex.Pattern;

public class password extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtil.applyTheme(this);
        setContentView(R.layout.password);

        Button thirdbtn = findViewById(R.id.thirdButton);
        thirdbtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.custom_red)));

        thirdbtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, Displayname.class);
            startActivity(intent);
        });

        EditText editTextPassword = findViewById(R.id.editTextPassword);
        EditText editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        CheckBox checkBoxShowPassword = findViewById(R.id.checkBoxShowPassword);

        checkBoxShowPassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Show password
                editTextPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                editTextConfirmPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                // Hide password
                editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                editTextConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            // Move the cursor to the end of the text
            editTextPassword.setSelection(editTextPassword.length());
            editTextConfirmPassword.setSelection(editTextConfirmPassword.length());
        });

        Button nextButton = findViewById(R.id.thirdButton);
        nextButton.setOnClickListener(v -> {
            String password = editTextPassword.getText().toString().trim();
            String confirmPassword = editTextConfirmPassword.getText().toString().trim();

            if (!isValidPassword(password)) {
                // Display error message for invalid password format
                Toast.makeText(this, "Password does not meet the conditions.", Toast.LENGTH_LONG).show();
            } else if (!password.equals(confirmPassword)) {
                // Display error message if passwords do not match
                Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
            } else {
                // Proceed to next screen
                Intent intent = new Intent(this, Displayname.class);
                intent.putExtra("firstName", getIntent().getStringExtra("firstName"));
                intent.putExtra("lastName", getIntent().getStringExtra("lastName"));
                intent.putExtra("username", getIntent().getStringExtra("username"));
                intent.putExtra("password", password);
                startActivity(intent);
            }
        });
    }

    private boolean isValidPassword(String password) {
        // Password must contain at least one letter, one number, and one symbol
        String passwordRegex = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        Pattern pattern = Pattern.compile(passwordRegex);
        return pattern.matcher(password).matches();
    }
}
