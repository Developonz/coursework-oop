package com.example.planner.controllers;

import android.content.Context;
import com.example.planner.db.NoteCRUD;
import com.example.planner.models.Note;

import java.util.ArrayList;
import java.util.List;

public class NoteDBWorker {

    public static void addItem(Context context, Note note) {
        NoteCRUD noteCRUD = new NoteCRUD(context);
        noteCRUD.open();
        note.setId(noteCRUD.addNote(note));
        noteCRUD.close();
    }

    public static void removeItem(Context context, Note note) {
        NoteCRUD noteCRUD = new NoteCRUD(context);
        noteCRUD.open();
        noteCRUD.deleteNote(note.getId());
        noteCRUD.close();
    }

    public static void updateItem(Context context, Note note) {
        NoteCRUD noteCRUD = new NoteCRUD(context);
        noteCRUD.open();
        noteCRUD.updateNote(note);
        noteCRUD.close();
    }

    public static void getAllNotes(Context context, List<Note> list) {
        NoteCRUD noteCRUD = new NoteCRUD(context);
        noteCRUD.open();
        list.addAll(noteCRUD.getAllNotes());
        noteCRUD.close();
    }

    public static void resetDataBase(Context context) {
        NoteCRUD noteCRUD = new NoteCRUD(context);
        noteCRUD.open();
        ArrayList<Note> list = new ArrayList<>();
        getAllNotes(context, list);
        noteCRUD.resetDataBase();
        noteCRUD.close();
    }

    public static Note getNote(Context context, long id) {
        NoteCRUD noteCRUD = new NoteCRUD(context);
        noteCRUD.open();
        Note note = noteCRUD.getNote(id);
        noteCRUD.close();
        return note;
    }
}
