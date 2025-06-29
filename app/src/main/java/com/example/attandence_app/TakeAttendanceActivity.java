package com.example.attandence_app;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.*;

public class TakeAttendanceActivity extends AppCompatActivity {

    Spinner spinnerClass;
    Button btnPickDate, btnSubmitAttendance, btnExit;
    TextView txtSelectedDate;
    ListView listStudents;

    AttendanceDatabaseHelper dbHelper;
    List<StudentAttendance> studentList = new ArrayList<>();
    List<Integer> classIds = new ArrayList<>();

    String selectedDate = "";
    int selectedClassId = -1;

    StudentAdapter studentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_attendance);

        spinnerClass = findViewById(R.id.spinnerClass);
        btnPickDate = findViewById(R.id.btnPickDate);
        txtSelectedDate = findViewById(R.id.txtSelectedDate);
        listStudents = findViewById(R.id.listStudents);
        btnSubmitAttendance = findViewById(R.id.btnSubmitAttendance);
        btnExit = findViewById(R.id.btnExit);

        dbHelper = new AttendanceDatabaseHelper(this);

        loadClasses();

        btnPickDate.setOnClickListener(v -> pickDate());

        btnSubmitAttendance.setOnClickListener(v -> {
            if (selectedDate.isEmpty() || selectedClassId == -1) {
                Toast.makeText(this, "Please select class and date", Toast.LENGTH_SHORT).show();
                return;
            }

            for (StudentAttendance sa : studentList) {
                if (!dbHelper.isAttendanceMarked(selectedClassId, sa.id, selectedDate)) {
                    dbHelper.markAttendance(selectedClassId, sa.id, sa.isPresent ? "Present" : "Absent", selectedDate);
                }
            }

            Toast.makeText(this, "Attendance saved", Toast.LENGTH_SHORT).show();
        });

        btnExit.setOnClickListener(v -> finish());

        spinnerClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedClassId = classIds.get(position);
                loadStudents(selectedClassId);
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadClasses() {
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("SELECT id, title FROM Classes", null);
        List<String> classTitles = new ArrayList<>();
        classIds.clear();

        while (cursor.moveToNext()) {
            classIds.add(cursor.getInt(0));
            classTitles.add(cursor.getString(1));
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, classTitles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClass.setAdapter(adapter);
    }

    private void loadStudents(int classId) {
        studentList.clear();

        Cursor cursor = dbHelper.getStudentsInClass(classId);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            studentList.add(new StudentAttendance(id, name, true)); // default is present
        }
        cursor.close();

        studentAdapter = new StudentAdapter();
        listStudents.setAdapter(studentAdapter);
    }

    private void pickDate() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                    txtSelectedDate.setText(selectedDate);
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    // Inner class to hold student attendance state
    class StudentAttendance {
        int id;
        String name;
        boolean isPresent;

        StudentAttendance(int id, String name, boolean isPresent) {
            this.id = id;
            this.name = name;
            this.isPresent = isPresent;
        }
    }

    // Adapter for student list
    class StudentAdapter extends BaseAdapter {
        @Override public int getCount() { return studentList.size(); }
        @Override public Object getItem(int i) { return studentList.get(i); }
        @Override public long getItemId(int i) { return studentList.get(i).id; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = getLayoutInflater().inflate(R.layout.row_student_attendance, parent, false);
            TextView txtName = row.findViewById(R.id.txtStudentName);
            CheckBox check = row.findViewById(R.id.checkAttendance);

            StudentAttendance sa = studentList.get(position);
            txtName.setText(sa.name);
            check.setChecked(sa.isPresent);

            check.setOnCheckedChangeListener((buttonView, isChecked) -> sa.isPresent = isChecked);
            return row;
        }
    }
}
