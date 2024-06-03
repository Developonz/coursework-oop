package com.example.planner.ui.tasks;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.planner.MainActivity;
import com.example.planner.databinding.FragmentTasksItemBinding;
import com.example.planner.databinding.HeaderOldTasksListBinding;
import com.example.planner.databinding.HeaderTasksListBinding;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class TasksRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnItemHeaderOldRecyclerViewClickListener, Filterable {
    private final List<Task> allTasks;
    private final List<Object> items;
    private final List<Task> filteredList;
    private String category;
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_HEADER_OLD = 1;
    private static final int VIEW_TYPE_TASK = 2;
    private boolean isVisibleOldTasks = true;
    private OnItemTaskRecyclerClickListener listener;
    private Context context;
    private String filterTitle = "";

    public TasksRecyclerViewAdapter(Context context, List<Task> tasks, String category, OnItemTaskRecyclerClickListener listener) {
        this.context = context;
        allTasks = tasks;
        items = new ArrayList<>();
        filteredList = new ArrayList<>();
        filteredList.addAll(allTasks);
        this.category = category;
        this.listener = listener;
        updateTasksList();
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof Task) {
            return VIEW_TYPE_TASK;
        } else if (items.get(position) == "") {
            return VIEW_TYPE_HEADER;
        } else {
            return VIEW_TYPE_HEADER_OLD;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_HEADER) {
            return new HeaderHolder(HeaderTasksListBinding.inflate(inflater, parent, false));
        } else if (viewType == VIEW_TYPE_TASK) {
            return new TaskHolder(FragmentTasksItemBinding.inflate(inflater, parent, false), listener);
        } else {
            return new HeaderOldHolder(HeaderOldTasksListBinding.inflate(inflater, parent, false), this);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderHolder) {
            ((HeaderHolder) holder).header.setText(((Task)items.get(position + 1)).getStringDate());
        } else if (holder instanceof  TaskHolder) {
            Task task = (Task) items.get(position);
            ((TaskHolder) holder).mCheck.setChecked(false);
            ((TaskHolder) holder).mContentView.setText(task.getTitle());
            ((TaskHolder) holder).mPriorityView.setText(task.getPriority());
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateTasksList() {
        generateItems(filteredList);
    }

    private void generateItems(List<Task> list) {
        Log.i("test", "generate");
        items.clear();
        if (list.isEmpty()) {
            notifyDataSetChanged();
            return;
        }
        list.sort(Comparator.comparing(Task::getTaskDate));
        List<Task> oldTask = new ArrayList<>();
        String currentHeader = "";
        for (Task task : list) {
            if (!task.getTaskDate().isBefore(LocalDate.now())) {
                if (category.equals("Все") || task.getCategory().equals(category)) {
                    String taskDate = task.getStringDate();
                    if (!taskDate.equals(currentHeader)) {
                        currentHeader = taskDate;
                        items.add("");
                    }
                    items.add(task);
                }
            } else {
                oldTask.add(task);
            }
        }
        if (!oldTask.isEmpty()) {
            items.add("Old");
            if (isVisibleOldTasks) {
                for (Task task : oldTask) {
                    if (category.equals("Все") || task.getCategory().equals(category)) {
                        items.add(task);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    public void addItem(Task task) {
        if (!task.isStatus()) {
            DBWorker.addItem(context, task);
        }
        allTasks.add(task);
        filteredList.add(task);
        updateTasksList();
    }

    public void removeItem(Task task) {
        if (task.isStatus()) {
            DBWorker.updateItem(context, task);
        } else {
            DBWorker.removeItem(context, task);
        }
        filteredList.remove(task);
        allTasks.remove(task);
        updateTasksList();
    }

    public void changeCategory(String category) {
        this.category = category;
        updateTasksList();
    }

    public Task getTask(int position) {
        if  (items.get(position) instanceof Task) {
            return (Task) items.get(position);
        }
        return null;
    }

    @Override
    public void onItemHeaderOldClickListener() {
        isVisibleOldTasks = !isVisibleOldTasks;
        updateTasksList();
    }

    @Override
    public Filter getFilter() {
        return myFilter;
    }

    private final Filter myFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            filteredList.clear();
            filterTitle = charSequence.toString();

            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(allTasks);
                Log.i("test", "in char null");
            } else {
                Log.i("test", "in char not null");
                for (Task task: allTasks) {
                    if (task.getTitle().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        filteredList.add(task);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            updateTasksList();
        }
    };

    public static class HeaderHolder extends RecyclerView.ViewHolder {
        public final TextView header;

        public HeaderHolder(HeaderTasksListBinding binding) {
            super(binding.getRoot());
            header = binding.headerTasks;
        }
    }

    public static class HeaderOldHolder extends RecyclerView.ViewHolder {
        public final TextView header;
        public final ImageView arrow;

        @SuppressLint("NotifyDataSetChanged")
        public HeaderOldHolder(HeaderOldTasksListBinding binding, OnItemHeaderOldRecyclerViewClickListener listener) {
            super(binding.getRoot());
            header = binding.header;
            arrow = binding.arrow;
            binding.panel.setOnClickListener(v -> {
                binding.arrow.setRotation(binding.arrow.getRotation() + 180);
                listener.onItemHeaderOldClickListener();
            });
        }
    }

    public static class TaskHolder extends RecyclerView.ViewHolder {
        public final CheckBox mCheck;
        public final TextView mContentView;
        public final TextView mPriorityView;

        public TaskHolder(FragmentTasksItemBinding binding, OnItemTaskRecyclerClickListener listener) {
            super(binding.getRoot());
            mCheck = binding.checkBoxItem;
            mContentView = binding.titleTaskItem;
            mPriorityView = binding.priorityTaskItem;

            mCheck.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        mCheck.postDelayed(() -> listener.onItemCheckBoxClick(position), 750);
                    }
                }
            });

            binding.contentPanel.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemViewClick(position);
                    }
                }
            });
        }
    }
}
