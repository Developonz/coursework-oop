package com.example.planner.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.example.planner.controllers.DBWorker;
import com.example.planner.controllers.TasksController;
import com.example.planner.models.Task;
import com.example.planner.utils.Notifications.AlarmManagerNot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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

    public void exportDatabaseToJson(Uri uri) throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        DBWorker.getAllTasks(context, tasks, false);
        DBWorker.getAllTasks(context, tasks, true);
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(tasks);
        try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.getContentResolver().openOutputStream(uri))) {
            outputStreamWriter.write(json);
        } catch (Exception e) {
            throw new IOException("Ошибка при экспорте базы данных", e);
        }
    }

    public void importDatabaseFromJson(Uri uri, boolean isSaveDataImport) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.getContentResolver().openInputStream(uri))))
        {
            Gson gson = new GsonBuilder().create();
            Type taskListType = new TypeToken<ArrayList<Task>>() {}.getType();
            List<Task> importedTasks = gson.fromJson(bufferedReader, taskListType);

            if (!isSaveDataImport) {
                controller.resetData();
            }
            for (Task task : importedTasks) {
                DBWorker.addItem(context, task);
                AlarmManagerNot.createOrUpdateNotification(context, task);
            }
        } catch (Exception e) {
            throw new IOException("Ошибка при импорте базы данных", e);
        }
    }

    public void openFilePickerForImport(Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/json");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        activity.startActivityForResult(intent, requestCode);
    }

    public void openFilePickerForExport(Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.setType("application/json");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_TITLE, "tasks");
        activity.startActivityForResult(intent, requestCode);
    }
}
