package com.example.planner.ui.notes;

import android.content.Context;
import android.graphics.Canvas;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.example.planner.MainActivity;
import com.example.planner.R;
import com.example.planner.models.Note;
import com.example.planner.utils.SwipeDecorator;
import com.google.android.material.snackbar.Snackbar;


public class NoteItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    private final NotesRecyclerViewAdapter adapter;
    private final RecyclerView recyclerView;
    private final Context context;

    public NoteItemTouchHelper(NotesRecyclerViewAdapter adapter, RecyclerView recyclerView, Context context) {
        super(0, ItemTouchHelper.LEFT);
        this.adapter = adapter;
        this.recyclerView = recyclerView;
        this.context = context;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof NotesRecyclerViewAdapter.NoteHolder) {
            return makeMovementFlags(0, ItemTouchHelper.LEFT);
        } else {
            return 0;
        }
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        if (viewHolder instanceof NotesRecyclerViewAdapter.NoteHolder) {
            final int position = viewHolder.getBindingAdapterPosition();
            Note note = adapter.getNote(position);
            adapter.removeItem(note);
            Snackbar.make(recyclerView, "Удалено", Snackbar.LENGTH_LONG)
                    .setAnchorView(((MainActivity) context).findViewById(R.id.bottom_navigation))
                    .setAction("Отменить", v -> adapter.addItem(note)).show();
        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (viewHolder instanceof NotesRecyclerViewAdapter.NoteHolder) {
            SwipeDecorator.createDecorator(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive, "Удалить", R.color.red);
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public float getSwipeEscapeVelocity(float defaultValue) {
        return defaultValue * 10;
    }
}
