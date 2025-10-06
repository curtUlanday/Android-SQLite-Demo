package com.example.androidsqlitedemo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "school.db";
    public static final int DB_VERSION = 1;

    public static final String TABLE_STUDENTS = "students";
    // Use _id (required by CursorAdapter/Loaders)
    public static final String COL_ID = "_id";
    public static final String COL_NAME = "name";
    public static final String COL_COURSE = "course";
    public static final String COL_CREATED_AT = "created_at";

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_STUDENTS + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_NAME + " TEXT NOT NULL, " +
                    COL_COURSE + " TEXT, " +
                    COL_CREATED_AT + " TEXT DEFAULT (datetime('now'))" +
                    ");";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // quick-n-dirty: drop & recreate (for production, do ALTER TABLE migrations)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);
        onCreate(db);
    }

    // --- CRUD helpers ---

    public long insertStudent(String name, String course) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_NAME, name);
        cv.put(COL_COURSE, course);
        return db.insert(TABLE_STUDENTS, null, cv);
    }

    public Cursor getAllStudents() {
        SQLiteDatabase db = getReadableDatabase();
        // Return a Cursor; caller MUST close it when using raw CursorAdapters. (SimpleCursorAdapter will manage it)
        return db.query(
                TABLE_STUDENTS,
                new String[]{COL_ID, COL_NAME, COL_COURSE, COL_CREATED_AT},
                null, null, null, null,
                COL_CREATED_AT + " DESC"
        );
    }

    public int updateStudent(long id, String name, String course) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_NAME, name);
        cv.put(COL_COURSE, course);
        return db.update(TABLE_STUDENTS, cv, COL_ID + "=?", new String[]{String.valueOf(id)});
    }

    public int deleteStudent(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_STUDENTS, COL_ID + "=?", new String[]{String.valueOf(id)});
    }
}
