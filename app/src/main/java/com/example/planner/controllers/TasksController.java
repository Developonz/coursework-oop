package com.example.planner.controllers;

import android.content.Context;

import com.example.planner.R;
import com.example.planner.models.Task;
import com.example.planner.ui.tasks.TasksViewModel;
import com.example.planner.utils.Notifications.AlarmManagerNot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class TasksController {
    private final Context context;
    private final TasksViewModel viewModel;

    public TasksController(Context context, TasksViewModel viewModel) {
        this.context = context;
        this.viewModel = viewModel;
    }

    public void addTask(Task task) {
        if (getTasks() != null) {
            if (!task.isStatus()) {
                TaskDBWorker.addItem(context, task);
            } else {
                task.setStatus(false);
                TaskDBWorker.updateItem(context, task);
            }
            getTasks().add(task);
        }
        AlarmManagerNot.createOrUpdateNotification(context, task);
    }

    public void removeTask(Task task) {
        if (getTasks() != null) {
            if (!task.isStatus()) {
                TaskDBWorker.removeItem(context, task);
            } else {
                TaskDBWorker.updateItem(context, task);
            }
            getTasks().remove(task);
        }
        AlarmManagerNot.deleteNotification(context, task);
    }

    public void resetData() {
        loadTasks(false);
        for (Task task : viewModel.getListValue()) {
            AlarmManagerNot.deleteNotification(context, task);
        }
        TaskDBWorker.resetDataBase(context);
    }

    public void updateTask(Task task) {
        if (getTasks() != null) {
            TaskDBWorker.updateItem(context, task);
        }
        AlarmManagerNot.createOrUpdateNotification(context, task);
    }

    public void loadTasks(boolean status) {
        getTasks().clear();
        TaskDBWorker.getAllTasks(context, getTasks(), status);
        viewModel.getList().setValue(viewModel.getList().getValue());
    }

    public void sortTasksTitle(ArrayList<Task> list, boolean direction) {
        if (direction) {
            list.sort(Comparator.comparing(Task::getTitle));
        } else {
            list.sort(Comparator.comparing(Task::getTitle).reversed());
        }
    }

    public void sortTasksPriority(ArrayList<Task> list, boolean direction) {
        String[] priorities = context.getResources().getStringArray(R.array.priorities);
        if (direction) {
            list.sort(Comparator.comparingInt(item -> priorities.length - Arrays.asList(priorities).indexOf(item.getPriority()) - 1));
        } else {
            list.sort(Comparator.comparingInt(item -> Arrays.asList(priorities).indexOf(item.getPriority())));
        }
    }

    public Context getContext() {
        return context;
    }

    public TasksViewModel getViewModel() {
        return viewModel;
    }

    public ArrayList<Task> getTasks() {
        return viewModel.getListValue();
    }
}