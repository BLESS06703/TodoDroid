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
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TodoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    
    private ArrayList<TaskItem> sourceList;
    private ArrayList<TaskItem> visibleItems;
    private boolean hideCompleted = false;
    private boolean sortByLatest = true;
    private boolean cardView = true;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, hh:mm a", Locale.getDefault());
    
    public TodoAdapter(ArrayList<TaskItem> items) {
        this.sourceList = items;
        this.visibleItems = new ArrayList<>();
        rebuildVisibleList();
    }
    
    public void setHideCompleted(boolean hide) {
        this.hideCompleted = hide;
        rebuildVisibleList();
        notifyDataSetChanged();
    }
    
    public boolean isHideCompleted() { return hideCompleted; }
    
    public void setSortByLatest(boolean latest) {
        this.sortByLatest = latest;
        rebuildVisibleList();
        notifyDataSetChanged();
    }
    
    public boolean isSortByLatest() { return sortByLatest; }
    
    public void setCardView(boolean card) {
        this.cardView = card;
        notifyDataSetChanged();
    }
    
    public boolean isCardView() { return cardView; }
    
    public void refreshFromSource() {
        rebuildVisibleList();
        notifyDataSetChanged();
    }
    
    private void rebuildVisibleList() {
        visibleItems.clear();
        
        ArrayList<TaskItem> tasksOnly = new ArrayList<>();
        for (TaskItem item : sourceList) {
            if (item.getType() == TaskItem.TYPE_TASK) {
                if (hideCompleted && item.isCompleted()) continue;
                tasksOnly.add(item);
            }
        }
        
        Collections.sort(tasksOnly, (a, b) -> {
            if (sortByLatest) return Long.compare(b.getTimestamp(), a.getTimestamp());
            else return Long.compare(a.getTimestamp(), b.getTimestamp());
        });
        
        String lastHeader = null;
        for (TaskItem task : tasksOnly) {
            String category = getCategory(task.getTimestamp());
            if (!category.equals(lastHeader)) {
                visibleItems.add(new TaskItem(TaskItem.TYPE_HEADER, category));
                lastHeader = category;
            }
            visibleItems.add(task);
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
            return new TaskViewHolder(inflater.inflate(
                cardView ? R.layout.item_todo : R.layout.item_todo_compact, parent, false));
        }
    }
    
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TaskItem item = visibleItems.get(position);
        
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).headerText.setText(item.getTitle());
        } else if (holder instanceof TaskViewHolder) {
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
                    updateAppearance(t, item);
                }
            });
            
            updateAppearance(t, item);
            
            t.btnDelete.setOnClickListener(v -> {
                int pos = t.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    TaskItem toDelete = visibleItems.get(pos);
                    new AlertDialog.Builder(v.getContext())
                        .setTitle("Delete Task")
                        .setMessage("Delete \"" + toDelete.getTaskText() + "\"?")
                        .setPositiveButton("Delete", (d, w) -> {
                            sourceList.remove(toDelete);
                            rebuildVisibleList();
                            notifyDataSetChanged();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                }
            });
        }
    }
    
    private void updateAppearance(TaskViewHolder t, TaskItem item) {
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
    
    private String formatTime(long ts) {
        long diff = System.currentTimeMillis() - ts;
        long hrs = TimeUnit.MILLISECONDS.toHours(diff);
        if (hrs < 24) return "Today at " + timeFormat.format(new Date(ts));
        if (hrs < 48) return "Yesterday at " + timeFormat.format(new Date(ts));
        return dateFormat.format(new Date(ts));
    }
    
    @Override
    public int getItemCount() { return visibleItems.size(); }
    
    public void addTask(TaskItem task) {
        sourceList.add(task);
        rebuildVisibleList();
        notifyDataSetChanged();
    }
    
    private String getCategory(long ts) {
        long days = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - ts);
        if (days == 0) return "Today";
        if (days <= 7) return "Previous 7 Days";
        if (days <= 30) return "Previous 30 Days";
        return new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(new Date(ts));
    }
}
