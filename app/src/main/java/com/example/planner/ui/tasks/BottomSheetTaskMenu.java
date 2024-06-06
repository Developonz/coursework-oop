package com.example.planner.ui.tasks;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.planner.R;
import com.example.planner.controllers.DBWorker;
import com.example.planner.controllers.TasksController;
import com.example.planner.databinding.TaskMenuInfoBinding;
import com.example.planner.models.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class BottomSheetTaskMenu extends BottomSheetDialogFragment {
    private static boolean isOpenedThis = false;
    private TaskMenuInfoBinding binding;
    private LocalDate selectedDate;
    private LocalTime selectedTime;
    private TasksRecyclerViewAdapter adapter;
    private boolean isUpdateMode = false;
    private boolean isOpenDate = false;
    private boolean isOpenTime = false;
    private Task taskToUpdate;
    private String selectedCategory;
    private String selectedPriority;
    private Context context;
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();


    private BottomSheetTaskMenu(Context context, TasksRecyclerViewAdapter adapter, String category) {
        this.adapter = adapter;
        selectedDate = LocalDate.now();
        selectedCategory = category;
        this.context = context;
    }

    private BottomSheetTaskMenu(Context context, TasksRecyclerViewAdapter adapter, Task taskToUpdate) {
        this.adapter = adapter;
        this.taskToUpdate = taskToUpdate;
        this.isUpdateMode = true;
        selectedDate = taskToUpdate.getTaskDate();
        selectedTime = taskToUpdate.getTaskTime();
        this.context = context;
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
        View bottomSheet = getDialog().findViewById(com.google.android.material.R.id.design_bottom_sheet);
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

    public static BottomSheetTaskMenu getInstance(Context context, TasksRecyclerViewAdapter adapter, String category) {
        if (!isOpenedThis) {
            isOpenedThis = true;
            return new BottomSheetTaskMenu(context, adapter, category);
        } else {
            return null;
        }
    }

    public static BottomSheetTaskMenu getInstance(Context context, TasksRecyclerViewAdapter adapter, Task taskToUpdate) {
        if (!isOpenedThis) {
            isOpenedThis = true;
            return new BottomSheetTaskMenu(context, adapter, taskToUpdate);
        } else {
            return null;
        }
    }

    private void changeModeKeyBoarding() {
        binding.getRoot().getViewTreeObserver().addOnWindowFocusChangeListener(new ViewTreeObserver.OnWindowFocusChangeListener() {
            @Override
            public void onWindowFocusChanged(boolean hasFocus) {
                if (hasFocus) {
                    if (getDialog() != null && getDialog().getWindow() != null) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                            }
                        }, 100);

                    }
                } else {
                    if (getDialog() != null && getDialog().getWindow() != null) {
                        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
                    }
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
            String title = binding.titleNewTask.getText().toString();
            if (!title.isEmpty()) {
                if (isUpdateMode) {
                    updateTask();
                } else {
                    createTask();
                }
                resetTime();
            } else {
                showToast("Название не может быть пустым");
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

            getActivity().runOnUiThread(() -> {
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

    /*private void updateDropDownLists() {
        Log.i("test", "updateDropDown");
        setupDropDown(binding.categoryNewTask, R.array.categories, selectedCategory);
        setupDropDown(binding.priorityNewTask, R.array.priorities, selectedPriority);
    }*/

    private void createTask() {
        String title = binding.titleNewTask.getText().toString();
        String category = binding.categoryNewTask.getText().toString();
        String priority = binding.priorityNewTask.getText().toString();
        Task task = new Task(title, selectedDate, selectedTime, priority, category);
        binding.titleNewTask.setText("");
        adapter.addItem(task);
    }

    private void updateTask() {
        taskToUpdate.setTitle(binding.titleNewTask.getText().toString());
        taskToUpdate.setCategory(binding.categoryNewTask.getText().toString());
        taskToUpdate.setPriority(binding.priorityNewTask.getText().toString());
        taskToUpdate.setTaskDate(selectedDate);
        taskToUpdate.setTaskTime(selectedTime);
        adapter.updateTask(taskToUpdate);
        dismiss();
    }

    private void openTimePicker() {
        if (!isOpenTime) {
            MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                    .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour((selectedTime != null) ? selectedTime.getHour() : 12)
                    .setMinute((selectedTime != null) ? selectedTime.getMinute() : 0)
                    .setTitleText("Установить время")
                    .setTheme(R.style.TimePicker)
                    .build();

            timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedTime = LocalTime.of(timePicker.getHour(), timePicker.getMinute());
                    binding.removeTime.setVisibility(View.VISIBLE);
                    binding.timeTVBS.setText(selectedTime.toString());
                }
            });
            timePicker.addOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    isOpenTime = false;
                }
            });
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
            datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                @Override
                public void onPositiveButtonClick(Long selection) {
                    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                    calendar.setTimeInMillis(selection);
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH);
                    int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                    selectedDate = LocalDate.of(year, month + 1, dayOfMonth);
                    binding.dateTVBS.setText(selectedDate.toString());
                }
            });
            datePicker.addOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    isOpenDate = false;
                }
            });
            isOpenDate = true;
            datePicker.show(getParentFragmentManager(), getTag());
        }
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT);
        View view = toast.getView();
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