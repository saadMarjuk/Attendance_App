package com.example.attandence_app;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ClassListActivity extends AppCompatActivity {

    // Layout container where class buttons will be dynamically added
    private LinearLayout layoutClasses;

    // Database helper instance to access attendance-related data
    private AttendanceDatabaseHelper dbHelper;

    // Teacher ID to filter classes; replace with real ID from login/session
    private int teacherId = 1;

    // Tag for logging
    private static final String TAG = "ClassListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the layout for this activity
        setContentView(R.layout.activity_class_list);

        // Find the LinearLayout defined in the XML layout
        layoutClasses = findViewById(R.id.layoutClasses);

        // Initialize the database helper
        dbHelper = new AttendanceDatabaseHelper(this);

        // Load all classes assigned to this teacher and create buttons for each
        loadClassButtons();
    }

    /**
     * Loads class data from the database and creates a button for each class.
     * When a button is clicked, it opens the ClassAttendanceActivity
     * passing the class ID and title as extras.
     */
    private void loadClassButtons() {
        // Query the database for classes belonging to the teacher
        Cursor cursor = dbHelper.getClassesByTeacher(teacherId);

        // Check if cursor is null, meaning query failed or no data
        if (cursor == null) {
            Toast.makeText(this, "No classes found or database error", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Cursor is null from getClassesByTeacher");
            return;
        }

        // If no classes exist for this teacher, show a message and return
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No classes assigned to this teacher.", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "No classes found for teacherId: " + teacherId);
            cursor.close();
            return;
        }

        // Iterate through the cursor to create buttons for each class
        while (cursor.moveToNext()) {
            // Retrieve class ID and title from the cursor
            int classId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String classTitle = cursor.getString(cursor.getColumnIndexOrThrow("title"));

            Log.d(TAG, "Loading class: " + classTitle + " (id=" + classId + ")");

            // Create a new button for the class
            Button btn = new Button(this);

            // Set button text to class title
            btn.setText(classTitle);

            // Optional: keep text case as is (not all caps)
            btn.setAllCaps(false);

            // Add some padding for better UI
            btn.setPadding(16, 16, 16, 16);

            // Set click listener to open ClassAttendanceActivity with class info
            // On normal click, open attendance screen
            btn.setOnClickListener(v -> {
                Log.d(TAG, "Clicked class button: " + classTitle + ", id: " + classId);
                Intent intent = new Intent(ClassListActivity.this, ClassAttendanceActivity.class);
                intent.putExtra("classId", classId);
                intent.putExtra("classTitle", classTitle);
                startActivity(intent);
            });

// On long press, confirm and delete class
            btn.setOnLongClickListener(v -> {
                new android.app.AlertDialog.Builder(ClassListActivity.this)
                        .setTitle("Delete Class")
                        .setMessage("Are you sure you want to delete \"" + classTitle + "\"?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            boolean deleted = dbHelper.deleteClass(classId);
                            if (deleted) {
                                Toast.makeText(ClassListActivity.this, "Class deleted", Toast.LENGTH_SHORT).show();
                                layoutClasses.removeAllViews(); // Clear and reload UI
                                loadClassButtons();
                            } else {
                                Toast.makeText(ClassListActivity.this, "Failed to delete class", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;
            });


            // Add the button to the layout
            layoutClasses.addView(btn);
        }

        // Close the cursor to avoid memory leaks
        cursor.close();
    }
}
