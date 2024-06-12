package com.example.planner.ui.notes;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
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
import com.example.planner.controllers.NotesController;
import com.example.planner.databinding.FragmentNotesBinding;
import com.example.planner.listeners.OnItemNoteRecyclerClickListener;
import com.example.planner.models.Note;
import com.google.android.material.tabs.TabLayout;

import java.time.LocalDate;
import java.util.List;


public class NotesFragment extends Fragment implements OnItemNoteRecyclerClickListener {
    private NotesRecyclerViewAdapter adapter;
    private FragmentNotesBinding binding;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private NotesController controller;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentNotesBinding.inflate(this.getLayoutInflater());
        controller = new NotesController(requireActivity(), new ViewModelProvider(requireActivity()).get(NotesViewModel.class));

        setupToolbar();
        setupCategories();
        setupRecyclerView();

        controller.getViewModel().getList().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> note) {
                adapter.resetNotesList();
            }
        });

        binding.addNoteBtn.setOnClickListener(v -> openNoteMenu());
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setupOptionsMenu();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        controller.loadNotes();
    }


    private void openNoteMenu() {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
        navController.navigate(R.id.openNoteMenu);
    }


    public void setupOptionsMenu() {
        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                Log.i("test", "onCreateMenu called");
                menuInflater.inflate(R.menu.search_menu, menu);
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
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    @Override
    public void onItemViewClick(int position) {
        Note note = adapter.getNote(position);
        NotesFragmentDirections.OpenNoteMenu action = NotesFragmentDirections.openNoteMenu(note.getId());
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
        navController.navigate(action);
    }

    private void setupToolbar() {
        toolbar = binding.toolbar;
        ((MainActivity) requireActivity()).setSupportActionBar(toolbar);
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
        adapter = new NotesRecyclerViewAdapter(controller, this);
        recyclerView.setAdapter(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new NoteItemTouchHelper(adapter, recyclerView, getActivity()));
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
}
