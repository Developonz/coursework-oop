package com.example.planner.utils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import com.example.planner.db.notes.NoteDbHelper;
import com.example.planner.db.tasks.TaskDbHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseImporterExporter {

    public void openFilePickerForExport(Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.setType("application/json");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_TITLE, "tasks.json");
        activity.startActivityForResult(intent, requestCode);
    }



    public void openFilePickerForImport(Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/json");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        activity.startActivityForResult(intent, requestCode);
    }

    public void exportDatabaseToJson(Uri uri, Context context) throws IOException {
        TaskDbHelper taskDbHelper = new TaskDbHelper(context);
        SQLiteDatabase db = taskDbHelper.getWritableDatabase();

        NoteDbHelper noteDbHelper = new NoteDbHelper(context);
        SQLiteDatabase dbNote = noteDbHelper.getWritableDatabase();

        Map<String, List<Map<String, Object>>> tablesData = new HashMap<>();
        try {
            List<Map<String, Object>> tasksData = new ArrayList<>();
            Cursor taskCursor = db.query(TaskDbHelper.TABLE_TASK, null, null, null, null, null, null);
            if (taskCursor.moveToFirst()) {
                do {
                    Map<String, Object> row = new HashMap<>();
                    for (String columnName : taskCursor.getColumnNames()) {
                        Object value;
                        int columnIndex = taskCursor.getColumnIndex(columnName);
                        switch (taskCursor.getType(columnIndex)) {
                            case Cursor.FIELD_TYPE_INTEGER:
                                value = taskCursor.getInt(columnIndex);
                                break;
                            case Cursor.FIELD_TYPE_FLOAT:
                                value = taskCursor.getFloat(columnIndex);
                                break;
                            case Cursor.FIELD_TYPE_STRING:
                                value = taskCursor.getString(columnIndex);
                                break;
                            default:
                                value = null;
                                break;
                        }
                        row.put(columnName, value);
                    }
                    tasksData.add(row);
                } while (taskCursor.moveToNext());
            }
            taskCursor.close();
            tablesData.put("tasks", tasksData);


            List<Map<String, Object>> notesData = new ArrayList<>();
            Cursor noteCursor = dbNote.query(NoteDbHelper.TABLE_NOTE, null, null, null, null, null, null);
            if (noteCursor.moveToFirst()) {
                do {
                    Map<String, Object> row = new HashMap<>();
                    for (String columnName : noteCursor.getColumnNames()) {
                        Object value;
                        int columnIndex = noteCursor.getColumnIndex(columnName);
                        switch (noteCursor.getType(columnIndex)) {
                            case Cursor.FIELD_TYPE_INTEGER:
                                value = noteCursor.getInt(columnIndex);
                                break;
                            case Cursor.FIELD_TYPE_FLOAT:
                                value = noteCursor.getFloat(columnIndex);
                                break;
                            case Cursor.FIELD_TYPE_STRING:
                                value = noteCursor.getString(columnIndex);
                                break;
                            default:
                                value = null;
                                break;
                        }
                        row.put(columnName, value);
                    }
                    notesData.add(row);
                } while (noteCursor.moveToNext());
            }
            noteCursor.close();
            tablesData.put("notes", notesData);

            Gson gson = new GsonBuilder().create();
            String json = gson.toJson(tablesData);
            Log.i("export", "Export " + uri);

            try (OutputStream outputStream = context.getContentResolver().openOutputStream(uri)) {
                if (outputStream == null) {
                    throw new IOException("OutputStream is null");
                }
                outputStream.write(json.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
            } catch (Exception e) {
                Log.e("export", "Error exporting database", e);
                throw new IOException("Error exporting database", e);
            }
        } finally {
            taskDbHelper.close();
            noteDbHelper.close();
        }
    }


    public void importDatabaseFromJson(Uri uri, boolean isSaveDataImport, Context context) throws IOException {
        TaskDbHelper taskDbHelper = new TaskDbHelper(context);
        SQLiteDatabase db = taskDbHelper.getWritableDatabase();

        NoteDbHelper noteDbHelper = new NoteDbHelper(context);
        SQLiteDatabase dbNote = noteDbHelper.getWritableDatabase();

        try (InputStream inputStream = context.getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            Gson gson = new GsonBuilder().create();
            Type type = new TypeToken<Map<String, List<Map<String, Object>>>>() {}.getType();
            Map<String, List<Map<String, Object>>> tablesData = gson.fromJson(reader, type);

            if (!isSaveDataImport) {
                db.execSQL("DELETE FROM " + TaskDbHelper.TABLE_TASK);
                dbNote.execSQL("DELETE FROM " + NoteDbHelper.TABLE_NOTE);
            }

            if (tablesData.containsKey("tasks")) {
                List<Map<String, Object>> tasksData = tablesData.get("tasks");
                assert tasksData != null;
                for (Map<String, Object> row : tasksData) {
                    ContentValues values = new ContentValues();
                    for (Map.Entry<String, Object> entry : row.entrySet()) {
                        if (!entry.getKey().equals("id")) { // Пропускаем поле id
                            values.put(entry.getKey(), entry.getValue().toString());
                        }
                    }
                    db.insert(TaskDbHelper.TABLE_TASK, null, values);
                }
            }

            if (tablesData.containsKey("notes")) {
                List<Map<String, Object>> notesData = tablesData.get("notes");
                assert notesData != null;
                for (Map<String, Object> row : notesData) {
                    ContentValues values = new ContentValues();
                    for (Map.Entry<String, Object> entry : row.entrySet()) {
                        if (!entry.getKey().equals("id")) {
                            values.put(entry.getKey(), entry.getValue().toString());
                        }
                    }
                    dbNote.insert(NoteDbHelper.TABLE_NOTE, null, values);
                }
            }
        } catch (FileNotFoundException e) {
            Log.e("import", "File not found: " + uri, e);
            throw new IOException("File not found: " + uri, e);
        } catch (IOException e) {
            Log.e("import", "IO error: " + uri, e);
            throw new IOException("IO error: " + uri, e);
        } catch (JsonSyntaxException e) {
            Log.e("import", "JSON syntax error: " + uri, e);
            throw new IOException("JSON syntax error", e);
        } catch (Exception e) {
            Log.e("import", "Unexpected error: " + uri, e);
            throw new IOException("Unexpected error", e);
        } finally {
            taskDbHelper.close();
            noteDbHelper.close();
        }
    }





}
