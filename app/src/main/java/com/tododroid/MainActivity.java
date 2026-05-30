package com.tododroid;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.navigation.NavigationView;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private ImageButton btnMenu, btnSort;
    private TextView tabTasks, tabNotes;
    private ViewPager2 viewPager;
    
    // Tasks
    private ArrayList<String> todoList;
    private TodoAdapter taskAdapter;
    private EditText inputTask;
    private ImageButton btnAddTask;
    private RecyclerView recyclerTasks;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Views
        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);
        btnMenu = findViewById(R.id.btn_menu);
        btnSort = findViewById(R.id.btn_sort);
        tabTasks = findViewById(R.id.tab_tasks);
        tabNotes = findViewById(R.id.tab_notes);
        viewPager = findViewById(R.id.view_pager);
        
        // Setup ViewPager2
        setupViewPager();
        
        // Hamburger menu
        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        
        // Pill tab switching
        tabTasks.setOnClickListener(v -> {
            viewPager.setCurrentItem(0);
            selectTab(true);
        });
        tabNotes.setOnClickListener(v -> {
            viewPager.setCurrentItem(1);
            selectTab(false);
        });
        
        // Sync tabs with swipe
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                selectTab(position == 0);
            }
        });
        
        // Sort button
        btnSort.setOnClickListener(v -> 
            Toast.makeText(MainActivity.this, "Sort options coming soon", Toast.LENGTH_SHORT).show()
        );
        
        // Navigation drawer
        navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_tasks) {
                viewPager.setCurrentItem(0);
            } else if (id == R.id.nav_completed) {
                Toast.makeText(MainActivity.this, "Completed coming soon", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_settings) {
                Toast.makeText(MainActivity.this, "Settings coming soon", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_about) {
                Toast.makeText(MainActivity.this, "TodoDroid - Built in Termux", Toast.LENGTH_SHORT).show();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }
    
    private void setupViewPager() {
        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(0);
    }
    
    private void selectTab(boolean isTasks) {
        if (isTasks) {
            tabTasks.setBackgroundResource(R.drawable.pill_selected);
            tabTasks.setTextColor(0xFFFFFFFF);
            tabNotes.setBackgroundResource(R.drawable.pill_unselected);
            tabNotes.setTextColor(0xFF888888);
        } else {
            tabNotes.setBackgroundResource(R.drawable.pill_selected);
            tabNotes.setTextColor(0xFFFFFFFF);
            tabTasks.setBackgroundResource(R.drawable.pill_unselected);
            tabTasks.setTextColor(0xFF888888);
        }
    }
    
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
