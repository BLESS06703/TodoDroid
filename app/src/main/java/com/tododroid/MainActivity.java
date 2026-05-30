package com.tododroid;

import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private ImageButton btnMenu, btnSort, btnCreate;
    private EditText searchInput;
    private TextView tabTasks, tabNotes;
    private ViewPager2 viewPager;
    private ViewPagerAdapter pagerAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);
        btnMenu = findViewById(R.id.btn_menu);
        btnSort = findViewById(R.id.btn_sort);
        btnCreate = findViewById(R.id.btn_create);
        searchInput = findViewById(R.id.search_input);
        tabTasks = findViewById(R.id.tab_tasks);
        tabNotes = findViewById(R.id.tab_notes);
        viewPager = findViewById(R.id.view_pager);
        
        setupViewPager();
        
        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        
        tabTasks.setOnClickListener(v -> {
            viewPager.setCurrentItem(0);
            selectTab(true);
        });
        tabNotes.setOnClickListener(v -> {
            viewPager.setCurrentItem(1);
            selectTab(false);
        });
        
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                selectTab(position == 0);
            }
        });
        
        btnSort.setOnClickListener(v -> showSortPopup(v));
        
        // Create button - switch to tasks and focus search
        btnCreate.setOnClickListener(v -> {
            viewPager.setCurrentItem(0);
            selectTab(true);
            searchInput.requestFocus();
        });
        
        // Search input - create task on Enter/Search key
        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                String text = searchInput.getText().toString().trim();
                if (!text.isEmpty()) {
                    TasksFragment tasksFrag = (TasksFragment) pagerAdapter.getFragment(0);
                    if (tasksFrag != null) {
                        tasksFrag.addTask(text);
                        searchInput.setText("");
                        Toast.makeText(this, "Task added", Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }
            return false;
        });
        
        navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_tasks) viewPager.setCurrentItem(0);
            if (id == R.id.nav_completed) Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show();
            if (id == R.id.nav_settings) Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show();
            if (id == R.id.nav_about) Toast.makeText(this, "TodoDroid - Built in Termux", Toast.LENGTH_SHORT).show();
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }
    
    private void showSortPopup(View anchor) {
        View popupView = LayoutInflater.from(this).inflate(R.layout.popup_sort_menu, null);
        
        int width = (int) (300 * getResources().getDisplayMetrics().density);
        
        PopupWindow popup = new PopupWindow(popupView, width,
            LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popup.setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(
            android.graphics.Color.TRANSPARENT));
        popup.setElevation(24f);
        
        int[] location = new int[2];
        anchor.getLocationOnScreen(location);
        popup.showAtLocation(anchor, Gravity.NO_GRAVITY,
            location[0] - width + anchor.getWidth(),
            location[1] + anchor.getHeight() + 12);
        
        LinearLayout sortLatest = popupView.findViewById(R.id.sort_latest);
        LinearLayout sortOldest = popupView.findViewById(R.id.sort_oldest);
        TextView checkLatest = popupView.findViewById(R.id.check_latest);
        TextView checkOldest = popupView.findViewById(R.id.check_oldest);
        TextView viewList = popupView.findViewById(R.id.view_list);
        TextView viewCard = popupView.findViewById(R.id.view_card);
        
        TextView sortLatestText = (TextView) sortLatest.getChildAt(0);
        TextView sortOldestText = (TextView) sortOldest.getChildAt(0);
        
        sortLatest.setOnClickListener(v -> {
            sortLatestText.setTextColor(0xFFFFFFFF);
            checkLatest.setVisibility(View.VISIBLE);
            sortOldestText.setTextColor(0xFFB0B0B0);
            checkOldest.setVisibility(View.INVISIBLE);
            popup.dismiss();
        });
        
        sortOldest.setOnClickListener(v -> {
            sortOldestText.setTextColor(0xFFFFFFFF);
            checkOldest.setVisibility(View.VISIBLE);
            sortLatestText.setTextColor(0xFFB0B0B0);
            checkLatest.setVisibility(View.INVISIBLE);
            popup.dismiss();
        });
        
        viewList.setOnClickListener(v -> {
            viewList.setBackgroundResource(R.drawable.segment_selected);
            viewList.setTextColor(0xFFFFFFFF);
            viewCard.setBackgroundResource(R.drawable.segment_unselected);
            viewCard.setTextColor(0xFF888888);
        });
        
        viewCard.setOnClickListener(v -> {
            viewCard.setBackgroundResource(R.drawable.segment_selected);
            viewCard.setTextColor(0xFFFFFFFF);
            viewList.setBackgroundResource(R.drawable.segment_unselected);
            viewList.setTextColor(0xFF888888);
        });
    }
    
    private void setupViewPager() {
        pagerAdapter = new ViewPagerAdapter(this);
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
