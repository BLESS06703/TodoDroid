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
    private ArrayList<TaskItem> taskItems;
    private String currentFilter = "";
    
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
        taskItems = new ArrayList<>();
        loadTasksFromGlobal();
        adapter = new TodoAdapter(taskItems);
        recyclerTasks.setAdapter(adapter);
        toggleText.setText("Hide done");
        toggleDot.setBackgroundResource(R.drawable.toggle_dot_off);
        toggleCompleted.setOnClickListener(v -> {
            boolean ns = !adapter.isHideCompleted();
            adapter.setHideCompleted(ns);
            toggleText.setText(ns ? "Show done" : "Hide done");
            toggleDot.setBackgroundResource(ns ? R.drawable.toggle_dot_on : R.drawable.toggle_dot_off);
        });
        return view;
    }
    
    private void loadTasksFromGlobal() {
        taskItems.clear();
        for (TodoItem item : GlobalData.getInstance().getTasks()) {
            if (currentFilter.isEmpty() || item.getTitle().toLowerCase().contains(currentFilter.toLowerCase())) {
                taskItems.add(new TaskItem(TaskItem.TYPE_TASK, item.getTitle(), item.getTimestamp()));
            }
        }
    }
    
    public void filter(String q) { currentFilter = q; loadTasksFromGlobal(); if (adapter != null) adapter.refreshFromSource(); }
    public void refreshData() { loadTasksFromGlobal(); if (adapter != null) adapter.refreshFromSource(); }
    public void setSort(boolean l) { if (adapter != null) adapter.setSortByLatest(l); }
    public void setCardView(boolean c) { if (adapter != null) adapter.setCardView(c); }
}
