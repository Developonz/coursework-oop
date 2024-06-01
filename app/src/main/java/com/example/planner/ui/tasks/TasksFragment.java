package com.example.planner.ui.tasks;

import android.content.DialogInterface;
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
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.planner.MainActivity;
import com.example.planner.R;
import com.example.planner.databinding.FragmentTasksBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDate;
import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class TasksFragment extends Fragment implements OnItemRecyclerClickListener {

    private ArrayList<Button> categoriesBtn;
    private Button activeCategory;
    private final String[] categoriesTitle = {"Все", "Личное", "Учёба", "Работа", "Желания"};
    private ArrayList<Task> taskList = new ArrayList<>();
    private TasksRecyclerViewAdapter adapter;
    private FragmentTasksBinding binding;
    private RecyclerView recyclerView;
    private Toolbar toolbar;

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

        binding.addTaskBtn.setOnClickListener(v -> {
                BottomSheetTaskMenuInfo bottomSheetTaskMenuInfo = BottomSheetTaskMenuInfo.getInstance(adapter, getCategory(categoriesBtn.indexOf(activeCategory)));
                if (bottomSheetTaskMenuInfo != null)
                    bottomSheetTaskMenuInfo.show(getParentFragmentManager(), "BottomSheetTaskMenu");
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
        BottomSheetTaskMenuInfo bottomSheetTaskMenuInfo = BottomSheetTaskMenuInfo.getInstance(adapter, task);
        if (bottomSheetTaskMenuInfo != null)
            bottomSheetTaskMenuInfo.show(getParentFragmentManager(), bottomSheetTaskMenuInfo.getTag());
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

    private String getCategory(int number) {
        String[] categories = getResources().getStringArray(R.array.categories);
        String category = null;
        if (number >= 0 && number < categories.length) {
            category = categories[number];
        }
        return category;
    }
}
