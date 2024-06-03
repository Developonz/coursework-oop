package com.example.planner.ui.tasks;

import android.content.Context;

import java.util.ArrayList;

public class DBWorker {

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

    public static void getAllTasks(Context context, ArrayList<Task> list, boolean status) {
        TaskCRUD taskCRUD = new TaskCRUD(context);
        taskCRUD.open();
        list.addAll(taskCRUD.getAllTasks(status));
        taskCRUD.close();
    }
}
