package com.example.attandence_app;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;  // <-- Import Log for debugging
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.BaseAdapter;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ClassAttendanceActivity extends Activity {

    private static final String TAG = "ClassAttendanceActivity";

    private int classId;
    private TextView txtClassTitle, txtDate;
    private ListView listClassStudents;
    private Button btnSubmitAttendance;
    private AttendanceDatabaseHelper dbHelper;
    private ArrayList<StudentItem> studentList;
    private StudentAdapter adapter;
    private String currentDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_attendance);

        txtClassTitle = findViewById(R.id.txtClassTitle);
        txtDate = findViewById(R.id.txtDate);
        listClassStudents = findViewById(R.id.listClassStudents);
        btnSubmitAttendance = findViewById(R.id.btnSubmitAttendance);

        dbHelper = new AttendanceDatabaseHelper(this);

        // Get classId from Intent
        classId = getIntent().getIntExtra("classId", -1);
        Log.d(TAG, "Received classId: " + classId);

        if (classId == -1) {
            Toast.makeText(this, "Invalid class ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set current date
        currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        txtDate.setText("Date: " + currentDate);

        // Load class title and log
        Cursor classInfo = dbHelper.getReadableDatabase().rawQuery(
                "SELECT title FROM Classes WHERE id = ?", new String[]{String.valueOf(classId)});
        if (classInfo.moveToFirst()) {
            String title = classInfo.getString(classInfo.getColumnIndexOrThrow("title"));
            txtClassTitle.setText("Class: " + title);
            Log.d(TAG, "Class title: " + title);
        } else {
            Log.d(TAG, "No class found with id: " + classId);
        }
        classInfo.close();

        // *** Load students for this class and setup adapter ***
        studentList = new ArrayList<>();
        Cursor cursor = dbHelper.getStudentsInClass(classId);
        Log.d(TAG, "Students cursor count: " + cursor.getCount());

        if (cursor.moveToFirst()) {
            do {
                int studentId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String studentName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                Log.d(TAG, "Loaded student: id=" + studentId + ", name=" + studentName);
                studentList.add(new StudentItem(studentId, studentName, false));
            } while (cursor.moveToNext());
        } else {
            Log.d(TAG, "No students found for class id: " + classId);
        }
        cursor.close();

        adapter = new StudentAdapter();
        listClassStudents.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        // Submit Attendance button listener
        btnSubmitAttendance.setOnClickListener(v -> {
            for (StudentItem item : studentList) {
                if (!dbHelper.isAttendanceMarked(classId, item.id, currentDate)) {
                    String status = item.isPresent ? "present" : "absent";
                    long res = dbHelper.markAttendance(classId, item.id, status, currentDate);
                    Log.d(TAG, "Marked attendance for studentId=" + item.id + ", status=" + status + ", result=" + res);
                } else {
                    Log.d(TAG, "Attendance already marked for studentId=" + item.id);
                }
            }
            Toast.makeText(this, "Attendance marked successfully!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }



    // Student Data Model
    class StudentItem {
        int id;
        String name;
        boolean isPresent;

        StudentItem(int id, String name, boolean isPresent) {
            this.id = id;
            this.name = name;
            this.isPresent = isPresent;
        }
    }

    // Custom Adapter
    class StudentAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return studentList.size();
        }

        @Override
        public Object getItem(int position) {
            return studentList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return studentList.get(position).id;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            StudentItem student = studentList.get(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(ClassAttendanceActivity.this)
                        .inflate(R.layout.row_student_attendance, parent, false);
            }

            TextView txtName = convertView.findViewById(R.id.txtStudentName);
            CheckBox checkBox = convertView.findViewById(R.id.checkAttendance);

            // Prevent checkbox recycling issues
            checkBox.setOnCheckedChangeListener(null);
            checkBox.setChecked(student.isPresent);
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> student.isPresent = isChecked);

            txtName.setText(student.name);

            return convertView;
        }

    }
}
