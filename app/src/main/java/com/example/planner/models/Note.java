package com.example.planner.models;

import java.io.Serializable;
import java.time.LocalDate;

public class Note {

    private long id;
    private String title;
    private String content;
    private String category;
    private LocalDate dateLastChange;
    private final String[] mounths = {"Января", "Февраля", "Марта", "Апреля", "Мая", "Июня", "Июля", "Августа", "Сентября", "Октября", "Ноября", "Декабря"};

    public Note() {
        this.id = -1;
        this.title = "";
        this.content = "";
        this.category = "Без категории";
        this.dateLastChange = LocalDate.now();
    }

    public Note(String title, String content, String category, LocalDate dateLastChange, long id) {
        this.title = title;
        this.content = content;
        this.dateLastChange = dateLastChange;
        this.category = category;
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getDateLastChange() {
        return dateLastChange;
    }

    public void setDateLastChange(LocalDate dateLastChange) {
        this.dateLastChange = dateLastChange;
    }

    public String getStringDate() {
        String str = dateLastChange.getDayOfMonth() +
                " " +
                mounths[dateLastChange.getMonthValue() - 1];
        return str;
    }
}
