package com.example.attandence_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class activity_dashboard extends AppCompatActivity {

    private LinearLayout studentDashboard, teacherDashboard;
    private Button btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Get the role from Intent
        String userRole = getIntent().getStringExtra("USER_ROLE");

        // Find layout sections
        studentDashboard = findViewById(R.id.studentDashboard);
        teacherDashboard = findViewById(R.id.teacherDashboard);

        // Show/hide sections based on role
        if ("student".equalsIgnoreCase(userRole)) {
            teacherDashboard.setVisibility(View.GONE);
            studentDashboard.setVisibility(View.VISIBLE);
        } else {
            teacherDashboard.setVisibility(View.VISIBLE);
            studentDashboard.setVisibility(View.GONE);
        }

        // Setup buttons
        Button btnStudentList = findViewById(R.id.btnStudentList);
        Button btnCreateClass = findViewById(R.id.btnCreateClass);
        Button btnClassList = findViewById(R.id.btnClassList);
        Button btnViewAttendance = findViewById(R.id.btnViewAttendance);
        btnExit = findViewById(R.id.btnExit);

        btnStudentList.setOnClickListener(view -> {
            startActivity(new Intent(this, StudentListActivity.class));
        });

        btnCreateClass.setOnClickListener(view -> {
            startActivity(new Intent(this, CreateClassActivity.class));
        });

        btnClassList.setOnClickListener(view -> {
            startActivity(new Intent(this, ClassListActivity.class));
        });

        btnViewAttendance.setOnClickListener(view -> {
            startActivity(new Intent(this, AllAttendanceRecordsActivity.class));
        });

        btnExit.setOnClickListener(view -> finishAffinity());
    }
}
