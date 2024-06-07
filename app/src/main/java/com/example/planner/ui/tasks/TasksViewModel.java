package com.example.planner.ui.tasks;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.planner.models.Task;

import java.util.ArrayList;
import java.util.List;

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

}
