package com.example.attandence_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AttendanceDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "AppDB";
    private static final int DATABASE_VERSION = 1;

    public AttendanceDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create users table
        db.execSQL("CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "email TEXT UNIQUE, " +
                "password TEXT, " +
                "role TEXT)");

        // Create Classes table
        db.execSQL("CREATE TABLE Classes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL, " +
                "teacher_id INTEGER NOT NULL, " +
                "date TEXT NOT NULL, " +
                "FOREIGN KEY(teacher_id) REFERENCES users(id))");

        // Create ClassStudents table
        db.execSQL("CREATE TABLE ClassStudents (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "class_id INTEGER NOT NULL, " +
                "student_id INTEGER NOT NULL, " +
                "FOREIGN KEY(class_id) REFERENCES Classes(id), " +
                "FOREIGN KEY(student_id) REFERENCES users(id))");

        // Create Attendance table
        db.execSQL("CREATE TABLE Attendance (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "class_id INTEGER NOT NULL, " +
                "student_id INTEGER NOT NULL, " +
                "status TEXT NOT NULL, " +
                "date TEXT NOT NULL, " +
                "FOREIGN KEY(class_id) REFERENCES Classes(id), " +
                "FOREIGN KEY(student_id) REFERENCES users(id))");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Attendance");
        db.execSQL("DROP TABLE IF EXISTS ClassStudents");
        db.execSQL("DROP TABLE IF EXISTS Classes");
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }
    public boolean isAttendanceMarked(int classId, int studentId, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT id FROM Attendance WHERE class_id = ? AND student_id = ? AND date = ?";
        Cursor cursor = db.rawQuery(query, new String[]{
                String.valueOf(classId),
                String.valueOf(studentId),
                date
        });
        boolean marked = cursor.moveToFirst();
        cursor.close();
        return marked;
    }

    // User methods
    public boolean insertUser(String name, String email, String password, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("email", email);
        values.put("password", password);
        values.put("role", role);

        long result = db.insert("users", null, values);
        return result != -1;
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email=? AND password=?", new String[]{email, password});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public int getUserId(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM users WHERE email=? AND password=?", new String[]{email, password});
        int userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(0);
        }
        cursor.close();
        return userId;
    }

    public Cursor getAllStudents() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM users WHERE role='student'", null);
    }

    // Classes and attendance methods
    public long addClass(String title, int teacherId, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("teacher_id", teacherId);
        values.put("date", date);
        return db.insert("Classes", null, values);
    }

    public long addStudentToClass(int classId, int studentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("class_id", classId);
        values.put("student_id", studentId);
        return db.insert("ClassStudents", null, values);
    }

    public long markAttendance(int classId, int studentId, String status, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("class_id", classId);
        values.put("student_id", studentId);
        values.put("status", status);
        values.put("date", date);
        return db.insert("Attendance", null, values);
    }

    public Cursor getStudentsInClass(int classId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT u.id, u.name, u.email FROM users u " +
                "JOIN ClassStudents cs ON u.id = cs.student_id " +
                "WHERE cs.class_id = ?";
        return db.rawQuery(query, new String[]{String.valueOf(classId)});
    }

    public Cursor getAttendanceForStudent(int studentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT c.title, a.date, a.status FROM Attendance a " +
                "JOIN Classes c ON a.class_id = c.id " +
                "WHERE a.student_id = ? ORDER BY a.date DESC";
        return db.rawQuery(query, new String[]{String.valueOf(studentId)});
    }

    public Cursor getClassesByTeacher(int teacherId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM Classes WHERE teacher_id = ?";
        return db.rawQuery(query, new String[]{String.valueOf(teacherId)});
    }
    public Cursor getAllAttendanceRecords() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT u.name AS student_name, c.title AS class_title, " +
                "a.date, a.status " +
                "FROM Attendance a " +
                "JOIN users u ON a.student_id = u.id " +
                "JOIN Classes c ON a.class_id = c.id " +
                "ORDER BY a.date DESC";
        return db.rawQuery(query, null);
    }

    public String getUserRole(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT role FROM users WHERE email = ?", new String[]{email});
        String role = "student"; // default fallback
        if (cursor.moveToFirst()) {
            role = cursor.getString(cursor.getColumnIndexOrThrow("role"));
        }
        cursor.close();
        return role;
    }
    public boolean deleteClass(int classId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedRows = db.delete("classes", "id = ?", new String[]{String.valueOf(classId)});
        return deletedRows > 0;
    }

}
