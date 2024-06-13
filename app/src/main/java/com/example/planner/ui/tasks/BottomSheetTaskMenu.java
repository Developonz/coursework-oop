package com.example.planner.ui.tasks;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.planner.R;
import com.example.planner.databinding.TaskMenuInfoBinding;
import com.example.planner.models.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class BottomSheetTaskMenu extends BottomSheetDialogFragment {
    private static boolean isOpenedThis = false;
    private TaskMenuInfoBinding binding;
    private LocalDate selectedDate;
    private LocalTime selectedTime;
    private final TasksRecyclerViewAdapter adapter;
    private boolean isUpdateMode = false;
    private boolean isOpenDate = false;
    private boolean isOpenTime = false;
    private Task taskToUpdate;
    private String selectedCategory;
    private String selectedPriority;
    private final static String[] categoriesTitle = {"Без категории", "Личное", "Учёба", "Работа", "Желания"};
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();


    private BottomSheetTaskMenu(TasksRecyclerViewAdapter adapter, int category) {
        this.adapter = adapter;
        selectedDate = LocalDate.now();
        selectedCategory = categoriesTitle[category];
    }

    private BottomSheetTaskMenu(TasksRecyclerViewAdapter adapter, Task taskToUpdate) {
        this.adapter = adapter;
        this.taskToUpdate = taskToUpdate;
        this.isUpdateMode = true;
        selectedDate = taskToUpdate.getTaskDateBegin();
        selectedTime = taskToUpdate.getTaskTime();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = TaskMenuInfoBinding.inflate(getLayoutInflater());
        changeModeKeyBoarding();
        setupViews();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View bottomSheet = Objects.requireNonNull(getDialog()).findViewById(com.google.android.material.R.id.design_bottom_sheet);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setDraggable(false);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public int getTheme() {
        return R.style.AppBottomSheetDialogTheme;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        isOpenedThis = false;
    }

    public static BottomSheetTaskMenu getInstance(TasksRecyclerViewAdapter adapter, int category) {
        if (!isOpenedThis) {
            isOpenedThis = true;
            return new BottomSheetTaskMenu(adapter, category);
        } else {
            return null;
        }
    }

    public static BottomSheetTaskMenu getInstance(TasksRecyclerViewAdapter adapter, Task taskToUpdate) {
        if (!isOpenedThis) {
            isOpenedThis = true;
            return new BottomSheetTaskMenu(adapter, taskToUpdate);
        } else {
            return null;
        }
    }

    private void changeModeKeyBoarding() {
        binding.getRoot().getViewTreeObserver().addOnWindowFocusChangeListener(hasFocus -> {
            if (hasFocus) {
                if (getDialog() != null && getDialog().getWindow() != null) {
                    new Handler().postDelayed(() -> getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE), 100);

                }
            } else {
                if (getDialog() != null && getDialog().getWindow() != null) {
                    getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
                }
            }
        });
    }

    private void setupViews() {
        Log.i("test", "setupViews");

        if (isUpdateMode) {
            binding.titleNewTask.setText(taskToUpdate.getTitle());
            binding.createTaskBtn.setText("Изменить");
            if (selectedCategory == null) {
                selectedCategory = taskToUpdate.getCategory();
            }
            if (selectedPriority == null) {
                selectedPriority = taskToUpdate.getPriority();
            }
        } else {
            if (selectedCategory == null) {
                selectedCategory = getResources().getStringArray(R.array.categories)[0];
            }
            if (selectedPriority == null) {
                selectedPriority = getResources().getStringArray(R.array.priorities)[0];
            }
        }

        binding.dateTVBS.setText(selectedDate.toString());
        if (selectedTime != null) {
            binding.timeTVBS.setText(selectedTime.toString());
            binding.removeTime.setVisibility(View.VISIBLE);
        }

        binding.createTaskBtn.setOnClickListener(v -> {
            String title = Objects.requireNonNull(binding.titleNewTask.getText()).toString();
            if (!title.isEmpty()) {
                if (isUpdateMode) {
                    updateTask();
                } else {
                    createTask();
                }
                resetTime();
            } else {
                showToast();
            }
        });

        binding.dateLL.setOnClickListener(v -> openDatePicker());
        binding.timeLL.setOnClickListener(v -> openTimePicker());
        binding.removeTime.setOnClickListener(v -> resetTime());

        initializeViewsAsync();
    }

    private void initializeViewsAsync() {
        executor.execute(() -> {
            String[] categories = getResources().getStringArray(R.array.categories);
            String[] priorities = getResources().getStringArray(R.array.priorities);

            requireActivity().runOnUiThread(() -> {
                setupDropDown(binding.categoryNewTask, categories, selectedCategory);
                setupDropDown(binding.priorityNewTask, priorities, selectedPriority);
            });
        });
    }

    private void setupDropDown(AutoCompleteTextView autoCompleteTextView, String[] items, String selectedItem) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, items);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setText(selectedItem, false);
        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem1 = (String) parent.getItemAtPosition(position);
            if (autoCompleteTextView == binding.categoryNewTask) {
                selectedCategory = selectedItem1;
            } else if (autoCompleteTextView == binding.priorityNewTask) {
                selectedPriority = selectedItem1;
            }
        });
    }

    private void createTask() {
        String title = Objects.requireNonNull(binding.titleNewTask.getText()).toString();
        String category = binding.categoryNewTask.getText().toString();
        String priority = binding.priorityNewTask.getText().toString();
        Task task = new Task(title, selectedDate, selectedTime, priority, category);
        binding.titleNewTask.setText("");
        adapter.addItem(task);
    }

    private void updateTask() {
        taskToUpdate.setTitle(Objects.requireNonNull(binding.titleNewTask.getText()).toString());
        taskToUpdate.setCategory(binding.categoryNewTask.getText().toString());
        taskToUpdate.setPriority(binding.priorityNewTask.getText().toString());
        taskToUpdate.setTaskDateBegin(selectedDate);
        taskToUpdate.setTaskTime(selectedTime);
        adapter.updateTask(taskToUpdate);
        dismiss();
    }

    private void openTimePicker() {
        if (!isOpenTime) {
            MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                    .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour((selectedTime != null) ? selectedTime.getHour() : LocalTime.now().getHour())
                    .setMinute((selectedTime != null) ? selectedTime.getMinute() : LocalTime.now().getMinute())
                    .setTitleText("Установить время")
                    .setTheme(R.style.TimePicker)
                    .build();

            timePicker.addOnPositiveButtonClickListener(v -> {
                selectedTime = LocalTime.of(timePicker.getHour(), timePicker.getMinute());
                binding.removeTime.setVisibility(View.VISIBLE);
                binding.timeTVBS.setText(selectedTime.toString());
            });
            timePicker.addOnDismissListener(dialog -> isOpenTime = false);
            isOpenTime = true;
            timePicker.show(getParentFragmentManager(), getTag());
        }
    }

    private void openDatePicker() {
        if (!isOpenDate) {
            ZonedDateTime zonedDateTime = selectedDate.atStartOfDay(ZoneOffset.UTC);
            long milliseconds = zonedDateTime.toInstant().toEpochMilli();

            MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
            builder.setTitleText("Установить дату");
            builder.setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR);
            builder.setSelection(milliseconds);
            builder.setTheme(R.style.DatePicker);
            MaterialDatePicker<Long> datePicker = builder.build();
            datePicker.addOnPositiveButtonClickListener(selection -> {
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                calendar.setTimeInMillis(selection);
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                selectedDate = LocalDate.of(year, month + 1, dayOfMonth);
                binding.dateTVBS.setText(selectedDate.toString());
            });
            datePicker.addOnDismissListener(dialog -> isOpenDate = false);
            isOpenDate = true;
            datePicker.show(getParentFragmentManager(), getTag());
        }
    }

    private void showToast() {
        Toast toast = Toast.makeText(requireContext(), "Название не может быть пустым", Toast.LENGTH_SHORT);
        View view = toast.getView();
        assert view != null;
        view.setBackgroundColor(Color.parseColor("#333333"));
        TextView text = view.findViewById(android.R.id.message);
        text.setTextColor(Color.WHITE);
        text.setGravity(Gravity.CENTER);
        toast.show();
    }

    private void resetTime() {
        if (selectedTime != null) {
            selectedTime = null;
            binding.timeTVBS.setText("Нет");
            binding.removeTime.setVisibility(View.GONE);
        }
    }

}