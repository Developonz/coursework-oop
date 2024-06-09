package com.example.planner.ui.tasks;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
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
import com.example.planner.controllers.DBWorker;
import com.example.planner.controllers.TasksController;
import com.example.planner.databinding.FragmentTasksBinding;
import com.example.planner.databinding.FragmentTasksCompleteBinding;
import com.example.planner.listeners.OnItemLinkRecyclerClickListener;
import com.example.planner.listeners.OnItemTaskRecyclerClickListener;
import com.example.planner.models.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.time.LocalDate;
import java.util.List;


public class CompleteTasksFragment extends Fragment {
    private TasksRecyclerViewAdapter adapter;
    private FragmentTasksCompleteBinding binding;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private TasksController controller;
    private SortDialog sortDialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentTasksCompleteBinding.inflate(this.getLayoutInflater());
        controller = new TasksController(requireActivity(), new ViewModelProvider(requireActivity()).get(TasksViewModel.class));
        controller.loadTasks(true);
        sortDialog = new SortDialog(requireActivity());

        setupToolbar();
        setupCategories();
        setupRecyclerView();

        controller.getViewModel().getList().observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                adapter.resetTasksList();
            }
        });
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setupOptionsMenu();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        controller.loadTasks(true);
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
                    sortDialog.showDialog(adapter);
                    return true;
                } else if (id == android.R.id.home) {
                    getActivity().onBackPressed();
                    return true;
                }
                return false;
            }

        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private void setupToolbar() {
        toolbar = binding.toolbar;
        ((MainActivity) requireActivity()).setSupportActionBar(toolbar);
        ((MainActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupCategories() {
        tabLayout = binding.tabLayout;
        final String[] categoriesTitle = {"Все", "Личное", "Учёба", "Работа", "Желания"};
        for (String title : categoriesTitle) {
            TabLayout.Tab tab = tabLayout.newTab().setText(title);
            tab.view.setOnLongClickListener(view -> true);
            tabLayout.addTab(tab);
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                adapter.changeCategory(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupRecyclerView() {
        recyclerView = binding.list;
        recyclerView.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        adapter = new TasksRecyclerViewAdapter(controller, true);
        recyclerView.setAdapter(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TaskItemTouchHelper(adapter, recyclerView, getActivity()));
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
}
