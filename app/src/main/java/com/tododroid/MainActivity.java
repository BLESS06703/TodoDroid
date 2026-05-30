package com.tododroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private ImageButton btnMenu, btnSort, btnCreate;
    private EditText searchInput;
    private TextView tabTasks, tabNotes;
    private ViewPager2 viewPager;
    private ViewPagerAdapter pagerAdapter;
    private String currentCreateType = "Task";
    
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
        seedDemoData();
        
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
        btnCreate.setOnClickListener(v -> showCreateDialog());
        
        searchInput.setHint("Search...");
        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                String query = searchInput.getText().toString().trim();
                if (!query.isEmpty()) {
                    GlobalData.getInstance().addItem(
                        new TodoItem("Task", query, "", System.currentTimeMillis()));
                    refreshCurrentFragment();
                    searchInput.setText("");
                    Toast.makeText(this, "Task added", Toast.LENGTH_SHORT).show();
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
    
    private void showCreateDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_create, null);
        dialog.setContentView(view);
        
        TextView typeTask = view.findViewById(R.id.type_task);
        TextView typeNote = view.findViewById(R.id.type_note);
        EditText inputTitle = view.findViewById(R.id.input_title);
        EditText inputContent = view.findViewById(R.id.input_content);
        TextView btnCreateItem = view.findViewById(R.id.btn_create_item);
        
        currentCreateType = "Task";
        
        typeTask.setOnClickListener(v -> {
            currentCreateType = "Task";
            typeTask.setBackgroundResource(R.drawable.segment_selected);
            typeTask.setTextColor(0xFFFFFFFF);
            typeTask.setTypeface(null, android.graphics.Typeface.BOLD);
            typeNote.setBackgroundResource(R.drawable.segment_unselected);
            typeNote.setTextColor(0xFF888888);
            typeNote.setTypeface(null, android.graphics.Typeface.NORMAL);
        });
        
        typeNote.setOnClickListener(v -> {
            currentCreateType = "Note";
            typeNote.setBackgroundResource(R.drawable.segment_selected);
            typeNote.setTextColor(0xFFFFFFFF);
            typeNote.setTypeface(null, android.graphics.Typeface.BOLD);
            typeTask.setBackgroundResource(R.drawable.segment_unselected);
            typeTask.setTextColor(0xFF888888);
            typeTask.setTypeface(null, android.graphics.Typeface.NORMAL);
        });
        
        btnCreateItem.setOnClickListener(v -> {
            String title = inputTitle.getText().toString().trim();
            if (title.isEmpty()) {
                Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show();
                return;
            }
            
            dialog.dismiss();
            
            if (currentCreateType.equals("Note")) {
                // Open full note editor
                Intent intent = new Intent(MainActivity.this, NoteEditorActivity.class);
                intent.putExtra("note_title", title);
                intent.putExtra("note_content", inputContent.getText().toString().trim());
                startActivity(intent);
            } else {
                // Quick task creation
                String content = inputContent.getText().toString().trim();
                GlobalData.getInstance().addItem(
                    new TodoItem("Task", title, content, System.currentTimeMillis()));
                viewPager.setCurrentItem(0);
                selectTab(true);
                refreshCurrentFragment();
                Toast.makeText(this, "Task created!", Toast.LENGTH_SHORT).show();
            }
        });
        
        dialog.show();
    }
    
    private void refreshCurrentFragment() {
        if (pagerAdapter != null) {
            TasksFragment taskFrag = (TasksFragment) pagerAdapter.getFragment(0);
            NotesFragment noteFrag = (NotesFragment) pagerAdapter.getFragment(1);
            if (taskFrag != null) taskFrag.refreshData();
            if (noteFrag != null) noteFrag.refreshData();
        }
    }
    
    private void seedDemoData() {
        GlobalData data = GlobalData.getInstance();
        if (!data.getItems().isEmpty()) return;
        
        long now = System.currentTimeMillis();
        data.addItem(new TodoItem("Task", "Build TodoDroid app", "Complete Android app with Termux", now));
        data.addItem(new TodoItem("Task", "Push code to GitHub", "", now - 3600000));
        data.addItem(new TodoItem("Task", "Design dark theme UI", "Purple accent on dark background", now - 86400000L * 3));
        data.addItem(new TodoItem("Note", "App Ideas", "Add rich text editor, voice notes, and cloud sync", now - 86400000L * 2));
        data.addItem(new TodoItem("Note", "Meeting Notes", "Discussed module architecture and bottom nav design", now - 86400000L * 5));
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        refreshCurrentFragment();
    }
    
    private void showSortPopup(View anchor) {
        View popupView = LayoutInflater.from(this).inflate(R.layout.popup_sort_menu, null);
        int width = (int) (300 * getResources().getDisplayMetrics().density);
        
        PopupWindow popup = new PopupWindow(popupView, width, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popup.setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        popup.setElevation(24f);
        
        int[] location = new int[2];
        anchor.getLocationOnScreen(location);
        popup.showAtLocation(anchor, Gravity.NO_GRAVITY,
            location[0] - width + anchor.getWidth(), location[1] + anchor.getHeight() + 12);
        
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
