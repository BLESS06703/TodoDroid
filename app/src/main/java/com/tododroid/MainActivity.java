package com.tododroid;

import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.navigation.NavigationView;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    
    private ArrayList<String> todoList;
    private TodoAdapter adapter;
    private EditText inputTask;
    private ImageButton btnAdd, btnMenu;
    private RecyclerView recyclerView;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        todoList = new ArrayList<>();
        
        inputTask = findViewById(R.id.input_task);
        btnAdd = findViewById(R.id.btn_add);
        btnMenu = findViewById(R.id.btn_menu);
        recyclerView = findViewById(R.id.recycler_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TodoAdapter(todoList);
        recyclerView.setAdapter(adapter);
        
        // Add task
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String task = inputTask.getText().toString().trim();
                if (!task.isEmpty()) {
                    todoList.add(task);
                    adapter.notifyItemInserted(todoList.size() - 1);
                    inputTask.setText("");
                }
            }
        });
        
        // Hamburger menu - open drawer
        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        
        // Navigation item clicks
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                
                if (id == R.id.nav_tasks) {
                    Toast.makeText(MainActivity.this, "My Tasks", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_completed) {
                    Toast.makeText(MainActivity.this, "Completed tasks coming soon", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_settings) {
                    Toast.makeText(MainActivity.this, "Settings coming soon", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_about) {
                    Toast.makeText(MainActivity.this, "TodoDroid - Built in Termux", Toast.LENGTH_SHORT).show();
                }
                
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
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
