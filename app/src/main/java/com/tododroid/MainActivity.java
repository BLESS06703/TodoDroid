package com.tododroid;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private ImageButton btnMenu, btnSort;
    private TextView tabTasks, tabNotes;
    private ViewPager2 viewPager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);
        btnMenu = findViewById(R.id.btn_menu);
        btnSort = findViewById(R.id.btn_sort);
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
        
        // Sort button - show popup
        btnSort.setOnClickListener(v -> showSortPopup(v));
        
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
    
    private void showSortPopup(View anchor) {
        View popupView = LayoutInflater.from(this).inflate(R.layout.popup_sort_menu, null);
        PopupWindow popup = new PopupWindow(
            popupView,
            180,
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        );
        popup.setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        popup.setElevation(12);
        popup.setAnimationStyle(android.R.style.Animation_Dialog);
        
        // Show popup anchored to the sort button
        popup.showAsDropDown(anchor, -120, 8, Gravity.TOP | Gravity.END);
        
        // Sort options
        TextView sortLatest = popupView.findViewById(R.id.sort_latest);
        TextView sortOldest = popupView.findViewById(R.id.sort_oldest);
        TextView viewList = popupView.findViewById(R.id.view_list);
        TextView viewCard = popupView.findViewById(R.id.view_card);
        
        sortLatest.setOnClickListener(v -> {
            Toast.makeText(this, "Sorted by latest", Toast.LENGTH_SHORT).show();
            popup.dismiss();
        });
        
        sortOldest.setOnClickListener(v -> {
            Toast.makeText(this, "Sorted by oldest", Toast.LENGTH_SHORT).show();
            popup.dismiss();
        });
        
        viewList.setOnClickListener(v -> {
            viewList.setBackgroundResource(R.drawable.pill_selected_small);
            viewList.setTextColor(0xFFFFFFFF);
            viewList.setTypeface(null, android.graphics.Typeface.BOLD);
            viewCard.setBackgroundResource(R.drawable.pill_unselected_small);
            viewCard.setTextColor(0xFF888888);
            viewCard.setTypeface(null, android.graphics.Typeface.NORMAL);
            Toast.makeText(this, "List view", Toast.LENGTH_SHORT).show();
        });
        
        viewCard.setOnClickListener(v -> {
            viewCard.setBackgroundResource(R.drawable.pill_selected_small);
            viewCard.setTextColor(0xFFFFFFFF);
            viewCard.setTypeface(null, android.graphics.Typeface.BOLD);
            viewList.setBackgroundResource(R.drawable.pill_unselected_small);
            viewList.setTextColor(0xFF888888);
            viewList.setTypeface(null, android.graphics.Typeface.NORMAL);
            Toast.makeText(this, "Card view", Toast.LENGTH_SHORT).show();
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
