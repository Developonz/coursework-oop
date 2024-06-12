package com.example.planner.controllers.tasks;

import android.content.Context;
import com.example.planner.db.tasks.TaskCRUD;
import com.example.planner.models.Task;
import java.util.ArrayList;
import java.util.List;

public class TaskDBWorker {

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
        ArrayList<Task> list = new ArrayList<>();
        getAllTasks(context, list, false);
        taskCRUD.resetDataBase();
        taskCRUD.close();
    }

    public static Task getTask(Context context, long id) {
        TaskCRUD taskCRUD = new TaskCRUD(context);
        taskCRUD.open();
        Task task = taskCRUD.getTask(id);
        taskCRUD.close();
        return task;
    }
}
