package com.example.planner.ui.tasks;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.planner.databinding.TaskMenuDateBinding;
import com.example.planner.databinding.TaskMenuInfoBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.time.LocalDate;

public class BottomSheetTaskMenuDate extends BottomSheetDialogFragment {
    public interface OnDateSelectedListener {
        void onDateSelected(LocalDate newDate);
    }

    private TaskMenuDateBinding binding;
    private LocalDate selectedDate;
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
                onDateSelectedListener.onDateSelected(selectedDate);
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
        return binding.getRoot();
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


