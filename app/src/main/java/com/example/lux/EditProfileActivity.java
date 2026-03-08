package com.example.lux;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editTextName, editTextEmail, editTextPhone;
    private Button buttonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhone = findViewById(R.id.editTextPhone);
        buttonSave = findViewById(R.id.buttonSave);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the entered information
                String name = editTextName.getText().toString();
                String email = editTextEmail.getText().toString();
                String phone = editTextPhone.getText().toString();

                // You would typically save this data to a database or shared preferences
                // For this example, we'll just show a toast message.
                String message = "Profile updated!\nName: " + name + "\nEmail: " + email + "\nPhone: " + phone;
                Toast.makeText(EditProfileActivity.this, message, Toast.LENGTH_SHORT).show();

                // Finish the activity and go back
                finish();
            }
        });
    }
}