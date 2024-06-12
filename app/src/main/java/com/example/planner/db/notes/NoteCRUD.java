package com.example.planner.db.notes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.planner.models.Note;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NoteCRUD {

    private SQLiteDatabase db;
    private final NoteDbHelper dbHelper;

    public NoteCRUD(Context context) {
        dbHelper = new NoteDbHelper(context);

    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void resetDataBase() {
        dbHelper.onUpgrade(db, 1, 1);
    }

    public long addNote(Note note) {
        ContentValues values = fillValuesNote(note);
        return db.insert(NoteDbHelper.TABLE_NOTE, null, values);
    }

    public Note getNote(long id) {
        Cursor cursor = db.query(NoteDbHelper.TABLE_NOTE, null, NoteDbHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            Note note = cursorToNote(cursor);
            cursor.close();
            return note;
        }
        return null;
    }

    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();

        Cursor cursor = db.query(NoteDbHelper.TABLE_NOTE, null, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Note note = cursorToNote(cursor);
            notes.add(note);
            cursor.moveToNext();
        }
        cursor.close();
        return notes;
    }

    public void updateNote(Note note) {
        ContentValues values = fillValuesNote(note);
        db.update(NoteDbHelper.TABLE_NOTE, values, NoteDbHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
    }

    public void deleteNote(long id) {
        db.delete(NoteDbHelper.TABLE_NOTE, NoteDbHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    private Note cursorToNote(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(NoteDbHelper.COLUMN_ID));
        String title = cursor.getString(cursor.getColumnIndexOrThrow(NoteDbHelper.COLUMN_TITLE));
        String content = cursor.getString(cursor.getColumnIndexOrThrow(NoteDbHelper.COLUMN_CONTENT));
        String category = cursor.getString(cursor.getColumnIndexOrThrow(NoteDbHelper.COLUMN_CATEGORY));
        String dateLastChangeString = cursor.getString(cursor.getColumnIndexOrThrow(NoteDbHelper.COLUMN_DATE_LAST_CHANGE));
        LocalDate dateLastChange = dateLastChangeString != null ? LocalDate.parse(dateLastChangeString) : null;
        return new Note(title, content, category, dateLastChange, id);
    }

    private ContentValues fillValuesNote(Note note) {
        ContentValues values = new ContentValues();
        values.put(NoteDbHelper.COLUMN_TITLE, note.getTitle());
        values.put(NoteDbHelper.COLUMN_CONTENT, note.getContent());
        values.put(NoteDbHelper.COLUMN_CATEGORY, note.getCategory());
        if (note.getDateLastChange() != null) {
            values.put(NoteDbHelper.COLUMN_DATE_LAST_CHANGE, note.getDateLastChange().toString());
        } else {
            values.putNull(NoteDbHelper.COLUMN_DATE_LAST_CHANGE);
        }
        return values;
    }
}
