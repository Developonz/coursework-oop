package com.example.planner.ui.tasks;

import android.app.Dialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.planner.MainActivity;
import com.example.planner.R;
import com.example.planner.databinding.FragmentTasksBinding;
import java.time.LocalDate;
import java.util.ArrayList;


public class TasksFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    RecyclerView recyclerView;
    private ArrayList<Button> categoriesBtn;
    private Button activeCategory;
    private String[] categoriesTitle = {"Все", "Личное", "Учёба", "Работа", "Желания"};
    private String[] priorities = {"Важно - срочно", "Важно - не срочно", "Не важно - срочно", "Не важно - не срочно"};
    private String[] category = {"Без", "Личное", "Учёба", "Работа", "Желания"};
    private Dialog men;
    private ArrayList<Task> taskList = new ArrayList<>();;
    LocalDate selectedDate;
    TasksRecyclerViewAdapter adapter;


    public TasksFragment() {
    }

    @SuppressWarnings("unused")
    public static TasksFragment newInstance(int columnCount) {
        TasksFragment fragment = new TasksFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentTasksBinding binding = FragmentTasksBinding.inflate(inflater);

        View view = binding.getRoot();

        selectedDate = LocalDate.now();



        Toolbar toolbar = binding.toolbar;
        ((MainActivity) requireActivity()).setSupportActionBar(toolbar);

        LayoutInflater inflater1 = LayoutInflater.from(((MainActivity) requireActivity()));

        View customToolbar = inflater1.inflate(R.layout.custom_toolbar_layout, null);

        ActionBar actionBar = ((MainActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setTitle("");
        }

        toolbar.addView(customToolbar);

        categoriesBtn = new ArrayList<>();
        for (String title : categoriesTitle) {
            categoriesBtn.add(createNavBtn(title, customToolbar));
        }
        activeCategory = categoriesBtn.get(0);

        ViewCompat.setBackgroundTintList(activeCategory,
                ContextCompat.getColorStateList(customToolbar.getContext(), R.color.teal_700));
        Drawable drawable = toolbar.getOverflowIcon();
        if (drawable != null) {
            drawable.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        }

        Context context = view.getContext();
        recyclerView = (RecyclerView) (view.findViewById(R.id.list));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new TasksRecyclerViewAdapter(taskList, (String) activeCategory.getText());
        recyclerView.setAdapter(adapter);

        men = new Dialog(((MainActivity) requireActivity()));
        men.setContentView(R.layout.create_task_menu);
        Spinner spinner = (Spinner) men.findViewById(R.id.categoryNewTask);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(((MainActivity) requireActivity()), android.R.layout.simple_spinner_item, category);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter1);
        Spinner spinner1 = (Spinner) men.findViewById(R.id.priorityNewTask);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(((MainActivity) requireActivity()), android.R.layout.simple_spinner_item, priorities);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter2);


        binding.addTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Spinner) men.findViewById(R.id.categoryNewTask)).setSelection(categoriesBtn.indexOf(activeCategory));
                ((EditText) men.findViewById(R.id.titleNewTask)).setText("");
                men.show();
            }
        });

        ((CalendarView) men.findViewById(R.id.calendarViewNewTask)).setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                selectedDate = LocalDate.of(year, month + 1, dayOfMonth);
            }
        });

        men.findViewById(R.id.createTaskBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = ((EditText) men.findViewById(R.id.titleNewTask)).getText().toString();
                String category = (String) ((Spinner) men.findViewById(R.id.categoryNewTask)).getSelectedItem();
                String priority = (String) ((Spinner) men.findViewById(R.id.priorityNewTask)).getSelectedItem();
                Task task = new Task(title, selectedDate, priority, category);
                ((EditText) men.findViewById(R.id.titleNewTask)).setText("");
                adapter.addItem(task);

            }
        });
        return view;
    }
    public Button createNavBtn(String text, View v) {

        ContextThemeWrapper newContext = new ContextThemeWrapper(v.getContext(), R.style.customButtonCategory);
        Button button = new Button(newContext);
        button.setText(text);
        button.setId(View.generateViewId());

        int margin = getResources().getDimensionPixelSize(R.dimen.button_margin);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 0, margin, 0);
        button.setLayoutParams(layoutParams);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activeCategory != null) {
                    ViewCompat.setBackgroundTintList(activeCategory,
                            ContextCompat.getColorStateList(v.getContext(), R.color.teal_200));
                }
                activeCategory = button;
                ViewCompat.setBackgroundTintList(activeCategory,
                        ContextCompat.getColorStateList(v.getContext(), R.color.teal_700));
                adapter.changeCategory((String) activeCategory.getText());
            }
        });

        LinearLayout layout = (LinearLayout) v.findViewById(R.id.toolbarBtns);
        layout.addView(button);
        return button;
    }
}


