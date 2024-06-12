package com.example.planner.ui.notes;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import com.example.planner.controllers.NotesController;
import com.example.planner.databinding.FragmentNotesItemBinding;
import com.example.planner.databinding.HeaderListBinding;
import com.example.planner.listeners.OnItemNoteRecyclerClickListener;
import com.example.planner.models.Note;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class NotesRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private final List<Object> items;
    private final List<Note> filteredList;
    private int category = 0;
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_NOTE = 1;
    private OnItemNoteRecyclerClickListener listener;
    private final NotesController controller;
    private final String[] categoriesTitle = {"Все", "Личное", "Учёба", "Работа", "Желания"};

    public NotesRecyclerViewAdapter(NotesController controller, OnItemNoteRecyclerClickListener listener) {
        this.controller = controller;
        items = new ArrayList<>();
        filteredList = new ArrayList<>();
        this.listener = listener;
        filteredList.addAll(controller.getNotes());
        updateNotesList();
    }


    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof Note) {
            return VIEW_TYPE_NOTE;
        } else  {
            return VIEW_TYPE_HEADER;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_HEADER) {
            return new HeaderHolder(HeaderListBinding.inflate(inflater, parent, false));
        } else  {
            return new NoteHolder(FragmentNotesItemBinding.inflate(inflater, parent, false), listener);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderHolder) {
            ((HeaderHolder) holder).header.setText(((Note) items.get(position + 1)).getStringDate());
        } else if (holder instanceof NoteHolder) {
            Note note = (Note) items.get(position);
            ((NoteHolder) holder).mContentView.setText(!note.getTitle().isEmpty() ? note.getTitle() : note.getContent());
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateNotesList() {
        items.clear();
        if (filteredList.isEmpty()) {
            notifyDataSetChanged();
            return;
        }
        filteredList.sort(Comparator.comparing(Note::getDateLastChange).reversed());
        String currentHeader = "";
        for (Note note : filteredList) {
            if (category == 0 || note.getCategory().equals(categoriesTitle[category])) {
                String noteDate = note.getStringDate();
                if (!noteDate.equals(currentHeader)) {
                    currentHeader = noteDate;
                    items.add("");
                }
                items.add(note);
            }
        }
        notifyDataSetChanged();
    }

    public void resetNotesList() {
        filteredList.clear();
        filteredList.addAll(controller.getNotes());
        updateNotesList();
    }

    public void addItem(Note note) {
        filteredList.add(note);
        controller.addNote(note);
        updateNotesList();
    }

    public void removeItem(Note note) {
        filteredList.remove(note);
        controller.removeNote(note);
        updateNotesList();
    }

    public void updateNotes(Note note) {
        controller.updateNote(note);
        updateNotesList();
    }
    
    public void changeCategory(int category) {
        this.category = category;
        updateNotesList();
    }

    public Note getNote(int position) {
        if (items.get(position) instanceof Note) {
            return (Note) items.get(position);
        }
        return null;
    }

    @Override
    public Filter getFilter() {
        return myFilter;
    }

    private final Filter myFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            filteredList.clear();

            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(controller.getNotes());
            } else {
                for (Note note : controller.getNotes()) {
                    String str = !note.getTitle().isEmpty() ? note.getTitle() : note.getContent();
                    if (str.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        filteredList.add(note);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            updateNotesList();
        }
    };


    public static class HeaderHolder extends RecyclerView.ViewHolder {
        public final TextView header;

        public HeaderHolder(HeaderListBinding binding) {
            super(binding.getRoot());
            header = binding.headerList;
        }
    }

    public static class NoteHolder extends RecyclerView.ViewHolder {
        public final TextView mContentView;


        public NoteHolder(FragmentNotesItemBinding binding, OnItemNoteRecyclerClickListener listener) {
            super(binding.getRoot());
            mContentView = binding.titleNoteItem;


            binding.contentPanel.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemViewClick(position);
                    }
                }
            });
        }
    }
}
