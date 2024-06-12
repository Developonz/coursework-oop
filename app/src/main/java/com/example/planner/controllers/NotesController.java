package com.example.planner.controllers;

import android.content.Context;
import com.example.planner.models.Note;
import com.example.planner.models.Task;
import com.example.planner.ui.notes.NotesViewModel;
import com.example.planner.utils.Notifications.AlarmManagerNot;

import java.util.ArrayList;

public class NotesController {
    private final Context context;
    private final NotesViewModel viewModel;

    public NotesController(Context context, NotesViewModel viewModel) {
        this.context = context;
        this.viewModel = viewModel;
    }

    public void addNote(Note note) {
        if (getNotes() != null) {
            NoteDBWorker.addItem(context, note);
            getNotes().add(note);
        }
    }

    public void removeNote(Note note) {
        if (getNotes() != null) {
            NoteDBWorker.removeItem(context, note);
            getNotes().remove(note);
        }
    }

    public void updateNote(Note note) {
        if (getNotes() != null) {
            NoteDBWorker.updateItem(context, note);
        }
    }

    public void loadNotes() {
        getNotes().clear();
        NoteDBWorker.getAllNotes(context, getNotes());
        viewModel.getList().setValue(viewModel.getList().getValue());
    }

    public void resetData() {
        NoteDBWorker.resetDataBase(context);
    }

    public Note getNote(long id) {
        return NoteDBWorker.getNote(context, id);
    }


    public Context getContext() {
        return context;
    }

    public NotesViewModel getViewModel() {
        return viewModel;
    }

    public ArrayList<Note> getNotes() {
        return viewModel.getListValue();
    }
}