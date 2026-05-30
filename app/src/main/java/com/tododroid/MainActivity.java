package com.tododroid;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    
    private ArrayList<String> todoList;
    private TodoAdapter adapter;
    private EditText inputTask;
    private ImageButton btnAdd;
    private RecyclerView recyclerView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        todoList = new ArrayList<>();
        
        inputTask = findViewById(R.id.input_task);
        btnAdd = findViewById(R.id.btn_add);
        recyclerView = findViewById(R.id.recycler_view);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TodoAdapter(todoList);
        recyclerView.setAdapter(adapter);
        
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String task = inputTask.getText().toString().trim();
                if (!task.isEmpty()) {
                    todoList.add(task);
                    adapter.notifyItemInserted(todoList.size() - 1);
                    inputTask.setText("");
                }
            }
        });
    }
}
