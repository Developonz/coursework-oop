package com.example.planner.ui.tasks;

import android.content.Context;
import android.graphics.Canvas;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.example.planner.MainActivity;
import com.example.planner.R;
import com.example.planner.controllers.tasks.TaskDBWorker;
import com.example.planner.models.Task;
import com.google.android.material.snackbar.Snackbar;
import com.example.planner.utils.notifications.ManagerAlarm;
import com.example.planner.utils.SwipeDecorator;
import java.time.LocalDate;


public class TaskItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    private final TasksTaskRecyclerViewAdapter adapter;
    private final RecyclerView recyclerView;
    private final Context context;

    public TaskItemTouchHelper(TasksTaskRecyclerViewAdapter adapter, RecyclerView recyclerView, Context context) {
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
        if (viewHolder instanceof TasksTaskRecyclerViewAdapter.TaskHolder || viewHolder instanceof TasksTaskRecyclerViewAdapter.CompleteTaskHolder) {
            return makeMovementFlags(0, ItemTouchHelper.LEFT);
        } else {
            return 0;
        }
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        final int position = viewHolder.getBindingAdapterPosition();
        Task task = adapter.getTask(position);
        if (viewHolder instanceof TasksTaskRecyclerViewAdapter.TaskHolder) {
            adapter.removeItem(task);
            Snackbar.make(recyclerView, "Удалено", Snackbar.LENGTH_LONG)
                    .setAnchorView(((MainActivity) context).findViewById(R.id.bottom_navigation))
                    .setAction("Отменить", v -> adapter.addItem(task)).show();
        } else {
            LocalDate date = task.getTaskDateEnd();
            task.setTaskDateEnd(null);
            adapter.removeItem(task);
            task.setStatus(false);
            TaskDBWorker.updateItem(context, task);
            ManagerAlarm.createOrUpdateNotification(context, task);
            Snackbar.make(recyclerView, "Восстановлено", Snackbar.LENGTH_LONG)
                    .setAnchorView(((MainActivity) context).findViewById(R.id.bottom_navigation))
                    .setAction("Отменить", v -> {
                        task.setTaskDateEnd(date);
                        task.setStatus(true);
                        adapter.addItem(task);
                        task.setStatus(true);
                        TaskDBWorker.updateItem(context, task);
                    }).show();
        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (viewHolder instanceof TasksTaskRecyclerViewAdapter.TaskHolder) {
            SwipeDecorator.createDecorator(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive, "Удалить", R.color.red);
        } else if (viewHolder instanceof TasksTaskRecyclerViewAdapter.CompleteTaskHolder) {
            SwipeDecorator.createDecorator(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive, "Восстановить", R.color.blue);
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public float getSwipeEscapeVelocity(float defaultValue) {
        return defaultValue * 10;
    }
}
