package com.example.planner.controllers;

import android.content.Context;
import com.example.planner.R;
import com.example.planner.models.Task;
import com.example.planner.models.TasksViewModel;
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

    public void sortTasksTitle(ArrayList<Task> list, boolean direction, boolean isDate) {
        if (direction) {
            list.sort(Comparator.comparing(Task::getTitle));
        } else {
            list.sort(Comparator.comparing(Task::getTitle).reversed());
        }
        if (isDate) {
            list.sort(Comparator.comparing(Task::getTaskDate));
        }
    }

    public void sortTasksPriority(ArrayList<Task> list, boolean direction, boolean isDate) {
        String[] priorities = context.getResources().getStringArray(R.array.priorities);
        if (direction) {
            list.sort(Comparator.comparingInt(item -> priorities.length - Arrays.asList(priorities).indexOf(item.getPriority()) - 1));
        } else {
            list.sort(Comparator.comparingInt(item -> Arrays.asList(priorities).indexOf(item.getPriority())));
        }
        if (isDate) {
            list.sort(Comparator.comparing(Task::getTaskDate));
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
