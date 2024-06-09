package com.example.planner.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.planner.models.Task;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


public class TaskCRUD {

    private SQLiteDatabase db;

    private final TaskDbHelper dbHelper;

    public TaskCRUD(Context context) {
        dbHelper = new TaskDbHelper(context);
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
        /*dbHelper.onUpgrade(db, 1, 1);*/
    }

    public void close() {
        dbHelper.close();
    }

    public long addTask(Task task) {
        ContentValues values = fillValuesTask(task);
        return db.insert(TaskDbHelper.TABLE_TASK, null, values);
    }

    public Task getTask(long id) {
        Cursor cursor = db.query(TaskDbHelper.TABLE_TASK, null, TaskDbHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            Task task = cursorToTask(cursor);
            cursor.close();
            return task;
        }
        return null;
    }

    public List<Task> getAllTasks(boolean status) {
        List<Task> tasks = new ArrayList<>();
        String selection = TaskDbHelper.COLUMN_STATUS + " = ?";
        String[] selectionArgs = { status ? "1" : "0" };

        Cursor cursor = db.query(TaskDbHelper.TABLE_TASK, null, selection, selectionArgs, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Task task = cursorToTask(cursor);
            tasks.add(task);
            cursor.moveToNext();
        }
        cursor.close();
        return tasks;
    }


    public void updateTask(Task task) {
        ContentValues values = fillValuesTask(task);
        db.update(TaskDbHelper.TABLE_TASK, values, TaskDbHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(task.getId())});
    }

    public void deleteTask(long id) {
        db.delete(TaskDbHelper.TABLE_TASK, TaskDbHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    private Task cursorToTask(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(TaskDbHelper.COLUMN_ID));
        String title = cursor.getString(cursor.getColumnIndexOrThrow(TaskDbHelper.COLUMN_TITLE));
        String taskDateBeginString = cursor.getString(cursor.getColumnIndexOrThrow(TaskDbHelper.COLUMN_TASK_DATE_BEGIN));
        String taskDateEndString = cursor.getString(cursor.getColumnIndexOrThrow(TaskDbHelper.COLUMN_TASK_DATE_END));
        String taskTimeString = cursor.getString(cursor.getColumnIndexOrThrow(TaskDbHelper.COLUMN_TASK_TIME));
        LocalDate taskDateBegin = taskDateBeginString != null ? LocalDate.parse(taskDateBeginString) : null;
        LocalDate taskDateEnd = taskDateEndString != null ? LocalDate.parse(taskDateEndString) : null;
        LocalTime taskTime = taskTimeString != null ? LocalTime.parse(taskTimeString) : null;
        String priority = cursor.getString(cursor.getColumnIndexOrThrow(TaskDbHelper.COLUMN_PRIORITY));
        String category = cursor.getString(cursor.getColumnIndexOrThrow(TaskDbHelper.COLUMN_CATEGORY));
        boolean status = cursor.getInt(cursor.getColumnIndexOrThrow(TaskDbHelper.COLUMN_STATUS)) == 1;
        return new Task(title, taskDateBegin, taskDateEnd, taskTime, priority, category, status, id);
    }

    public void resetDataBase() {
        dbHelper.onUpgrade(db, 1, 1);
    }

    private ContentValues fillValuesTask(Task task) {
        ContentValues values = new ContentValues();
        values.put(TaskDbHelper.COLUMN_TITLE, task.getTitle());
        if (task.getTaskDateBegin() != null) {
            values.put(TaskDbHelper.COLUMN_TASK_DATE_BEGIN, task.getTaskDateBegin().toString());
        } else {
            values.putNull(TaskDbHelper.COLUMN_TASK_DATE_BEGIN);
        } if (task.getTaskDateEnd() != null) {
            values.put(TaskDbHelper.COLUMN_TASK_DATE_END, task.getTaskDateEnd().toString());
        } else {
            values.putNull(TaskDbHelper.COLUMN_TASK_DATE_END);
        } if (task.getTaskTime() != null) {
            values.put(TaskDbHelper.COLUMN_TASK_TIME, task.getTaskTime().toString());
        } else {
            values.putNull(TaskDbHelper.COLUMN_TASK_TIME);
        }
        values.put(TaskDbHelper.COLUMN_PRIORITY, task.getPriority());
        values.put(TaskDbHelper.COLUMN_CATEGORY, task.getCategory());
        values.put(TaskDbHelper.COLUMN_STATUS, task.isStatus() ? 1 : 0);
        return values;
    }
}
