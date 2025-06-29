package com.example.attandence_app;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class activity_login extends AppCompatActivity {

    private AttendanceDatabaseHelper dbHelper;
    private EditText editEmail, editPassword;
    private Button btnLogin;
    private TextView txtSignUpLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new AttendanceDatabaseHelper(this);

        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtSignUpLink = findViewById(R.id.txtSignUpLink);

        btnLogin.setOnClickListener(view -> loginUser());
        txtSignUpLink.setOnClickListener(view ->
                startActivity(new Intent(activity_login.this, activity_signup.class)));
    }

    private void loginUser() {
        String email = editEmail.getText().toString();
        String password = editPassword.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Enter both email and password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (email.equals("admin@gmail.com") && password.equals("admin123")) {
            Toast.makeText(this, "Admin login successful!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(activity_login.this, activity_dashboard.class);
            intent.putExtra("USER_ID", -1);  // Optional: -1 or any placeholder
            intent.putExtra("USER_ROLE", "teacher");  // Give full access
            startActivity(intent);
            finish();
            return;
        }

        boolean isValid = dbHelper.checkUser(email, password);
        if (isValid) {
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

            // Retrieve user ID and role
            int userId = dbHelper.getUserId(email, password);
            String role = getUserRole(email); // getUserRole is a helper method

            Intent intent = new Intent(activity_login.this, activity_dashboard.class);
            intent.putExtra("USER_ID", userId);
            intent.putExtra("USER_ROLE", role);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Invalid email or password!", Toast.LENGTH_SHORT).show();
        }
    }

    // Optional: Helper method to get role
    private String getUserRole(String email) {
        String role = "student"; // default
        try (Cursor cursor = dbHelper.getAllStudents()) {
            while (cursor.moveToNext()) {
                String dbEmail = cursor.getString(cursor.getColumnIndexOrThrow("email"));
                if (dbEmail.equals(email)) {
                    role = "student";
                    break;
                }
            }
        }
        // If not in student list, assume teacher
        return role.equals("student") ? "student" : "teacher";
    }
}
