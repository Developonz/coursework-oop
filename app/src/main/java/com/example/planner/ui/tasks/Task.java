package com.example.planner.ui.tasks;

import java.time.LocalDate;
import java.time.LocalTime;

public class Task {

    private long id;
    private String title;
    private LocalDate taskDate;
    private LocalTime taskTime;
    private String priority;
    private String category;

    private boolean status;

    private String[] mounths = {"Января", "Февраля", "Марта", "Апреля", "Мая", "Июня", "Июля", "Августа", "Сентября", "Октября", "Ноября", "Декабря"};

    public Task(String title, LocalDate taskDate, LocalTime taskTime, String priority, String category) {
        this.title = title;
        this.taskDate = taskDate;
        this.taskTime = taskTime;
        this.priority = priority;
        this.category = category;
        status = false;
    }

    public Task(String title, LocalDate taskDate, LocalTime taskTime, String priority, String category, boolean status, long id) {
        this.title = title;
        this.taskDate = taskDate;
        this.taskTime = taskTime;
        this.priority = priority;
        this.category = category;
        this.status = status;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getTaskDate() {
        return taskDate;
    }

    public void setTaskDate(LocalDate taskDate) {
        this.taskDate = taskDate;
    }

    public LocalTime getTaskTime() {
        return taskTime;
    }

    public void setTaskTime(LocalTime taskTime) {
        this.taskTime = taskTime;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStringDate() {
        StringBuilder str = new StringBuilder();
        str.append(taskDate.getDayOfMonth());
        str.append(" ");
        str.append(mounths[taskDate.getMonthValue() - 1]);
        return str.toString();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
