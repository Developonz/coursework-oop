package com.example.planner.models;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.planner.controllers.DBWorker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class TasksViewModel extends ViewModel {

    private final MutableLiveData<List<Task>> tasksList;

    public TasksViewModel() {
        this.tasksList = new MutableLiveData<>();
        this.tasksList.setValue(new ArrayList<>());
    }

    public ArrayList<Task> getListValue() {
        return (ArrayList<Task>) tasksList.getValue();
    }

    public MutableLiveData<List<Task>> getList() {
        return tasksList;
    }

    /*public void updateListValue(Context context) {
        tasksList.getValue().clear();
        DBWorker.getAllTasks(context, tasksList.getValue(), false);
        tasksList.setValue(tasksList.getValue());
    }

    public void addTask(Context context, Task newTask) {
        if (tasksList.getValue() != null) {
            if (!newTask.isStatus()) {
                DBWorker.addItem(context, newTask);
            }
            tasksList.getValue().add(newTask);
        }
        tasksList.setValue(tasksList.getValue());
    }

    public void removeTask(Context context, Task task) {
        if (tasksList.getValue() != null) {
            if (task.isStatus()) {
                DBWorker.updateItem(context, task);
            } else {
                DBWorker.removeItem(context, task);
            }
            tasksList.getValue().remove(task);;
        }
        tasksList.setValue(tasksList.getValue());
    }*/
}
