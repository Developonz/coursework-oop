package com.example.planner.models;

import java.time.LocalDate;
import java.time.LocalTime;

public class Task {

    private long id;
    private String title;
    private LocalDate taskDateBegin;
    private LocalDate taskDateEnd;
    private LocalTime taskTime;
    private String priority;
    private String category;
    private boolean status;

    private final static String[] mounths = {"Января", "Февраля", "Марта", "Апреля", "Мая", "Июня", "Июля", "Августа", "Сентября", "Октября", "Ноября", "Декабря"};

    public Task(String title, LocalDate taskDateBegin, LocalTime taskTime, String priority, String category) {
        this.title = title;
        this.taskDateBegin = taskDateBegin;
        this.taskTime = taskTime;
        this.priority = priority;
        this.category = category;
        status = false;
    }

    public Task(String title, LocalDate taskDateBegin, LocalTime taskTime, String priority, String category, boolean status, long id) {
        this.title = title;
        this.taskDateBegin = taskDateBegin;
        this.taskTime = taskTime;
        this.priority = priority;
        this.category = category;
        this.status = status;
        this.id = id;
    }

    public Task(String title, LocalDate taskDateBegin, LocalDate taskDateEnd, LocalTime taskTime, String priority, String category, boolean status, long id) {
        this.title = title;
        this.taskDateBegin = taskDateBegin;
        this.taskDateEnd = taskDateEnd;
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

    public LocalDate getTaskDateBegin() {
        return taskDateBegin;
    }

    public void setTaskDateBegin(LocalDate taskDateBegin) {
        this.taskDateBegin = taskDateBegin;
    }

    public LocalDate getTaskDateEnd() {
        return taskDateEnd;
    }

    public void setTaskDateEnd(LocalDate taskDateEnd) {
        this.taskDateEnd = taskDateEnd;
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
        LocalDate date = (taskDateEnd == null) ? taskDateBegin : taskDateEnd;
        return date.getDayOfMonth() +
                " " +
                mounths[date.getMonthValue() - 1];
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
