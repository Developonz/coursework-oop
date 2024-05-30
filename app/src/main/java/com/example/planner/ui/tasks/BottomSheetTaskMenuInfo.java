package com.example.planner.ui.tasks;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.planner.R;
import com.example.planner.databinding.TaskMenuInfoBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.time.LocalDate;

public class BottomSheetTaskMenuInfo extends BottomSheetDialogFragment {

    private TaskMenuInfoBinding binding;
    private LocalDate selectedDate;
    private TasksRecyclerViewAdapter adapter;
    private boolean isUpdateMode = false;
    private Task taskToUpdate;

    public BottomSheetTaskMenuInfo(TasksRecyclerViewAdapter adapter) {
        this.adapter = adapter;
        selectedDate = LocalDate.now();
    }

    public BottomSheetTaskMenuInfo(TasksRecyclerViewAdapter adapter, Task taskToUpdate) {
        this.adapter = adapter;
        this.taskToUpdate = taskToUpdate;
        this.isUpdateMode = true;
        selectedDate = taskToUpdate.getTaskDate();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = TaskMenuInfoBinding.inflate(getLayoutInflater());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setupViews();
        return binding.getRoot();
    }

    private void setupViews() {
        setupDropDown(binding.categoryNewTask, R.array.categories, isUpdateMode ? taskToUpdate.getCategory() : getResources().getStringArray(R.array.categories)[0]);
        setupDropDown(binding.priorityNewTask, R.array.priorities, isUpdateMode ? taskToUpdate.getPriority() : getResources().getStringArray(R.array.priorities)[0]);

        if (isUpdateMode) {
            binding.titleNewTask.setText(taskToUpdate.getTitle());
            binding.createTaskBtn.setText("Изменить");
        }

        binding.dateTVBS.setText(selectedDate.toString());

        binding.createTaskBtn.setOnClickListener(v -> {
            String title = binding.titleNewTask.getText().toString();
            if (!title.isEmpty()) {
                if (isUpdateMode) {
                    updateTask();
                } else {
                    createTask();
                }
            } else {
                showToast("Название не может быть пустым");
            }
        });

        binding.dateLL.setOnClickListener(v -> {
            BottomSheetTaskMenuDate bottomSheetTaskMenuDate = new BottomSheetTaskMenuDate(this, selectedDate, newDate -> {
                selectedDate = newDate;
                binding.dateTVBS.setText(selectedDate.toString());
                /*this.show(getParentFragmentManager(), getTag());*/
            });
            bottomSheetTaskMenuDate.show(getParentFragmentManager(), bottomSheetTaskMenuDate.getTag());

            dismiss();
        });
    }

    private void setupDropDown(AutoCompleteTextView autoCompleteTextView, int arrayResourceId, String selectedItem) {
        String[] items = getResources().getStringArray(arrayResourceId);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, items);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setText(selectedItem, false);
    }

    private void createTask() {
        String title = binding.titleNewTask.getText().toString();
        String category = binding.categoryNewTask.getText().toString();
        String priority = binding.priorityNewTask.getText().toString();
        Task task = new Task(title, selectedDate, priority, category);
        binding.titleNewTask.setText("");
        adapter.addItem(task);
    }

    private void updateTask() {
        taskToUpdate.setTitle(binding.titleNewTask.getText().toString());
        taskToUpdate.setCategory(binding.categoryNewTask.getText().toString());
        taskToUpdate.setPriority(binding.priorityNewTask.getText().toString());
        taskToUpdate.setTaskDate(selectedDate);
        adapter.generateItems();
        adapter.notifyDataSetChanged();
        dismiss();
    }


    private void showToast(String message) {
        Toast toast = Toast.makeText(requireContext(), message, Toast.LENGTH_LONG);
        View view = toast.getView();
        view.setBackgroundColor(Color.parseColor("#333333"));
        TextView text = view.findViewById(android.R.id.message);
        text.setTextColor(Color.WHITE);
        text.setGravity(Gravity.CENTER);
        toast.show();
    }


}
