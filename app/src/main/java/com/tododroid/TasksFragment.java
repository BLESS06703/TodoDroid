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
        
        loadTasks();
        
        toggleCompleted.setOnClickListener(v -> {
            if (adapter != null) {
                boolean newState = !adapter.isHideCompleted();
                adapter.setHideCompleted(newState);
                toggleText.setText(newState ? "Show done" : "Hide done");
                toggleDot.setBackgroundResource(newState ? R.drawable.toggle_dot_on : R.drawable.toggle_dot_off);
            }
        });
        
        return view;
    }
    
    private void loadTasks() {
        ArrayList<TaskItem> taskItems = new ArrayList<>();
        
        for (TodoItem item : GlobalData.getInstance().getTasks()) {
            taskItems.add(new TaskItem(TaskItem.TYPE_TASK, item.getTitle(), item.getTimestamp()));
        }
        
        adapter = new TodoAdapter(taskItems);
        recyclerTasks.setAdapter(adapter);
    }
    
    public void refreshData() {
        if (recyclerTasks != null) {
            loadTasks();
        }
    }
    
    public void addTask(String text) {
        GlobalData.getInstance().addItem(
            new TodoItem("Task", text, "", System.currentTimeMillis()));
        refreshData();
    }
}
