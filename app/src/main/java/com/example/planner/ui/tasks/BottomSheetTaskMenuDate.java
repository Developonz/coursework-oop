package com.example.planner.ui.tasks;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.planner.R;
import com.example.planner.databinding.TaskMenuDateBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

public class BottomSheetTaskMenuDate extends BottomSheetDialogFragment {
    public interface OnDateSelectedListener {
        void onDateSelected(LocalDate newDate, LocalTime newTime);
    }

    private TaskMenuDateBinding binding;
    private LocalDate selectedDate;
    private LocalTime selectedTime;
    private OnDateSelectedListener onDateSelectedListener;
    private BottomSheetTaskMenuInfo bottomSheetTaskMenuInfo;

    public BottomSheetTaskMenuDate(BottomSheetTaskMenuInfo bottomSheetTaskMenuInfo, LocalDate selectedDate, OnDateSelectedListener onDateSelectedListener) {
        this.selectedDate = selectedDate;
        this.onDateSelectedListener = onDateSelectedListener;
        this.bottomSheetTaskMenuInfo = bottomSheetTaskMenuInfo;
    }

    @Override
    public BottomSheetDialog onCreateDialog(Bundle savedInstanceState) {
        return new BottomSheetDialog(getActivity(), getTheme()){
            @Override
            public void onBackPressed() {
                super.onBackPressed();
                onDateSelectedListener.onDateSelected(selectedDate, selectedTime);
                dismiss();
                bottomSheetTaskMenuInfo.show(getParentFragmentManager(), getTag());
            }
        };
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = TaskMenuDateBinding.inflate(getLayoutInflater());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setupCalendar();
        setupTimePicker();
        return binding.getRoot();
    }

    private void setupTimePicker() {
        binding.removeTime.setOnClickListener(v -> {
            if (selectedTime != null) {
                selectedTime = null;
                binding.timeTVBS.setText("Нет");
                binding.removeTime.setVisibility(View.GONE);
            }
        });

        binding.timeLL.setOnClickListener(v -> {
            MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                    .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(12)
                    .setMinute(0)
                    .setTitleText("Установить время")
                    .build();

            timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedTime = LocalTime.of(timePicker.getHour(), timePicker.getMinute());
                    binding.removeTime.setVisibility(View.VISIBLE);
                    binding.timeTVBS.setText(selectedTime.toString());
                    timePicker.dismiss();
                }
            });

            timePicker.show(getParentFragmentManager(), getTag());
        });
    }

    @Override
    public int getTheme() {
        return R.style.AppBottomSheetDialogTheme;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View bottomSheet = getDialog().findViewById(com.google.android.material.R.id.design_bottom_sheet);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setDraggable(false);
    }


    private void setupCalendar() {
        binding.calendarViewNewTask.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = LocalDate.of(year, month + 1, dayOfMonth);
        });
        binding.calendarViewNewTask.setDate(selectedDate.toEpochDay() * 24 * 60 * 60 * 1000);
    }

    public void onBackPressed() {
        Log.i("test", "backPressed");

    }
}


