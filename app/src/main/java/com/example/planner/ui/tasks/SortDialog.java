package com.example.planner.ui.tasks;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.Toast;

public class SortDialog {

    private final Context context;
    private int selectedSortType = 0;

    public SortDialog(Context context) {
        this.context = context;
    }

    public void showDialog(TasksRecyclerViewAdapter adapter) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Задачи отсортированы по");

        String[] sortTypes = {
                "По алфавиту (По возрастанию)",
                "По алфавиту (По убыванию)",
                "По приоритету (По возрастанию)",
                "По приоритету (По убыванию)"
        };

        builder.setSingleChoiceItems(sortTypes, selectedSortType, (dialog, which) -> selectedSortType = which);

        // Кнопка "ВЫБОР"
        builder.setPositiveButton("ВЫБОР", (dialog, id) -> {
            Toast.makeText(context, "Выбран тип сортировки: " + sortTypes[selectedSortType], Toast.LENGTH_SHORT).show();
            adapter.sortItems(selectedSortType);
        });


        AlertDialog dialog = builder.create();
        dialog.show();
    }
}