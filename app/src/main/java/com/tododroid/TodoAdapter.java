package com.tododroid;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TodoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    
    private ArrayList<TaskItem> items;
    
    public TodoAdapter(ArrayList<TaskItem> items) {
        this.items = items;
    }
    
    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType();
    }
    
    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public TextView headerText;
        public HeaderViewHolder(View itemView) {
            super(itemView);
            headerText = itemView.findViewById(R.id.header_text);
        }
    }
    
    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        public TextView taskText;
        public CheckBox checkbox;
        public ImageButton btnDelete;
        public TaskViewHolder(View itemView) {
            super(itemView);
            taskText = itemView.findViewById(R.id.task_text);
            checkbox = itemView.findViewById(R.id.checkbox);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
    
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TaskItem.TYPE_HEADER) {
            View view = inflater.inflate(R.layout.item_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_todo, parent, false);
            return new TaskViewHolder(view);
        }
    }
    
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TaskItem item = items.get(position);
        
        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder h = (HeaderViewHolder) holder;
            h.headerText.setText(item.getTitle());
        } 
        else if (holder instanceof TaskViewHolder) {
            TaskViewHolder t = (TaskViewHolder) holder;
            t.taskText.setText(item.getTaskText());
            
            t.checkbox.setOnCheckedChangeListener(null);
            t.checkbox.setChecked(item.isCompleted());
            t.checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                item.setCompleted(isChecked);
                if (isChecked) {
                    t.taskText.setPaintFlags(
                        t.taskText.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                    t.taskText.setTextColor(0xFF666666);
                } else {
                    t.taskText.setPaintFlags(
                        t.taskText.getPaintFlags() & ~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                    t.taskText.setTextColor(0xFFE0E0E0);
                }
            });
            
            if (item.isCompleted()) {
                t.taskText.setPaintFlags(
                    t.taskText.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                t.taskText.setTextColor(0xFF666666);
            }
            
            t.btnDelete.setOnClickListener(v -> {
                int pos = t.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    new AlertDialog.Builder(v.getContext())
                        .setTitle("Delete Task")
                        .setMessage("Are you sure you want to delete \"" + 
                                    items.get(pos).getTaskText() + "\"?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            items.remove(pos);
                            notifyItemRemoved(pos);
                            updateHeaders();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                }
            });
        }
    }
    
    @Override
    public int getItemCount() {
        return items.size();
    }
    
    public void updateHeaders() {
        for (int i = items.size() - 1; i >= 0; i--) {
            if (items.get(i).getType() == TaskItem.TYPE_HEADER) {
                boolean hasTasks = false;
                for (int j = i + 1; j < items.size(); j++) {
                    if (items.get(j).getType() == TaskItem.TYPE_HEADER) break;
                    hasTasks = true;
                }
                if (!hasTasks) {
                    items.remove(i);
                }
            }
        }
        notifyDataSetChanged();
    }
    
    public void addTask(String text) {
        long now = System.currentTimeMillis();
        String category = getCategory(now);
        
        int headerIndex = findHeaderIndex(category);
        if (headerIndex == -1) {
            items.add(new TaskItem(TaskItem.TYPE_HEADER, category));
            headerIndex = items.size() - 1;
        }
        
        TaskItem task = new TaskItem(TaskItem.TYPE_TASK, text, now);
        int insertAt = headerIndex + 1;
        while (insertAt < items.size() && items.get(insertAt).getType() == TaskItem.TYPE_TASK) {
            insertAt++;
        }
        items.add(insertAt, task);
        notifyItemInserted(insertAt);
    }
    
    private int findHeaderIndex(String category) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getType() == TaskItem.TYPE_HEADER && 
                items.get(i).getTitle().equals(category)) {
                return i;
            }
        }
        return -1;
    }
    
    private String getCategory(long timestamp) {
        long now = System.currentTimeMillis();
        long diff = now - timestamp;
        long days = TimeUnit.MILLISECONDS.toDays(diff);
        
        if (days == 0) return "Today";
        if (days <= 7) return "Previous 7 Days";
        if (days <= 30) return "Previous 30 Days";
        
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
}
