package com.example.planner.ui.notes;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;

import com.example.planner.MainActivity;
import com.example.planner.R;
import com.example.planner.controllers.notes.NotesController;
import com.example.planner.databinding.FragmentNoteMenuBinding;
import com.example.planner.models.Note;

import java.time.LocalDate;
import java.util.Objects;

public class NoteMenuFragment extends Fragment {
    private FragmentNoteMenuBinding binding;
    private NotesController controller;
    private Note note;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentNoteMenuBinding.inflate(this.getLayoutInflater());
        controller = new NotesController(requireActivity(), new ViewModelProvider(requireActivity()).get(NotesViewModel.class));
        controller.loadNotes();
        setupToolbar();
        String[] categories = getResources().getStringArray(R.array.categories);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.category.setAdapter(adapter);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setupOptionsMenu();
        Bundle args = getArguments();
        if (args != null && args.containsKey("note")) {
            note = controller.getNote(NoteMenuFragmentArgs.fromBundle(args).getNote());
            binding.noteTitle.setText(note.getTitle());
            binding.noteContent.setText(note.getContent());
            String[] categories = getResources().getStringArray(R.array.categories);
            for (int i = 0; i < categories.length; ++i) {
                if (categories[i].equals(note.getCategory())) {
                    binding.category.setSelection(i);
                }
            }
        } else {
            note = new Note();
            binding.category.setSelection(0);
        }
        binding.noteDate.setText(note.getDateLastChange().toString());
        return binding.getRoot();
    }


    @Override
    public void onPause() {
        super.onPause();
        if (!(binding.noteTitle.getText().toString().isEmpty() && binding.noteContent.getText().toString().isEmpty())) {
            note.setTitle(binding.noteTitle.getText().toString());
            note.setContent(binding.noteContent.getText().toString());
            note.setCategory(binding.category.getSelectedItem().toString());
            note.setDateLastChange(LocalDate.parse(binding.noteDate.getText().toString()));
            if (note.getId() != -1) {
                controller.updateNote(note);
            } else {
                controller.addNote(note);
            }
        } else {
            if (note.getId() != -1) {
                controller.removeNote(note);
            }
        }
        closeKeyboard();
    }

    private void setupToolbar() {
        Toolbar toolbar = binding.toolbar;
        ((MainActivity) requireActivity()).setSupportActionBar(toolbar);
        Objects.requireNonNull(((MainActivity) requireActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    public void setupOptionsMenu() {
        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.empty_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == android.R.id.home) {
                    requireActivity().onBackPressed();
                    return true;
                }
                return false;
            }

        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private void closeKeyboard() {
        View view = requireActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
