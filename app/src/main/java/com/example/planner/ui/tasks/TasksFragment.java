package com.example.planner.ui.tasks;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import com.example.planner.MainActivity;
import com.example.planner.R;
import com.example.planner.databinding.FragmentTasksBinding;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;


public class TasksFragment extends Fragment implements OnItemTaskRecyclerClickListener {

    private ArrayList<Button> categoriesBtn;
    private Button activeCategory;
    private final String[] categoriesTitle = {"Все", "Личное", "Учёба", "Работа", "Желания"};
    private ArrayList<Task> taskList = new ArrayList<>();
    private TasksRecyclerViewAdapter adapter;
    private FragmentTasksBinding binding;
    private RecyclerView recyclerView;
    private Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentTasksBinding.inflate(this.getLayoutInflater());
        toolbar = binding.toolbar;
        ((MainActivity) requireActivity()).setSupportActionBar(toolbar);

        setupToolbar();
        setupCategories();
        setupRecyclerView();

        binding.addTaskBtn.setOnClickListener(v -> {
            BottomSheetTaskMenuInfo bottomSheetTaskMenuInfo = BottomSheetTaskMenuInfo.getInstance(adapter, getCategory(categoriesBtn.indexOf(activeCategory)));
            if (bottomSheetTaskMenuInfo != null)
                bottomSheetTaskMenuInfo.show(getParentFragmentManager(), "BottomSheetTaskMenu");
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setupOptionsMenu();
        return binding.getRoot();
    }

    public void setupOptionsMenu() {
        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                Log.i("test", "onCreateMenu called");
                menuInflater.inflate(R.menu.main, menu);
                MenuItem searchItem = menu.findItem(R.id.action_search);
                SearchView searchView = (SearchView) searchItem.getActionView();
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        adapter.getFilter().filter(newText);
                        return false;
                    }
                });
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.action_search) {
                    return true;
                } else if (id == R.id.action_sort) {
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    @Override
    public void onItemCheckBoxClick(int position) {
        Task task = adapter.getTask(position);
        adapter.removeItem(task);
        Snackbar.make(recyclerView, "Выполнено", Snackbar.LENGTH_LONG)
                .setAnchorView(((MainActivity) requireActivity()).findViewById(R.id.bottom_navigation))
                .setAction("Отменить", v -> {
                    adapter.addItem(task);
                    adapter.updateTasksList();
                }).show();
    }

    @Override
    public void onItemViewClick(int position) {
        Task task = adapter.getTask(position);
        BottomSheetTaskMenuInfo bottomSheetTaskMenuInfo = BottomSheetTaskMenuInfo.getInstance(adapter, task);
        if (bottomSheetTaskMenuInfo != null)
            bottomSheetTaskMenuInfo.show(getParentFragmentManager(), bottomSheetTaskMenuInfo.getTag());
    }

    private void setupToolbar() {
        View customToolbar = this.getLayoutInflater().inflate(R.layout.custom_toolbar_layout, null);
        toolbar.addView(customToolbar);
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

    private void setupRecyclerView() {
        recyclerView = binding.list;
        recyclerView.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        adapter = new TasksRecyclerViewAdapter(taskList, (String) activeCategory.getText(), this);
        recyclerView.setAdapter(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TaskItemTouchHelper(adapter, recyclerView, getActivity()));
        itemTouchHelper.attachToRecyclerView(recyclerView);
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
