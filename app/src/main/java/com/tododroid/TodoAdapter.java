package com.tododroid;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder> {
    
    private ArrayList<String> todoList;
    
    public TodoAdapter(ArrayList<String> list) {
        this.todoList = list;
    }
    
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView taskText;
        public ViewHolder(View itemView) {
            super(itemView);
            taskText = itemView.findViewById(R.id.task_text);
        }
    }
    
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_todo, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.taskText.setText(todoList.get(position));
    }
    
    @Override
    public int getItemCount() {
        return todoList.size();
    }
}
