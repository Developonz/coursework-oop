package com.example.planner.ui.tasks;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.TooltipCompat;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.example.planner.MainActivity;
import com.example.planner.R;
import com.example.planner.databinding.FragmentTasksBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.lang.reflect.Field;
import java.util.ArrayList;


public class TasksFragment extends Fragment implements OnItemTaskRecyclerClickListener {

    private String activeCategory;
    private final String[] categoriesTitle = {"Все", "Личное", "Учёба", "Работа", "Желания"};
    private ArrayList<Task> taskList = new ArrayList<>();
    private TasksRecyclerViewAdapter adapter;
    private FragmentTasksBinding binding;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private TabLayout tabLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentTasksBinding.inflate(this.getLayoutInflater());
        setupToolbar();
        setupCategories();
        setupRecyclerView();

        binding.addTaskBtn.setOnClickListener(v -> {
            String categpry = activeCategory.equals("Все") ? "Без категории" : activeCategory;
            BottomSheetTaskMenuInfo bottomSheetTaskMenuInfo = BottomSheetTaskMenuInfo.getInstance(adapter, categpry);
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
        toolbar = binding.toolbar;
        toolbar.setTitle("Задачи");
        ((MainActivity) requireActivity()).setSupportActionBar(toolbar);
    }

    private void setupCategories() {
        tabLayout = binding.tabLayout;

        for (String title : categoriesTitle) {
            TabLayout.Tab tab = tabLayout.newTab().setText(title);
            tab.view.setOnLongClickListener(view -> true);
            tabLayout.addTab(tab);
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                activeCategory = (String) tab.getText();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
        activeCategory = categoriesTitle[0];
    }

    private void setupRecyclerView() {
        recyclerView = binding.list;
        recyclerView.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        adapter = new TasksRecyclerViewAdapter(taskList, activeCategory, this);
        recyclerView.setAdapter(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TaskItemTouchHelper(adapter, recyclerView, getActivity()));
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
}
