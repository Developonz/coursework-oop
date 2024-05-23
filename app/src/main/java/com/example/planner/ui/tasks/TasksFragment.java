package com.example.planner.ui.tasks;

import android.app.Dialog;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.planner.MainActivity;
import com.example.planner.R;
import com.example.planner.databinding.FragmentTasksBinding;
import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class TasksFragment extends Fragment implements OnItemRecyclerClickListener {

    private ArrayList<Button> categoriesBtn;
    private Button activeCategory;
    private final String[] categoriesTitle = {"Все", "Личное", "Учёба", "Работа", "Желания"};
    private final String[] priorities = {"Важно - срочно", "Важно - не срочно", "Не важно - срочно", "Не важно - не срочно"};
    private final String[] category = {"Без", "Личное", "Учёба", "Работа", "Желания"};
    private ArrayList<Task> taskList = new ArrayList<>();
    private LocalDate selectedDate = LocalDate.now();
    private LocalDate chosedDate = LocalDate.now();
    private TasksRecyclerViewAdapter adapter;
    private FragmentTasksBinding binding;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private Dialog taskDialog;

    private ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getAdapterPosition();
            final int pos = adapter.getTask(position);
            Task task = taskList.get(pos);
            taskList.remove(pos);
            adapter.generateItems();
            adapter.notifyDataSetChanged();
            Snackbar.make(recyclerView, "Удалено", Snackbar.LENGTH_LONG)
                    .setAnchorView(((MainActivity) requireActivity()).findViewById(R.id.bottom_navigation))
                    .setAction("Отменить", v -> {
                        taskList.add(pos, task);
                        adapter.generateItems();
                        adapter.notifyDataSetChanged();
                    }).show();
        }

        @Override
        public void onChildDraw (Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,float dX, float dY,int actionState, boolean isCurrentlyActive){
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftLabel("Удалить").setSwipeLeftLabelColor(R.color.white).setSwipeLeftLabelTextSize(0, 120)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.red))
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        @Override
        public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
            return 0.65f;
        }

        @Override
        public float getSwipeEscapeVelocity(float defaultValue) {
            return defaultValue * 10;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = FragmentTasksBinding.inflate(this.getLayoutInflater());
        toolbar = binding.toolbar;
        ((MainActivity) requireActivity()).setSupportActionBar(toolbar);

        setupToolbar();
        setupCategories();
        taskDialog = createTaskDialog();

        binding.addTaskBtn.setOnClickListener(v -> {
            ((Spinner) taskDialog.findViewById(R.id.categoryNewTask)).setSelection(categoriesBtn.indexOf(activeCategory));
            ((EditText) taskDialog.findViewById(R.id.titleNewTask)).setText("");
            taskDialog.show();
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        recyclerView = (RecyclerView) (binding.getRoot().findViewById(R.id.list));
        recyclerView.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        adapter = new TasksRecyclerViewAdapter(taskList, (String) activeCategory.getText(), this);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        return binding.getRoot();
    }

    @Override
    public void onItemCheckBoxClick(int position) {
        int pos = adapter.getTask(position);
        Task task = taskList.get(pos);
        taskList.remove(pos);
        adapter.generateItems();
        adapter.notifyDataSetChanged();
        Snackbar.make(recyclerView, "Выполнено", Snackbar.LENGTH_LONG)
                .setAnchorView(((MainActivity) requireActivity()).findViewById(R.id.bottom_navigation))
                .setAction("Отменить", v -> {
                    taskList.add(pos, task);
                    adapter.generateItems();
                    adapter.notifyDataSetChanged();
                }).show();
    }

    @Override
    public void onItemViewClick(int position) {
        int pos = adapter.getTask(position);
        Task task = taskList.get(pos);

        Dialog createTaskUpdateDialog = updateTaskDialog(task);
        createTaskUpdateDialog.show();
    }

    private void setupToolbar() {
        View customToolbar = this.getLayoutInflater().inflate(R.layout.custom_toolbar_layout, null);

        ActionBar actionBar = ((MainActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setTitle("");
        }

        toolbar.addView(customToolbar);

        Drawable drawable = toolbar.getOverflowIcon();
        if (drawable != null) {
            drawable.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        }
    }

    private void setupCategories() {
        categoriesBtn = new ArrayList<>();
        for (String title : categoriesTitle) {
            categoriesBtn.add(createNavBtn(title, toolbar));
        }
        activeCategory = categoriesBtn.get(0);
        ViewCompat.setBackgroundTintList(activeCategory,
                ContextCompat.getColorStateList(toolbar.getContext(), R.color.teal_700));
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

        button.setOnClickListener(v1 -> {
            if (activeCategory != null) {
                ViewCompat.setBackgroundTintList(activeCategory,
                        ContextCompat.getColorStateList(v1.getContext(), R.color.teal_200));
            }
            activeCategory = button;
            ViewCompat.setBackgroundTintList(activeCategory,
                    ContextCompat.getColorStateList(v1.getContext(), R.color.teal_700));
            adapter.changeCategory((String) activeCategory.getText());
        });

        LinearLayout layout = (LinearLayout) v.findViewById(R.id.toolbarBtns);
        layout.addView(button);
        return button;
    }

    private void setupSpinner(Spinner spinner, String[] set) {
        ArrayAdapter<String> SpinAdapter = new ArrayAdapter<>(((MainActivity) requireActivity()), android.R.layout.simple_spinner_item, set);
        SpinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(SpinAdapter);
    }

    private void setSpinnerSelection(Spinner spinner, String value, String[] set) {
        setupSpinner(spinner, set);
        for (int i = 0; i < spinner.getAdapter().getCount(); ++i) {
            if (spinner.getAdapter().getItem(i).toString().equals(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private Dialog createTaskDialog() {
        Dialog dialog = new Dialog(((MainActivity) requireActivity()));
        dialog.setContentView(R.layout.create_task_menu);

        Spinner categorySpin = (Spinner) dialog.findViewById(R.id.categoryNewTask);
        setupSpinner(categorySpin, category);
        Spinner prioritySpin = (Spinner) (dialog.findViewById(R.id.priorityNewTask));
        setupSpinner(prioritySpin, priorities);

        ((CalendarView) dialog.findViewById(R.id.calendarViewNewTask)).setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = LocalDate.of(year, month + 1, dayOfMonth);
        });

        dialog.findViewById(R.id.createTaskBtn).setOnClickListener(v -> {
            String title = ((EditText) dialog.findViewById(R.id.titleNewTask)).getText().toString();
            if (!(title.isEmpty())) {
                String category = (String) categorySpin.getSelectedItem();
                String priority = (String) prioritySpin.getSelectedItem();
                Task task = new Task(title, selectedDate, priority, category);
                ((EditText) dialog.findViewById(R.id.titleNewTask)).setText("");
                adapter.addItem(task);
            } else {
                Toast toast = Toast.makeText(dialog.getContext(), "Название не может быть пустым", Toast.LENGTH_SHORT);
                View decorView = dialog.getWindow().getDecorView();
                int[] location = new int[2];
                decorView.getLocationOnScreen(location);
                int x = location[0];
                int y = location[1];
                int height = decorView.getHeight();
                int bottomY = y + height;

                toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, bottomY - 85);
                toast.show();
            }
        });
        return dialog;
    }

    private Dialog updateTaskDialog(Task task) {
        Dialog dialog = new Dialog(((MainActivity) requireActivity()));
        dialog.setContentView(R.layout.create_task_menu);

        CalendarView calendarView = (CalendarView) (dialog.findViewById(R.id.calendarViewNewTask));
        chosedDate = task.getTaskDate();
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            chosedDate = LocalDate.of(year, month + 1, dayOfMonth);
        });
        calendarView.setDate(task.getTaskDate().toEpochDay() * 24 * 60 * 60 * 1000);

        Spinner categorySpin = (Spinner) (dialog.findViewById(R.id.categoryNewTask));
        setSpinnerSelection(categorySpin, task.getCategory(), category);

        Spinner prioritySpin = (Spinner) (dialog.findViewById(R.id.priorityNewTask));
        setSpinnerSelection(prioritySpin, task.getPriority(), priorities);

        ((EditText) dialog.findViewById(R.id.titleNewTask)).setText(task.getTitle());
        ((Button) dialog.findViewById(R.id.createTaskBtn)).setText("Изменить");

        dialog.findViewById(R.id.createTaskBtn).setOnClickListener(v -> {
            String title = ((EditText) dialog.findViewById(R.id.titleNewTask)).getText().toString();
            if (!title.isEmpty()) {
                String category = (String) categorySpin.getSelectedItem();
                String priority = (String) prioritySpin.getSelectedItem();
                task.setTitle(title);
                task.setCategory(category);
                task.setPriority(priority);
                task.setTaskDate(chosedDate);
                adapter.generateItems();
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            } else {
                Toast toast = Toast.makeText(dialog.getContext(), "Название не может быть пустым", Toast.LENGTH_SHORT);
                View decorView = dialog.getWindow().getDecorView();
                int[] location = new int[2];
                decorView.getLocationOnScreen(location);
                int x = location[0];
                int y = location[1];
                int height = decorView.getHeight();
                int bottomY = y + height;

                toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, bottomY - 85);
                toast.show();
            }
        });
        return dialog;
    }
}
