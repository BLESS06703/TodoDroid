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

public class TasksFragment extends Fragment {
    
    private ArrayList<String> todoList;
    private TodoAdapter adapter;
    private EditText inputTask;
    private ImageButton btnAddTask;
    private RecyclerView recyclerTasks;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);
        
        todoList = new ArrayList<>();
        
        inputTask = view.findViewById(R.id.input_task);
        btnAddTask = view.findViewById(R.id.btn_add_task);
        recyclerTasks = view.findViewById(R.id.recycler_tasks);
        
        recyclerTasks.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TodoAdapter(todoList);
        recyclerTasks.setAdapter(adapter);
        
        btnAddTask.setOnClickListener(v -> {
            String task = inputTask.getText().toString().trim();
            if (!task.isEmpty()) {
                todoList.add(task);
                adapter.notifyItemInserted(todoList.size() - 1);
                inputTask.setText("");
            }
        });
        
        return view;
    }
}
