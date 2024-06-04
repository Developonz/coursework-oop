package com.example.planner.controllers;

import android.content.Context;

import com.example.planner.models.Task;
import com.example.planner.models.TasksViewModel;

import java.util.ArrayList;

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
                DBWorker.addItem(context, task);
            } else {
                task.setStatus(false);
            }
            getTasks().add(task);
        }
        /*viewModel.getList().setValue(viewModel.getList().getValue());*/
    }

    public void removeTask(Task task) {
        if (getTasks() != null) {
            if (task.isStatus()) {
                DBWorker.updateItem(context, task);
            } else {
                DBWorker.removeItem(context, task);
            }
            getTasks().remove(task);;
        }
        /*viewModel.getList().setValue(viewModel.getList().getValue());*/
    }

    public void updateTask(Task task) {
        if (getTasks() != null) {
            DBWorker.updateItem(context, task);
        }
        /*viewModel.getList().setValue(viewModel.getList().getValue());*/
    }

    public void loadTasks() {
        getTasks().clear();
        DBWorker.getAllTasks(context, getTasks(), false);
        viewModel.getList().setValue(viewModel.getList().getValue());
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
