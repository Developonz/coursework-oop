package com.example.planner.ui.tasks;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.TypedValue;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.example.planner.MainActivity;
import com.example.planner.R;
import com.example.planner.models.Task;
import com.google.android.material.snackbar.Snackbar;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;


public class TaskItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    private TasksRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
    private Context context;

    public TaskItemTouchHelper(TasksRecyclerViewAdapter adapter, RecyclerView recyclerView, Context context) {
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
        if (viewHolder instanceof TasksRecyclerViewAdapter.TaskHolder) {
            return makeMovementFlags(0, ItemTouchHelper.LEFT);
        } else {
            return 0;
        }
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        final int position = viewHolder.getAdapterPosition();
        Task task = adapter.getTask(position);
        adapter.removeItem(task);
        Snackbar.make(recyclerView, "Удалено", Snackbar.LENGTH_LONG)
                .setAnchorView(((MainActivity) context).findViewById(R.id.bottom_navigation))
                .setAction("Отменить", v -> {
                    adapter.addItem(task);
                }).show();
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                .addSwipeLeftBackgroundColor(ContextCompat.getColor(viewHolder.itemView.getContext(), R.color.red))
                .setSwipeLeftLabelColor(ContextCompat.getColor(viewHolder.itemView.getContext(), R.color.white))
                .setSwipeLeftLabelTextSize(TypedValue.COMPLEX_UNIT_SP, 16)
                .setSwipeLeftLabelTypeface(Typeface.DEFAULT_BOLD)
                .addSwipeLeftLabel("Удалить")
                .create()
                .decorate();
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        return 0.5f;
    }

    @Override
    public float getSwipeEscapeVelocity(float defaultValue) {
        return defaultValue * 10;
    }
}
