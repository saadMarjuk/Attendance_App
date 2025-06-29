package com.example.attandence_app;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.attandence_app.AttendanceDatabaseHelper;

import java.util.ArrayList;

public class AllAttendanceRecordsActivity extends Activity {

    private ListView listView;
    private AttendanceDatabaseHelper dbHelper;
    private ArrayList<AttendanceRecord> records;
    private AttendanceRecordAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_attendance_records);  // layout you'll create

        listView = findViewById(R.id.listAttendanceRecords);
        dbHelper = new AttendanceDatabaseHelper(this);
        records = new ArrayList<>();

        Cursor cursor = dbHelper.getAllAttendanceRecords();
        if (cursor.moveToFirst()) {
            do {
                String classTitle = cursor.getString(cursor.getColumnIndexOrThrow("class_title"));
                String studentName = cursor.getString(cursor.getColumnIndexOrThrow("student_name"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));

                records.add(new AttendanceRecord(classTitle, studentName, date, status));
            } while (cursor.moveToNext());
        }
        cursor.close();

        adapter = new AttendanceRecordAdapter();
        listView.setAdapter(adapter);
    }

    class AttendanceRecord {
        String classTitle, studentName, date, status;
        AttendanceRecord(String classTitle, String studentName, String date, String status) {
            this.classTitle = classTitle;
            this.studentName = studentName;
            this.date = date;
            this.status = status;
        }
    }

    class AttendanceRecordAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return records.size();
        }

        @Override
        public Object getItem(int position) {
            return records.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AttendanceRecord record = records.get(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(AllAttendanceRecordsActivity.this)
                        .inflate(R.layout.row_attendance_record, parent, false);
            }

            ((TextView) convertView.findViewById(R.id.txtClassTitle)).setText("Class: " + record.classTitle);
            ((TextView) convertView.findViewById(R.id.txtStudentName)).setText("Student: " + record.studentName);
            ((TextView) convertView.findViewById(R.id.txtDate)).setText("Date: " + record.date);
            ((TextView) convertView.findViewById(R.id.txtStatus)).setText("Status: " + record.status);

            return convertView;
        }
    }
}
