package com.example.planner;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.planner.controllers.TasksController;
import com.example.planner.databinding.ActivityMainBinding;
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
    private ActivityMainBinding binding;
    private TasksController controller;
    private boolean isSaveDataImport = true;

    private static final int REQUEST_CODE_IMPORT = 1;
    private static final int REQUEST_CODE_EXPORT = 2;
    private static final int REQUEST_CODE_WRITE_PERMISSION = 3;
    private static final int REQUEST_CODE_READ_PERMISSION = 4;

    DatabaseImporterExporter taskDatabaseManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        BottomNavigationView navigation = binding.appBarMain.bottomNavigation;
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupWithNavController(navigation, navController);

        for (int i = 0; i < navigation.getMenu().size(); i++) {
            MenuItem item = navigation.getMenu().getItem(i);
            View actionView = findViewById(item.getItemId());
            actionView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return true;
                }
            });
        }

        controller = new TasksController(this, new ViewModelProvider(this).get(TasksViewModel.class));
        taskDatabaseManager = new DatabaseImporterExporter(controller);


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.export_json) {
                closeDrawer();
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_PERMISSION);
                } else {
                    taskDatabaseManager.openFilePickerForExport(this, REQUEST_CODE_EXPORT);
                }

                return true;
            } else if (id == R.id.import_json) {
                closeDrawer();
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_READ_PERMISSION);
                } else {
                    getImportMode(this);
                }
                return true;
            } else if (id == R.id.reset_data) {
                closeDrawer();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Сброс");
                builder.setMessage("Вы точно хотите удалить все ваши данные???");
                builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        controller.resetData();
                        controller.loadTasks(false);
                    }
                });
                builder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {}
                });
                builder.create().show();
                return true;
            }
            return false;
        });
    }

    public void openDrawer(MenuItem item) {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public void closeDrawer() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void getImportMode(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Импорт");
        builder.setMessage("Вы хотите оставить прежние данные?");
        builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                isSaveDataImport = true;
                taskDatabaseManager.openFilePickerForImport(activity, REQUEST_CODE_IMPORT);
            }
        });
        builder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                isSaveDataImport = false;
                taskDatabaseManager.openFilePickerForImport(activity, REQUEST_CODE_IMPORT);
            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();

            if (uri != null) {
                if (requestCode == REQUEST_CODE_IMPORT) {
                    try {
                        taskDatabaseManager.importDatabaseFromJson(uri, isSaveDataImport);
                        controller.loadTasks(false);
                        Toast.makeText(this, "Импорт завершен", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Ошибка импорта", Toast.LENGTH_SHORT).show();
                    }
                } else if (requestCode == REQUEST_CODE_EXPORT) {
                    try {
                        taskDatabaseManager.exportDatabaseToJson(uri);
                        Toast.makeText(this, "Экспорт завершен", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
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
                taskDatabaseManager.openFilePickerForExport(this, REQUEST_CODE_EXPORT);
            }
        } else if (requestCode == REQUEST_CODE_READ_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getImportMode(this);
            }
        }
    }
}