package com.example.planner.ui.tasks;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.planner.R;
import com.example.planner.databinding.TaskMenuInfoBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.time.LocalDate;

public class BottomSheetTaskMenuInfo extends BottomSheetDialogFragment {

    private TaskMenuInfoBinding binding;
    private LocalDate selectedDate;
    private TasksRecyclerViewAdapter adapter;
    private boolean isUpdateMode = false;
    private Task taskToUpdate;
    private String selectedCategory;
    private String selectedPriority;

    public BottomSheetTaskMenuInfo(TasksRecyclerViewAdapter adapter) {
        Log.i("test", "constructor");
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
    public int getTheme() {
        return R.style.AppBottomSheetDialogTheme;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("test", "onCreate");
        binding = TaskMenuInfoBinding.inflate(getLayoutInflater());

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("test", "onCreateView");
        setupViews();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("test", "OnResume");
        updateDropDownLists();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View bottomSheet = getDialog().findViewById(com.google.android.material.R.id.design_bottom_sheet);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setDraggable(false);
    }

    private void setupViews() {
        Log.i("test", "setupViews");

        if (isUpdateMode) {
            binding.titleNewTask.setText(taskToUpdate.getTitle());
            binding.createTaskBtn.setText("Изменить");
            if (selectedCategory == null && selectedPriority == null) {
                selectedCategory = taskToUpdate.getCategory();
                selectedPriority = taskToUpdate.getPriority();
            }
        } else {
            if (selectedCategory == null && selectedPriority == null) {
                selectedCategory = getResources().getStringArray(R.array.categories)[0];
                selectedPriority = getResources().getStringArray(R.array.priorities)[0];
            }
        }
/*        updateDropDownLists();*/

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
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                if (arrayResourceId == R.array.categories) {
                    selectedCategory = selectedItem;
                } else if (arrayResourceId == R.array.priorities) {
                    selectedPriority = selectedItem;
                }
            }
        });

    }

    private void updateDropDownLists() {
        Log.i("test", "updateDropDown");
        setupDropDown(binding.categoryNewTask, R.array.categories, selectedCategory);
        setupDropDown(binding.priorityNewTask, R.array.priorities, selectedPriority);
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

    public void setCategory(int number) {
        Log.i("test", "setCategory with number: " + number);
        String[] categories = getResources().getStringArray(R.array.categories);
        if (number >= 0 && number < categories.length) {
            String category = categories[number];
            Log.i("test", "Setting category: " + category);
            binding.categoryNewTask.setText(category, false);
            selectedCategory = category;
        }
    }
}
