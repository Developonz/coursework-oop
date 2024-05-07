package com.example.planner;

import static java.security.AccessController.getContext;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.planner.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    private Button activeCategory;

    private ArrayList<Button> categoriesBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LayoutInflater inflater = LayoutInflater.from(this);

        View customToolbar = inflater.inflate(R.layout.custom_toolbar_layout, null);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setTitle("");
        }

        toolbar.addView(customToolbar);

        categoriesBtn = new ArrayList<>();
        categoriesBtn.add(createNavBtn("Все", customToolbar));
        categoriesBtn.add(createNavBtn("Личное", customToolbar));
        categoriesBtn.add(createNavBtn("Учёба", customToolbar));
        categoriesBtn.add(createNavBtn("Работа", customToolbar));
        categoriesBtn.add(createNavBtn("Желания", customToolbar));
        activeCategory = categoriesBtn.get(0);
        ViewCompat.setBackgroundTintList(activeCategory,
                ContextCompat.getColorStateList(customToolbar.getContext(), R.color.teal_700));


        Drawable drawable = toolbar.getOverflowIcon();
        if (drawable != null) {
            drawable.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        }

        BottomNavigationView navigation = binding.appBarMain.bottomNavigation;

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupWithNavController(navigation, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*if(item.getItemId() == R.id.action_search) {
            Intent intent = new Intent(this, SearchUsersActivity.class);
            startActivity(intent);
        }*/
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void showToolbar(boolean show) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            if (show) {
                actionBar.show();
            } else {
                actionBar.hide();
            }
        }
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

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activeCategory != null) {
                    ViewCompat.setBackgroundTintList(activeCategory,
                            ContextCompat.getColorStateList(v.getContext(), R.color.teal_200));
                }
                activeCategory = button;
                ViewCompat.setBackgroundTintList(activeCategory,
                        ContextCompat.getColorStateList(v.getContext(), R.color.teal_700));
            }
        });

        LinearLayout layout = (LinearLayout) findViewById(R.id.toolbarBtns);
        layout.addView(button);
        return button;
    }

    public void openDrawer(MenuItem item) {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.openDrawer(GravityCompat.START);
    }
}