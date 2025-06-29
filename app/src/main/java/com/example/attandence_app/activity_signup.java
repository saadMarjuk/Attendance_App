package com.example.attandence_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class activity_signup extends AppCompatActivity {

    private AttendanceDatabaseHelper dbHelper;
    private EditText editName, editEmail, editPassword, editConfirmPassword;
    private RadioGroup radioGroupRole;
    private Button btnSignUp, btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        dbHelper = new AttendanceDatabaseHelper(this);

        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmailSignUp);
        editPassword = findViewById(R.id.editPasswordSignUp);
        editConfirmPassword = findViewById(R.id.editConfirmPassword);
        radioGroupRole = findViewById(R.id.radioGroupRole);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnExit = findViewById(R.id.btnExit);

        btnExit.setOnClickListener(view -> finishAffinity());
        btnSignUp.setOnClickListener(view -> registerUser());
    }

    private void registerUser() {
        String name = editName.getText().toString();
        String email = editEmail.getText().toString();
        String password = editPassword.getText().toString();
        String confirmPassword = editConfirmPassword.getText().toString();

        int selectedRoleId = radioGroupRole.getCheckedRadioButtonId();
        String role = selectedRoleId == R.id.radioStudent ? "student" : "teacher";

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success = dbHelper.insertUser(name, email, password, role);
        if (success) {
            Toast.makeText(this, "Signup successful!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(activity_signup.this, activity_login.class));
            finish();
        } else {
            Toast.makeText(this, "Signup failed, email may already exist!", Toast.LENGTH_SHORT).show();
        }
    }
}
