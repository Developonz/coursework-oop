package com.example.planner.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TaskDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "task.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_TASK = "task";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_TASK_DATE = "taskDate";
    public static final String COLUMN_TASK_TIME = "taskTime";
    public static final String COLUMN_PRIORITY = "priority";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_STATUS = "status";

    private static final String DATABASE_CREATE = "create table "
            + TABLE_TASK + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_TITLE + " text not null, "
            + COLUMN_TASK_DATE + " text, "
            + COLUMN_TASK_TIME + " text, "
            + COLUMN_PRIORITY + " text, "
            + COLUMN_CATEGORY + " text, "
            + COLUMN_STATUS + " integer);";

    public TaskDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASK);
        onCreate(db);
    }
}
