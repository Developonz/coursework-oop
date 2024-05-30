package com.example.planner.ui.tasks;

import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.planner.MainActivity;
import com.example.planner.R;
import com.example.planner.databinding.FragmentTasksBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDate;
import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class TasksFragment extends Fragment implements OnItemRecyclerClickListener {

    private ArrayList<Button> categoriesBtn;
    private Button activeCategory;
    private final String[] categoriesTitle = {"Все", "Личное", "Учёба", "Работа", "Желания"};
    private ArrayList<Task> taskList = new ArrayList<>();
    private LocalDate selectedDate = LocalDate.now();
    private TasksRecyclerViewAdapter adapter;
    private FragmentTasksBinding binding;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private BottomSheetDialog taskDialog;

    private ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            if (viewHolder instanceof TasksRecyclerViewAdapter.TaskHolder) {
                return makeMovementFlags(0, ItemTouchHelper.LEFT);
            } else {
                return 0;
            }
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
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(viewHolder.itemView.getContext(), R.color.red))
                    .setSwipeLeftLabelColor(ContextCompat.getColor(viewHolder.itemView.getContext(), R.color.white))
                    .setSwipeLeftLabelTextSize(TypedValue.COMPLEX_UNIT_SP, 16) // Размер текста
                    .setSwipeLeftLabelTypeface(Typeface.DEFAULT_BOLD) // Шрифт текста
                    .addSwipeLeftLabel("Удалить")
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        @Override
        public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
            return 0.6f;
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

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        recyclerView = binding.list;
        recyclerView.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        adapter = new TasksRecyclerViewAdapter(taskList, (String) activeCategory.getText(), this);
        recyclerView.setAdapter(adapter);

        BottomSheetTaskMenuInfo bottomSheetTaskMenuInfo = new BottomSheetTaskMenuInfo(adapter);

        binding.addTaskBtn.setOnClickListener(v -> {
            bottomSheetTaskMenuInfo.show(getParentFragmentManager(), bottomSheetTaskMenuInfo.getTag());
            /*((AutoCompleteTextView) taskDialog.findViewById(R.id.categoryNewTask)).setSelection(categoriesBtn.indexOf(activeCategory));


            ((TextInputEditText) taskDialog.findViewById(R.id.titleNewTask)).setText("");

            taskDialog.show();*/
        });

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

        /*Dialog createTaskUpdateDialog = updateTaskDialog(task);*/
        /*createTaskUpdateDialog.show();*/
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

    /*private BottomSheetDialog createTaskDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(((MainActivity) requireActivity()), R.style.AppBottomSheetDialogTheme);
        dialog.setContentView(R.layout.task_menu);
        *//*dialog.getBehavior().setDraggable(false);*//*

        selectedDate = LocalDate.now();
        CalendarView calendarView = initCalendar(dialog);

        AutoCompleteTextView categoryATV = dialog.findViewById(R.id.categoryNewTask);
        String[] items = getResources().getStringArray(R.array.categories);
        setupDropDown(categoryATV, items, items[0]);

        AutoCompleteTextView priorityATV = dialog.findViewById(R.id.priorityNewTask);
        items = getResources().getStringArray(R.array.priorities);
        setupDropDown(priorityATV, items, items[0]);

        ((TextView) dialog.findViewById(R.id.dateTVBS)).setText(selectedDate.toString());

        dialog.findViewById(R.id.createTaskBtn).setOnClickListener(v -> {
            String title = ((EditText) dialog.findViewById(R.id.titleNewTask)).getText().toString();
            if (!(title.isEmpty())) {
                String category =  categoryATV.getText().toString();
                String priority =  priorityATV.getText().toString();
                Task task = new Task(title, selectedDate, priority, category);
                ((EditText) dialog.findViewById(R.id.titleNewTask)).setText("");
                adapter.addItem(task);
            } else {
                Toast toast = Toast.makeText(requireContext(), "Название не может быть пустым", Toast.LENGTH_LONG);
                View view = toast.getView();
                view.setBackgroundColor(Color.parseColor("#333333"));
                TextView text = view.findViewById(android.R.id.message);
                text.setTextColor(Color.WHITE);
                text.setGravity(Gravity.CENTER);
                toast.show();
            }
        });

        dialog.setOnKeyListener((dialog1, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                if (dialog.findViewById(R.id.info_bottom_sheet).getVisibility() == View.GONE) {
                    dialog.findViewById(R.id.info_bottom_sheet).setVisibility(View.VISIBLE);
                    dialog.findViewById(R.id.deadline_bottom_sheet).setVisibility(View.GONE);
                    ((TextView) dialog.findViewById(R.id.dateTVBS)).setText(selectedDate.toString());
                } else {
                    dialog.dismiss();
                }
                return true;
            }
            return false;
        });

        dialog.findViewById(R.id.dateLL).setOnClickListener(v -> {
            dialog.findViewById(R.id.info_bottom_sheet).setVisibility(View.GONE);
            dialog.findViewById(R.id.deadline_bottom_sheet).setVisibility(View.VISIBLE);
        });

        return dialog;
    }

    private Dialog updateTaskDialog(Task task) {
        BottomSheetDialog dialog = new BottomSheetDialog(((MainActivity) requireActivity()), R.style.AppBottomSheetDialogTheme);
        dialog.setContentView(R.layout.task_menu);
        *//*dialog.getBehavior().setDraggable(false);*//*

        selectedDate = task.getTaskDate();
        CalendarView calendarView = initCalendar(dialog);
        calendarView.setDate(task.getTaskDate().toEpochDay() * 24 * 60 * 60 * 1000);


        AutoCompleteTextView categoryATV = dialog.findViewById(R.id.categoryNewTask);
        String[] items = getResources().getStringArray(R.array.categories);
        setupDropDown(categoryATV, items, task.getCategory());

        AutoCompleteTextView priorityATV = dialog.findViewById(R.id.priorityNewTask);
        items = getResources().getStringArray(R.array.priorities);
        setupDropDown(priorityATV, items, task.getPriority());


        ((EditText) dialog.findViewById(R.id.titleNewTask)).setText(task.getTitle());
        ((Button) dialog.findViewById(R.id.createTaskBtn)).setText("Изменить");

        dialog.findViewById(R.id.createTaskBtn).setOnClickListener(v -> {
            String title = ((EditText) dialog.findViewById(R.id.titleNewTask)).getText().toString();
            if (!title.isEmpty()) {
                task.setTitle(title);
                task.setCategory(categoryATV.getText().toString());
                task.setPriority(priorityATV.getText().toString());
                task.setTaskDate(selectedDate);
                adapter.generateItems();
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            } else {
                Toast toast = Toast.makeText(requireContext(), "Название не может быть пустым", Toast.LENGTH_LONG);
                View view = toast.getView();
                view.setBackgroundColor(Color.parseColor("#333333"));
                TextView text = view.findViewById(android.R.id.message);
                text.setTextColor(Color.WHITE);
                text.setGravity(Gravity.CENTER);
                toast.show();
            }
        });
        return dialog;
    }


    private void setupDropDown(AutoCompleteTextView dropDown, String[] items, String elem) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(((MainActivity) requireActivity()), android.R.layout.simple_dropdown_item_1line, items);
        dropDown.setAdapter(adapter);
        dropDown.setText(elem, false);
    }

    private CalendarView initCalendar(BottomSheetDialog dialog) {
        CalendarView calendar = (CalendarView) (dialog.findViewById(R.id.calendarViewNewTask));
        calendar.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = LocalDate.of(year, month + 1, dayOfMonth);
        });
        return calendar;
    }*/
}
