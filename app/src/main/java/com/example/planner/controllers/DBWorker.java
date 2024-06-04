package com.example.planner.controllers;

import android.content.Context;
import com.example.planner.db.TaskCRUD;
import com.example.planner.models.Task;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class DBWorker {

    private static final Executor executor = Executors.newSingleThreadExecutor();

    public static void addItem(Context context, Task task) {
            TaskCRUD taskCRUD = new TaskCRUD(context);
            taskCRUD.open();
            task.setId(taskCRUD.addTask(task));
            taskCRUD.close();
    }

    public static void removeItem(Context context, Task task) {
            TaskCRUD taskCRUD = new TaskCRUD(context);
            taskCRUD.open();
            taskCRUD.deleteTask(task.getId());
            taskCRUD.close();
    }

    public static void updateItem(Context context, Task task) {
            TaskCRUD taskCRUD = new TaskCRUD(context);
            taskCRUD.open();
            taskCRUD.updateTask(task);
            taskCRUD.close();
    }

    public static void getAllTasks(Context context, List<Task> list, boolean status) {
            TaskCRUD taskCRUD = new TaskCRUD(context);
            taskCRUD.open();
            list.addAll(taskCRUD.getAllTasks(status));
            taskCRUD.close();
    }

    public static void resetDataBase(Context context) {
        TaskCRUD taskCRUD = new TaskCRUD(context);
        taskCRUD.open();
        taskCRUD.resetDataBase();
        taskCRUD.close();
    }
}
