package com.tododroid;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class TasksFragment extends Fragment {
    
    private ArrayList<TaskItem> items;
    private TodoAdapter adapter;
    private EditText inputTask;
    private ImageButton btnAddTask;
    private RecyclerView recyclerTasks;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);
        
        items = new ArrayList<>();
        
        inputTask = view.findViewById(R.id.input_task);
        btnAddTask = view.findViewById(R.id.btn_add_task);
        recyclerTasks = view.findViewById(R.id.recycler_tasks);
        
        recyclerTasks.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TodoAdapter(items);
        recyclerTasks.setAdapter(adapter);
        
        // Seed demo tasks with different timestamps for testing headers
        seedDemoTasks();
        
        btnAddTask.setOnClickListener(v -> {
            String task = inputTask.getText().toString().trim();
            if (!task.isEmpty()) {
                adapter.addTask(task);
                inputTask.setText("");
                recyclerTasks.smoothScrollToPosition(adapter.getItemCount() - 1);
            }
        });
        
        return view;
    }
    
    private void seedDemoTasks() {
        long now = System.currentTimeMillis();
        
        // Today's tasks
        items.add(new TaskItem(TaskItem.TYPE_HEADER, "Today"));
        items.add(new TaskItem(TaskItem.TYPE_TASK, "Build TodoDroid app", now));
        items.add(new TaskItem(TaskItem.TYPE_TASK, "Push code to GitHub", now - 3600000));
        
        // Previous 7 days
        items.add(new TaskItem(TaskItem.TYPE_HEADER, "Previous 7 Days"));
        items.add(new TaskItem(TaskItem.TYPE_TASK, "Set up GitHub Actions", now - TimeUnit.DAYS.toMillis(3)));
        items.add(new TaskItem(TaskItem.TYPE_TASK, "Design dark theme UI", now - TimeUnit.DAYS.toMillis(5)));
        
        // Previous 30 days
        items.add(new TaskItem(TaskItem.TYPE_HEADER, "Previous 30 Days"));
        items.add(new TaskItem(TaskItem.TYPE_TASK, "Install Termux packages", now - TimeUnit.DAYS.toMillis(14)));
        items.add(new TaskItem(TaskItem.TYPE_TASK, "Learn Android XML layouts", now - TimeUnit.DAYS.toMillis(21)));
        
        // Older - monthly
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -2);
        String monthYear = new java.text.SimpleDateFormat("MMMM yyyy", java.util.Locale.getDefault())
            .format(cal.getTime());
        items.add(new TaskItem(TaskItem.TYPE_HEADER, monthYear));
        items.add(new TaskItem(TaskItem.TYPE_TASK, "Start learning Java", cal.getTimeInMillis()));
        
        adapter.notifyDataSetChanged();
    }
}
