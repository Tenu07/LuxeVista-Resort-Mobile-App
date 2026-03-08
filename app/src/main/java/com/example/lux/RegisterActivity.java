package com.example.lux;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextName, editTextEmail, editTextPassword, editTextConfirmPassword;
    private Button buttonRegister;
    private TextView textViewLoginLink;
    private DataRepository dataRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dataRepository = DataRepository.getInstance(getApplicationContext());

        editTextName = findViewById(R.id.editTextNameRegister);
        editTextEmail = findViewById(R.id.editTextEmailRegister);
        editTextPassword = findViewById(R.id.editTextPasswordRegister);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPasswordRegister);
        buttonRegister = findViewById(R.id.buttonRegister);
        textViewLoginLink = findViewById(R.id.textViewLoginLink);

        buttonRegister.setOnClickListener(v -> attemptRegistration());

        textViewLoginLink.setOnClickListener(v -> {
            // Go back to Login Activity
            finish(); // Closes RegisterActivity
        });
    }

    private void attemptRegistration() {
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        // --- Input Validation ---
        if (TextUtils.isEmpty(name)) {
            editTextName.setError("Name is required");
            editTextName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Enter a valid email");
            editTextEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) { // Basic password length check
            editTextPassword.setError("Password must be at least 6 characters");
            editTextPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            editTextConfirmPassword.setError("Passwords do not match");
            editTextConfirmPassword.requestFocus();
            return;
        }

        // --- Registration Logic ---
        User newUser = dataRepository.registerUser(name, email, password);

        if (newUser != null) {
            // Registration successful
            Toast.makeText(this, "Registration Successful! Please log in.", Toast.LENGTH_LONG).show();
            finish(); // Close RegisterActivity and return to LoginActivity
        } else {
            // Registration failed (likely email already exists in our simple repo)
            Toast.makeText(this, "Registration Failed. Email might already be in use.", Toast.LENGTH_LONG).show();
            editTextEmail.setError("Email might already be registered");
            editTextEmail.requestFocus();
        }
    }
}