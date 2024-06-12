package com.example.planner.ui.notes;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.planner.models.Note;
import java.util.ArrayList;
import java.util.List;

public class NotesViewModel extends ViewModel {
    private final MutableLiveData<List<Note>> notesList;

    public NotesViewModel() {
        this.notesList = new MutableLiveData<>();
        this.notesList.setValue(new ArrayList<>());
    }

    public ArrayList<Note> getListValue() {
        return (ArrayList<Note>) notesList.getValue();
    }
    public MutableLiveData<List<Note>> getList() {
        return notesList;
    }
}