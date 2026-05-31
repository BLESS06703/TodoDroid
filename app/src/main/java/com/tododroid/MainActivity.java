package com.tododroid;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
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
    private boolean mSortLatest = true;
    private boolean mCardViewMode = true;
    
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
        tabTasks.setOnClickListener(v -> { viewPager.setCurrentItem(0); selectTab(true); });
        tabNotes.setOnClickListener(v -> { viewPager.setCurrentItem(1); selectTab(false); });
        
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override public void onPageSelected(int p) { selectTab(p == 0); }
        });
        
        btnSort.setOnClickListener(v -> showSortPopup(v));
        btnCreate.setOnClickListener(v -> showCreateSheet());
        searchInput.setHint("Search tasks and notes...");
        
        // Real-time search filter
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                filterContent(s.toString().trim());
            }
        });
        
        navView.setNavigationItemSelectedListener(item -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }
    
    private void filterContent(String query) {
        TasksFragment tf = (TasksFragment) pagerAdapter.getFragment(0);
        NotesFragment nf = (NotesFragment) pagerAdapter.getFragment(1);
        
        if (tf != null) tf.filter(query);
        if (nf != null) nf.filter(query);
    }
    
    private void showCreateSheet() {
        BottomSheetDialog sheet = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_create, null);
        sheet.setContentView(view);
        view.findViewById(R.id.create_note).setOnClickListener(v -> {
            sheet.dismiss(); startActivity(new Intent(this, NoteEditorActivity.class));
        });
        view.findViewById(R.id.create_task).setOnClickListener(v -> {
            sheet.dismiss(); showQuickTaskDialog();
        });
        sheet.show();
    }
    
    private void showQuickTaskDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_quick_task, null);
        dialog.setContentView(view);
        
        EditText input = view.findViewById(R.id.quick_task_input);
        TextView btnAdd = view.findViewById(R.id.btn_add_task);
        
        btnAdd.setOnClickListener(v -> {
            String text = input.getText().toString().trim();
            if (text.isEmpty()) { Toast.makeText(this, "Enter a task", Toast.LENGTH_SHORT).show(); return; }
            GlobalData.getInstance().addItem(new TodoItem("Task", text, "", System.currentTimeMillis()));
            refreshCurrentFragment();
            dialog.dismiss();
            viewPager.setCurrentItem(0);
            selectTab(true);
            Toast.makeText(this, "Task added", Toast.LENGTH_SHORT).show();
        });
        dialog.show();
    }
    
    private void refreshCurrentFragment() {
        TasksFragment tf = (TasksFragment) pagerAdapter.getFragment(0);
        NotesFragment nf = (NotesFragment) pagerAdapter.getFragment(1);
        if (tf != null) tf.refreshData();
        if (nf != null) nf.refreshData();
    }
    
    private void seedDemoData() {
        GlobalData data = GlobalData.getInstance();
        if (!data.getItems().isEmpty()) return;
        long now = System.currentTimeMillis();
        data.addItem(new TodoItem("Task", "Build TodoDroid app", "", now));
        data.addItem(new TodoItem("Task", "Push code to GitHub", "", now - 3600000));
        data.addItem(new TodoItem("Task", "Design dark theme UI", "", now - 86400000L * 3));
        TodoItem n1 = new TodoItem("Note", "App Ideas", "Rich text editor", now - 86400000L * 2);
        n1.setThemeColor(0xFF1F2937); data.addItem(n1);
        TodoItem n2 = new TodoItem("Note", "Meeting Notes", "Module architecture", now - 86400000L * 5);
        n2.setThemeColor(0xFFEDE9FE); data.addItem(n2);
    }
    
    @Override protected void onResume() { super.onResume(); refreshCurrentFragment(); }
    
    private void showSortPopup(View anchor) {
        View pv = LayoutInflater.from(this).inflate(R.layout.popup_sort_menu, null);
        int w = (int) (300 * getResources().getDisplayMetrics().density);
        PopupWindow popup = new PopupWindow(pv, w, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popup.setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        popup.setElevation(24f);
        int[] l = new int[2]; anchor.getLocationOnScreen(l);
        popup.showAtLocation(anchor, Gravity.NO_GRAVITY, l[0] - w + anchor.getWidth(), l[1] + anchor.getHeight() + 12);
        
        LinearLayout rowLatest = pv.findViewById(R.id.sort_latest);
        LinearLayout rowOldest = pv.findViewById(R.id.sort_oldest);
        TextView txtLatest = (TextView) rowLatest.getChildAt(0);
        TextView txtOldest = (TextView) rowOldest.getChildAt(0);
        
        rowLatest.setOnClickListener(v -> { mSortLatest = true; applySort(); popup.dismiss(); });
        rowOldest.setOnClickListener(v -> { mSortLatest = false; applySort(); popup.dismiss(); });
    }
    
    private void applySort() { TasksFragment tf = (TasksFragment) pagerAdapter.getFragment(0); if (tf != null) tf.setSort(mSortLatest); }
    private void setupViewPager() { pagerAdapter = new ViewPagerAdapter(this); viewPager.setAdapter(pagerAdapter); viewPager.setCurrentItem(0); }
    
    private void selectTab(boolean t) {
        tabTasks.setBackgroundResource(t ? R.drawable.pill_selected : R.drawable.pill_unselected);
        tabTasks.setTextColor(t ? 0xFFFFFFFF : 0xFF888888);
        tabNotes.setBackgroundResource(t ? R.drawable.pill_unselected : R.drawable.pill_selected);
        tabNotes.setTextColor(t ? 0xFF888888 : 0xFFFFFFFF);
    }
    
    @Override public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) drawerLayout.closeDrawer(GravityCompat.START);
        else super.onBackPressed();
    }
}
