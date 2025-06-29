package com.example.attandence_app;

import static android.widget.Toast.LENGTH_SHORT;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class StudentListActivity extends AppCompatActivity {

    private ListView listView;
    private AttendanceDatabaseHelper attendanceDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);

        listView = findViewById(R.id.listStudents);
        attendanceDb = new AttendanceDatabaseHelper(this);

        loadStudents();

        Button btnExit = findViewById(R.id.btnExit);
        btnExit.setOnClickListener(v -> finish());
    }

    private void loadStudents() {
        Cursor cursor = attendanceDb.getAllStudents();

        if (cursor == null || cursor.getCount() == 0) {
            Toast.makeText(this, "No students found", LENGTH_SHORT).show();
            return;
        }

        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        while (cursor.moveToNext()) {
            HashMap<String, String> map = new HashMap<>();
            map.put("name", cursor.getString(cursor.getColumnIndexOrThrow("name")));
            map.put("email", cursor.getString(cursor.getColumnIndexOrThrow("email")));
            list.add(map);
        }

        cursor.close();

        SimpleAdapter adapter = new SimpleAdapter(
                this,
                list,
                R.layout.row_student,
                new String[]{"name", "email"},
                new int[]{R.id.txtStudentName, R.id.txtStudentEmail}
        );

        listView.setAdapter(adapter);
    }
}
