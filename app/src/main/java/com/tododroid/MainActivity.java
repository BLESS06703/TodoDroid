package com.tododroid;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private ImageButton btnMenu, btnSort, btnCreate;
    private EditText searchInput;
    private TextView tabTasks, tabNotes;
    private ViewPager2 viewPager;
    private ViewPagerAdapter pagerAdapter;
    private boolean mSortLatest = true, mCardViewMode = true;
    
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
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void afterTextChanged(Editable s) { filterContent(s.toString().trim()); }
        });
        navView.setNavigationItemSelectedListener(item -> { drawerLayout.closeDrawer(GravityCompat.START); return true; });
    }
    
    private void filterContent(String q) {
        TasksFragment tf = (TasksFragment) pagerAdapter.getFragment(0);
        NotesFragment nf = (NotesFragment) pagerAdapter.getFragment(1);
        if (tf != null) tf.filter(q);
        if (nf != null) nf.filter(q);
    }
    
    private void showCreateSheet() {
        BottomSheetDialog s = new BottomSheetDialog(this);
        View v = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_create, null);
        s.setContentView(v);
        v.findViewById(R.id.create_note).setOnClickListener(x -> { s.dismiss(); startActivity(new Intent(this, NoteEditorActivity.class)); });
        v.findViewById(R.id.create_task).setOnClickListener(x -> { s.dismiss(); showQuickTaskDialog(); });
        s.show();
    }
    
    private void showQuickTaskDialog() {
        BottomSheetDialog d = new BottomSheetDialog(this);
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_quick_task, null);
        d.setContentView(v);
        EditText input = v.findViewById(R.id.quick_task_input);
        TextView btnAdd = v.findViewById(R.id.btn_add_task);
        TextView dueDateText = v.findViewById(R.id.due_date_text);
        TextView btnClear = v.findViewById(R.id.btn_clear_date);
        final long[] due = {0};
        final SimpleDateFormat fmt = new SimpleDateFormat("MMM d, yyyy 'at' hh:mm a", Locale.getDefault());
        
        dueDateText.setOnClickListener(x -> {
            Calendar cal = Calendar.getInstance();
            if (due[0] > 0) cal.setTimeInMillis(due[0]);
            new DatePickerDialog(this, (dp, y, m, day) -> {
                cal.set(y, m, day);
                new TimePickerDialog(this, (tp, h, min) -> {
                    cal.set(Calendar.HOUR_OF_DAY, h);
                    cal.set(Calendar.MINUTE, min);
                    due[0] = cal.getTimeInMillis();
                    dueDateText.setText(fmt.format(cal.getTime()));
                    dueDateText.setTextColor(0xFF7C3AED);
                    btnClear.setVisibility(View.VISIBLE);
                }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show();
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        });
        
        btnClear.setOnClickListener(x -> {
            due[0] = 0; dueDateText.setText("Set due date (optional)");
            dueDateText.setTextColor(0xFF666666); btnClear.setVisibility(View.GONE);
        });
        
        btnAdd.setOnClickListener(x -> {
            String t = input.getText().toString().trim();
            if (t.isEmpty()) { Toast.makeText(this, "Enter a task", Toast.LENGTH_SHORT).show(); return; }
            TodoItem task = new TodoItem("Task", t, "", System.currentTimeMillis());
            if (due[0] > 0) task.setDueDate(due[0]);
            GlobalData.getInstance().addItem(task);
            refreshCurrentFragment();
            d.dismiss();
            viewPager.setCurrentItem(0);
            selectTab(true);
            Toast.makeText(this, "Task added", Toast.LENGTH_SHORT).show();
        });
        d.show();
    }
    
    private void refreshCurrentFragment() {
        TasksFragment tf = (TasksFragment) pagerAdapter.getFragment(0);
        NotesFragment nf = (NotesFragment) pagerAdapter.getFragment(1);
        if (tf != null) tf.refreshData();
        if (nf != null) nf.refreshData();
    }
    
    private void seedDemoData() {
        GlobalData d = GlobalData.getInstance();
        if (!d.getItems().isEmpty()) return;
        long n = System.currentTimeMillis();
        d.addItem(new TodoItem("Task", "Build TodoDroid app", "", n));
        d.addItem(new TodoItem("Task", "Push code to GitHub", "", n - 3600000));
        TodoItem n1 = new TodoItem("Note", "App Ideas", "Rich text editor", n - 86400000L * 2);
        n1.setThemeColor(0xFF1F2937); d.addItem(n1);
    }
    
    @Override protected void onResume() { super.onResume(); refreshCurrentFragment(); }
    
    private void showSortPopup(View anchor) {
        View pv = LayoutInflater.from(this).inflate(R.layout.popup_sort_menu, null);
        int w = (int)(300 * getResources().getDisplayMetrics().density);
        PopupWindow pop = new PopupWindow(pv, w, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        pop.setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        pop.setElevation(24f);
        int[] l = new int[2]; anchor.getLocationOnScreen(l);
        pop.showAtLocation(anchor, Gravity.NO_GRAVITY, l[0] - w + anchor.getWidth(), l[1] + anchor.getHeight() + 12);
        LinearLayout rl = pv.findViewById(R.id.sort_latest), ro = pv.findViewById(R.id.sort_oldest);
        ((TextView)rl.getChildAt(0)).setOnClickListener(v -> { mSortLatest = true; applySort(); pop.dismiss(); });
        ((TextView)ro.getChildAt(0)).setOnClickListener(v -> { mSortLatest = false; applySort(); pop.dismiss(); });
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
