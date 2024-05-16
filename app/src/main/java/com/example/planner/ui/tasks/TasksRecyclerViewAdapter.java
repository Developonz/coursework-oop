package com.example.planner.ui.tasks;

import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.planner.databinding.FragmentTasksItemBinding;
import com.example.planner.databinding.HeaderTasksListBinding;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TasksRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Task> allTasks;
    private final List<Object> items;
    private String category;
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_TASK = 1;

    public TasksRecyclerViewAdapter(List<Task> tasks, String category) {
        allTasks = tasks;
        items = new ArrayList<>();
        this.category = category;
        generateItems();
    }

    private void generateItems() {
        items.clear();
        if (allTasks.isEmpty()) return;

        allTasks.sort(Comparator.comparing(Task::getTaskDate));
        String currentHeader = "";
        for (Task task : allTasks) {
            if (category.equals("Все") || task.getCategory().equals(category)) {
                String taskDate = task.getStringDate();
                if (!taskDate.equals(currentHeader)) {
                    currentHeader = taskDate;
                    items.add("");
                }
                items.add(task);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof String) {
            return VIEW_TYPE_HEADER;
        } else {
            return VIEW_TYPE_TASK;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_HEADER) {
            return new HeaderHolder(HeaderTasksListBinding.inflate(inflater, parent, false));
        } else {
            return new TaskHolder(FragmentTasksItemBinding.inflate(inflater, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderHolder) {
            ((HeaderHolder) holder).header.setText(((Task)items.get(position + 1)).getStringDate());
        } else {
            Task task = (Task) items.get(position);
            ((TaskHolder) holder).mContentView.setText(task.getTitle());
            ((TaskHolder) holder).mPriorityView.setText(task.getPriority());
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(Task task) {
        allTasks.add(task);
        allTasks.sort(Comparator.comparing(Task::getTaskDate));
        generateItems();
        notifyDataSetChanged();
    }

    public void changeCategory(String category) {
        this.category = category;
        generateItems();
        notifyDataSetChanged();
    }

    public static class HeaderHolder extends RecyclerView.ViewHolder {
        public final TextView header;

        public HeaderHolder(HeaderTasksListBinding binding) {
            super(binding.getRoot());
            header = binding.headerTasks;
        }
    }

    public static class TaskHolder extends RecyclerView.ViewHolder {
        public final TextView mContentView;
        public final TextView mPriorityView;

        public TaskHolder(FragmentTasksItemBinding binding) {
            super(binding.getRoot());
            mContentView = binding.titleTaskItem;
            mPriorityView = binding.priorityTaskItem;
        }
    }
}
