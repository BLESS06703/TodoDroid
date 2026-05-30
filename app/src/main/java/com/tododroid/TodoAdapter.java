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
    
    private ArrayList<TaskItem> allItems;
    private ArrayList<TaskItem> visibleItems;
    private boolean hideCompleted = false;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, hh:mm a", Locale.getDefault());
    
    public TodoAdapter(ArrayList<TaskItem> items) {
        this.allItems = items;
        rebuildVisibleList();
    }
    
    public void setHideCompleted(boolean hide) {
        this.hideCompleted = hide;
        rebuildVisibleList();
        notifyDataSetChanged();
    }
    
    public boolean isHideCompleted() {
        return hideCompleted;
    }
    
    private void rebuildVisibleList() {
        visibleItems = new ArrayList<>();
        String currentHeader = null;
        
        for (TaskItem item : allItems) {
            if (item.getType() == TaskItem.TYPE_HEADER) {
                currentHeader = item.getTitle();
                visibleItems.add(item);
            } else if (item.getType() == TaskItem.TYPE_TASK) {
                if (hideCompleted && item.isCompleted()) {
                    continue;
                }
                visibleItems.add(item);
            }
        }
        
        // Remove empty headers
        for (int i = visibleItems.size() - 1; i >= 0; i--) {
            if (visibleItems.get(i).getType() == TaskItem.TYPE_HEADER) {
                boolean hasTasks = false;
                for (int j = i + 1; j < visibleItems.size(); j++) {
                    if (visibleItems.get(j).getType() == TaskItem.TYPE_HEADER) break;
                    hasTasks = true;
                }
                if (!hasTasks) {
                    visibleItems.remove(i);
                }
            }
        }
    }
    
    @Override
    public int getItemViewType(int position) {
        return visibleItems.get(position).getType();
    }
    
    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public TextView headerText;
        public HeaderViewHolder(View itemView) {
            super(itemView);
            headerText = itemView.findViewById(R.id.header_text);
        }
    }
    
    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        public TextView taskText, taskTime;
        public CheckBox checkbox;
        public ImageButton btnDelete;
        public TaskViewHolder(View itemView) {
            super(itemView);
            taskText = itemView.findViewById(R.id.task_text);
            taskTime = itemView.findViewById(R.id.task_time);
            checkbox = itemView.findViewById(R.id.checkbox);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
    
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TaskItem.TYPE_HEADER) {
            return new HeaderViewHolder(inflater.inflate(R.layout.item_header, parent, false));
        } else {
            return new TaskViewHolder(inflater.inflate(R.layout.item_todo, parent, false));
        }
    }
    
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TaskItem item = visibleItems.get(position);
        
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).headerText.setText(item.getTitle());
        } 
        else if (holder instanceof TaskViewHolder) {
            TaskViewHolder t = (TaskViewHolder) holder;
            t.taskText.setText(item.getTaskText());
            t.taskTime.setText(formatTime(item.getTimestamp()));
            
            t.checkbox.setOnCheckedChangeListener(null);
            t.checkbox.setChecked(item.isCompleted());
            t.checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                item.setCompleted(isChecked);
                if (isChecked && hideCompleted) {
                    rebuildVisibleList();
                    notifyDataSetChanged();
                } else {
                    updateTaskAppearance(t, item);
                }
            });
            
            updateTaskAppearance(t, item);
            
            t.btnDelete.setOnClickListener(v -> {
                int pos = t.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    TaskItem toDelete = visibleItems.get(pos);
                    new AlertDialog.Builder(v.getContext())
                        .setTitle("Delete Task")
                        .setMessage("Delete \"" + toDelete.getTaskText() + "\"?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            allItems.remove(toDelete);
                            rebuildVisibleList();
                            notifyDataSetChanged();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                }
            });
        }
    }
    
    private void updateTaskAppearance(TaskViewHolder t, TaskItem item) {
        if (item.isCompleted()) {
            t.taskText.setPaintFlags(t.taskText.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
            t.taskText.setTextColor(0xFF666666);
            t.taskTime.setTextColor(0xFF444444);
        } else {
            t.taskText.setPaintFlags(t.taskText.getPaintFlags() & ~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
            t.taskText.setTextColor(0xFFE0E0E0);
            t.taskTime.setTextColor(0xFF666666);
        }
    }
    
    private String formatTime(long timestamp) {
        long diff = System.currentTimeMillis() - timestamp;
        long hours = TimeUnit.MILLISECONDS.toHours(diff);
        if (hours < 24) return "Today at " + timeFormat.format(new Date(timestamp));
        if (hours < 48) return "Yesterday at " + timeFormat.format(new Date(timestamp));
        return dateFormat.format(new Date(timestamp));
    }
    
    @Override
    public int getItemCount() {
        return visibleItems.size();
    }
    
    public void addTask(String text) {
        long now = System.currentTimeMillis();
        String category = getCategory(now);
        int headerIndex = findHeaderIndex(category);
        
        if (headerIndex == -1) {
            allItems.add(new TaskItem(TaskItem.TYPE_HEADER, category));
            headerIndex = allItems.size() - 1;
        }
        
        TaskItem task = new TaskItem(TaskItem.TYPE_TASK, text, now);
        int insertAt = headerIndex + 1;
        while (insertAt < allItems.size() && allItems.get(insertAt).getType() == TaskItem.TYPE_TASK) {
            insertAt++;
        }
        allItems.add(insertAt, task);
        rebuildVisibleList();
        notifyDataSetChanged();
    }
    
    private int findHeaderIndex(String category) {
        for (int i = 0; i < allItems.size(); i++) {
            if (allItems.get(i).getType() == TaskItem.TYPE_HEADER && 
                allItems.get(i).getTitle().equals(category)) return i;
        }
        return -1;
    }
    
    private String getCategory(long timestamp) {
        long days = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - timestamp);
        if (days == 0) return "Today";
        if (days <= 7) return "Previous 7 Days";
        if (days <= 30) return "Previous 30 Days";
        return new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(new Date(timestamp));
    }
}
