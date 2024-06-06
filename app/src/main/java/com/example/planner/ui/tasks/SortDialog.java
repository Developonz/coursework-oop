package com.example.planner.ui.tasks;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

public class SortDialog {

    private Context context;
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

        builder.setSingleChoiceItems(sortTypes, selectedSortType, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedSortType = which;
            }
        });

        // Кнопка "ВЫБОР"
        builder.setPositiveButton("ВЫБОР", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(context, "Выбран тип сортировки: " + sortTypes[selectedSortType], Toast.LENGTH_SHORT).show();
                adapter.sortItems(selectedSortType);
            }
        });


        AlertDialog dialog = builder.create();
        dialog.show();
    }
}