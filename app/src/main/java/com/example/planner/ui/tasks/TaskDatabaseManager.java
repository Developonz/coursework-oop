package com.example.planner.ui.tasks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TaskDatabaseManager {

    private TaskDbHelper dbHelper;
    private Context context;

    public TaskDatabaseManager(Context context) {
        this.context = context;
        dbHelper = new TaskDbHelper(context);
    }

    public void exportDatabaseToJson(Uri uri) throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        DBWorker.getAllTasks(context, tasks, false);
        DBWorker.getAllTasks(context, tasks, true);
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(tasks);
        Log.i("test", "export");

        try (OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
             OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream)) {
             outputStreamWriter.write(json);
        } catch (Exception e) {
            Log.e("TaskDatabaseManager", "Ошибка при экспорте базы данных", e);
            throw new IOException("Ошибка при экспорте базы данных", e);
        }
    }

    public void importDatabaseFromJson(Uri uri) throws IOException {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            Gson gson = new GsonBuilder().create();
            Type taskListType = new TypeToken<ArrayList<Task>>() {}.getType();
            List<Task> importedTasks = gson.fromJson(bufferedReader, taskListType);

            for (Task task : importedTasks) {
                DBWorker.addItem(context, task);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openFilePickerForImport(Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/json");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        activity.startActivityForResult(intent, requestCode);
    }

    public void openFilePickerForExport(Activity activity, int requestCode) {
        Log.i("test", "openExport");
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.setType("application/json");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_TITLE, "tasks");
        activity.startActivityForResult(intent, requestCode);
    }
}
