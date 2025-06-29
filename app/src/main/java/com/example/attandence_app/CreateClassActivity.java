package com.example.attandence_app;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class CreateClassActivity extends AppCompatActivity {

    private EditText editClassName;
    private ListView listViewStudents;
    private Button btnCreateClass;

    private AttendanceDatabaseHelper attendanceDb;

    private ArrayList<Integer> selectedStudentIds = new ArrayList<>();
    private ArrayList<HashMap<String, String>> studentList = new ArrayList<>();
    private SimpleAdapter adapter;

    private int teacherId = 1; // Replace with dynamic teacher ID from login

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_class);

        editClassName = findViewById(R.id.editClassName);
        listViewStudents = findViewById(R.id.listViewStudents);
        btnCreateClass = findViewById(R.id.btnCreateClass);

        attendanceDb = new AttendanceDatabaseHelper(this);

        loadStudents();

        listViewStudents.setOnItemClickListener((parent, view, position, id) -> {
            int studentId = Integer.parseInt(studentList.get(position).get("id"));

            if (selectedStudentIds.contains(studentId)) {
                selectedStudentIds.remove(Integer.valueOf(studentId));
                view.setBackgroundColor(0x00000000); // Transparent
            } else {
                selectedStudentIds.add(studentId);
                view.setBackgroundColor(0xFFDDFFDD); // Light green
            }
        });

        btnCreateClass.setOnClickListener(v -> createClass());
    }

    private void loadStudents() {
        Cursor cursor = attendanceDb.getAllStudents();
        studentList.clear();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                map.put("id", cursor.getString(cursor.getColumnIndexOrThrow("id")));
                map.put("name", cursor.getString(cursor.getColumnIndexOrThrow("name")));
                map.put("email", cursor.getString(cursor.getColumnIndexOrThrow("email")));
                studentList.add(map);
            } while (cursor.moveToNext());

            cursor.close();
        }

        adapter = new SimpleAdapter(
                this,
                studentList,
                R.layout.row_student,
                new String[]{"name", "email"},
                new int[]{R.id.txtStudentName, R.id.txtStudentEmail}
        );

        listViewStudents.setAdapter(adapter);
    }

    private void createClass() {
        String title = editClassName.getText().toString().trim();

        if (title.isEmpty() || selectedStudentIds.isEmpty()) {
            Toast.makeText(this, "Please enter a class name and select at least one student.", Toast.LENGTH_SHORT).show();
            return;
        }

        String date = String.valueOf(System.currentTimeMillis());
        long classId = attendanceDb.addClass(title, teacherId, date);

        if (classId == -1) {
            Toast.makeText(this, "Failed to create class!", Toast.LENGTH_SHORT).show();
            return;
        }

        for (int studentId : selectedStudentIds) {
            attendanceDb.addStudentToClass((int) classId, studentId);
        }

        Toast.makeText(this, "Class created successfully!", Toast.LENGTH_LONG).show();
        finish();
    }
}
