package com.tododroid;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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
        public CheckBox checkbox;
        public TextView btnDelete;
        
        public ViewHolder(View itemView) {
            super(itemView);
            taskText = itemView.findViewById(R.id.task_text);
            checkbox = itemView.findViewById(R.id.checkbox);
            btnDelete = itemView.findViewById(R.id.btn_delete);
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
        String task = todoList.get(position);
        holder.taskText.setText(task);
        
        holder.checkbox.setOnCheckedChangeListener(null);
        holder.checkbox.setChecked(false);
        holder.checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                holder.taskText.setPaintFlags(
                    holder.taskText.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
                );
                holder.taskText.setTextColor(0xFF666666);
            } else {
                holder.taskText.setPaintFlags(
                    holder.taskText.getPaintFlags() & ~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
                );
                holder.taskText.setTextColor(0xFFE0E0E0);
            }
        });
        
        holder.btnDelete.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                todoList.remove(pos);
                notifyItemRemoved(pos);
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return todoList.size();
    }
}
