package com.example.planner;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.planner.controllers.notes.NotesController;
import com.example.planner.controllers.tasks.TasksController;
import com.example.planner.databinding.ActivityMainBinding;
import com.example.planner.ui.notes.NotesViewModel;
import com.example.planner.ui.tasks.TasksViewModel;
import com.example.planner.utils.DatabaseImporterExporter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.navigation.NavigationView;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_IMPORT = 1;
    private static final int REQUEST_CODE_EXPORT = 2;
    private static final int REQUEST_CODE_WRITE_PERMISSION = 3;
    private static final int REQUEST_CODE_READ_PERMISSION = 4;
    private boolean isSaveDataImport = true;
    private ActivityMainBinding binding;
    private TasksController tasksController;
    private NotesController notesController;
    private DatabaseImporterExporter databaseManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        BottomNavigationView navigation = binding.appBarMain.bottomNavigation;
        NavController navController = Navigation.findNavController(this, R.id.fragment_content);
        NavigationUI.setupWithNavController(navigation, navController);

        tasksController = new TasksController(this, new ViewModelProvider(this).get(TasksViewModel.class));
        notesController = new NotesController(this, new ViewModelProvider(this).get(NotesViewModel.class));
        databaseManager = new DatabaseImporterExporter();

        removeTooltips(navigation);

        NavigationView navigationView = findViewById(R.id.side_menu);
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            closeDrawer();
            if (id == R.id.export_json) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_PERMISSION);
                } else {
                    databaseManager.openFilePickerForExport(this, REQUEST_CODE_EXPORT);
                }
                return true;
            } else if (id == R.id.import_json) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_READ_PERMISSION);
                } else {
                    getImportMode(this);
                }
                return true;
            } else if (id == R.id.reset_data) {
                getConfirmationReset();
                return true;
            }
            return false;
        });

        navController.addOnDestinationChangedListener((navController1, navDestination, bundle) -> {
            if (navDestination.getId() == R.id.complete_tasks || navDestination.getId() == R.id.note_menu) {
                findViewById(R.id.bottom_navigation).setVisibility(View.GONE);
                DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            } else {
                findViewById(R.id.bottom_navigation).setVisibility(View.VISIBLE);
                DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                if (requestCode == REQUEST_CODE_IMPORT) {
                    try {
                        databaseManager.importDatabaseFromJson(uri, isSaveDataImport, getApplicationContext());
                        tasksController.loadTasks(false);
                        Toast.makeText(this, "Импорт завершен", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Toast.makeText(this, "Ошибка импорта", Toast.LENGTH_SHORT).show();
                    }
                } else if (requestCode == REQUEST_CODE_EXPORT) {
                    try {
                        databaseManager.exportDatabaseToJson(uri, getApplicationContext());
                        Toast.makeText(this, "Экспорт завершен", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Toast.makeText(this, "Ошибка экспорта", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_WRITE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                databaseManager.openFilePickerForExport(this, REQUEST_CODE_EXPORT);
            }
        } else if (requestCode == REQUEST_CODE_READ_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getImportMode(this);
            }
        }
    }

    private void getConfirmationReset() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Сброс");
        builder.setMessage("Вы точно хотите удалить все ваши данные???");
        builder.setPositiveButton("Да", (dialog, id1) -> {
            tasksController.resetData();
            notesController.resetData();
            tasksController.loadTasks(false);
            notesController.loadNotes();
        });
        builder.create().show();
    }

    private void removeTooltips(BottomNavigationView navigation) {
        for (int i = 0; i < navigation.getMenu().size(); i++) {
            MenuItem item = navigation.getMenu().getItem(i);
            View actionView = findViewById(item.getItemId());
            actionView.setOnLongClickListener(v -> true);
        }
    }

    public void openDrawer(MenuItem item) {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.openDrawer(GravityCompat.START);
    }

    private void closeDrawer() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void getImportMode(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Импорт");
        builder.setMessage("Вы хотите оставить прежние данные?");
        builder.setPositiveButton("Да", (dialog, id) -> {
            isSaveDataImport = true;
            databaseManager.openFilePickerForImport(activity, REQUEST_CODE_IMPORT);
        });
        builder.setNegativeButton("Нет", (dialog, id) -> {
            isSaveDataImport = false;
            databaseManager.openFilePickerForImport(activity, REQUEST_CODE_IMPORT);
        });
        builder.create().show();
    }
}