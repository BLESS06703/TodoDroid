package com.tododroid;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class TasksFragment extends Fragment {
    
    private TodoAdapter adapter;
    private RecyclerView recyclerTasks;
    private LinearLayout toggleCompleted;
    private TextView toggleText;
    private View toggleDot;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);
        
        recyclerTasks = view.findViewById(R.id.recycler_tasks);
        toggleCompleted = view.findViewById(R.id.toggle_completed);
        toggleText = view.findViewById(R.id.toggle_text);
        toggleDot = view.findViewById(R.id.toggle_dot);
        
        recyclerTasks.setLayoutManager(new LinearLayoutManager(getContext()));
        
        ArrayList<TaskItem> taskItems = convertToTaskItems(GlobalData.getInstance().getTasks());
        adapter = new TodoAdapter(taskItems);
        recyclerTasks.setAdapter(adapter);
        
        toggleCompleted.setOnClickListener(v -> {
            boolean newState = !adapter.isHideCompleted();
            adapter.setHideCompleted(newState);
            if (newState) {
                toggleText.setText("Show done");
                toggleDot.setBackgroundResource(R.drawable.toggle_dot_on);
            } else {
                toggleText.setText("Hide done");
                toggleDot.setBackgroundResource(R.drawable.toggle_dot_off);
            }
        });
        
        return view;
    }
    
    public void refreshData() {
        if (adapter != null) {
            // Rebuild adapter with fresh data
            adapter = new TodoAdapter(convertToTaskItems(GlobalData.getInstance().getTasks()));
            recyclerTasks.setAdapter(adapter);
        }
    }
    
    private ArrayList<TaskItem> convertToTaskItems(ArrayList<TodoItem> todoItems) {
        ArrayList<TaskItem> items = new ArrayList<>();
        for (TodoItem item : todoItems) {
            items.add(new TaskItem(TaskItem.TYPE_TASK, item.getTitle(), item.getTimestamp()));
        }
        return items;
    }
    
    public void addTask(String text) {
        GlobalData.getInstance().addItem(
            new TodoItem("Task", text, "", System.currentTimeMillis()));
        refreshData();
        recyclerTasks.smoothScrollToPosition(0);
    }
}
