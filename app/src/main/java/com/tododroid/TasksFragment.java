package com.tododroid;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private RecyclerView recyclerTasks;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);
        
        items = new ArrayList<>();
        recyclerTasks = view.findViewById(R.id.recycler_tasks);
        
        recyclerTasks.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TodoAdapter(items);
        recyclerTasks.setAdapter(adapter);
        
        seedDemoTasks();
        
        return view;
    }
    
    public void addTask(String text) {
        adapter.addTask(text);
        recyclerTasks.smoothScrollToPosition(adapter.getItemCount() - 1);
    }
    
    private void seedDemoTasks() {
        long now = System.currentTimeMillis();
        
        items.add(new TaskItem(TaskItem.TYPE_HEADER, "Today"));
        items.add(new TaskItem(TaskItem.TYPE_TASK, "Build TodoDroid app", now));
        items.add(new TaskItem(TaskItem.TYPE_TASK, "Push code to GitHub", now - 3600000));
        
        items.add(new TaskItem(TaskItem.TYPE_HEADER, "Previous 7 Days"));
        items.add(new TaskItem(TaskItem.TYPE_TASK, "Set up GitHub Actions", now - TimeUnit.DAYS.toMillis(3)));
        items.add(new TaskItem(TaskItem.TYPE_TASK, "Design dark theme UI", now - TimeUnit.DAYS.toMillis(5)));
        
        items.add(new TaskItem(TaskItem.TYPE_HEADER, "Previous 30 Days"));
        items.add(new TaskItem(TaskItem.TYPE_TASK, "Install Termux packages", now - TimeUnit.DAYS.toMillis(14)));
        items.add(new TaskItem(TaskItem.TYPE_TASK, "Learn Android XML layouts", now - TimeUnit.DAYS.toMillis(21)));
        
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -2);
        String monthYear = new java.text.SimpleDateFormat("MMMM yyyy", java.util.Locale.getDefault())
            .format(cal.getTime());
        items.add(new TaskItem(TaskItem.TYPE_HEADER, monthYear));
        items.add(new TaskItem(TaskItem.TYPE_TASK, "Start learning Java", cal.getTimeInMillis()));
        
        adapter.notifyDataSetChanged();
    }
}
