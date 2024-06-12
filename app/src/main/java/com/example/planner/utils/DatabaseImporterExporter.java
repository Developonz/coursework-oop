package com.example.planner.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.example.planner.controllers.TaskDBWorker;
import com.example.planner.controllers.TasksController;
import com.example.planner.models.Task;
import com.example.planner.utils.Notifications.AlarmManagerNot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DatabaseImporterExporter {

    private final Context context;
    private final TasksController controller;

    public DatabaseImporterExporter(TasksController controller) {
        this.controller = controller;
        this.context = controller.getContext();
    }

    public void openFilePickerForExport(Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.setType("application/json");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_TITLE, "tasks.json");
        activity.startActivityForResult(intent, requestCode);
    }

    public void exportDatabaseToJson(Uri uri) throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        TaskDBWorker.getAllTasks(context, tasks, false);
        TaskDBWorker.getAllTasks(context, tasks, true);
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(tasks);

        try (OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
             FileOutputStream fileOutputStream = (FileOutputStream) outputStream) {
            fileOutputStream.write(json.getBytes());
            fileOutputStream.flush();
        } catch (Exception e) {
            throw new IOException("Ошибка при экспорте базы данных", e);
        }
    }

    public void openFilePickerForImport(Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/json");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        activity.startActivityForResult(intent, requestCode);
    }

    public void importDatabaseFromJson(Uri uri, boolean isSaveDataImport) throws IOException {
        try (InputStream inputStream = context.getContentResolver().openInputStream(uri);
             FileInputStream fileInputStream = (FileInputStream) inputStream;
             InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            Gson gson = new GsonBuilder().create();
            Type taskListType = new TypeToken<ArrayList<Task>>() {}.getType();
            List<Task> importedTasks = gson.fromJson(bufferedReader, taskListType);

            if (importedTasks != null) {
                if (!isSaveDataImport) {
                    controller.resetData();
                }
            }

            for (Task task : importedTasks) {
                TaskDBWorker.addItem(context, task);
                AlarmManagerNot.createOrUpdateNotification(context, task);
            }
        } catch (Exception e) {
            throw new IOException("Ошибка при импорте базы данных", e);
        }
    }

}
