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
    
    private ArrayList<TaskItem> sourceList, visibleItems;
    private boolean hideCompleted = false, sortByLatest = true, cardView = true;
    private SimpleDateFormat timeFmt = new SimpleDateFormat("hh:mm a", Locale.getDefault());
    private SimpleDateFormat dateFmt = new SimpleDateFormat("MMM d, hh:mm a", Locale.getDefault());
    
    public TodoAdapter(ArrayList<TaskItem> items) {
        this.sourceList = items;
        this.visibleItems = new ArrayList<>();
        rebuildVisibleList();
    }
    
    public void setHideCompleted(boolean h) { hideCompleted = h; rebuildVisibleList(); notifyDataSetChanged(); }
    public boolean isHideCompleted() { return hideCompleted; }
    public void setSortByLatest(boolean l) { sortByLatest = l; rebuildVisibleList(); notifyDataSetChanged(); }
    public void setCardView(boolean c) { cardView = c; notifyDataSetChanged(); }
    public void refreshFromSource() { rebuildVisibleList(); notifyDataSetChanged(); }
    
    private void rebuildVisibleList() {
        visibleItems.clear();
        ArrayList<TaskItem> tasks = new ArrayList<>();
        for (TaskItem item : sourceList) {
            if (item.getType() == TaskItem.TYPE_TASK && !(hideCompleted && item.isCompleted())) tasks.add(item);
        }
        Collections.sort(tasks, (a, b) -> sortByLatest ? Long.compare(b.getTimestamp(), a.getTimestamp()) : Long.compare(a.getTimestamp(), b.getTimestamp()));
        String lastH = null;
        for (TaskItem t : tasks) {
            String cat = getCategory(t.getTimestamp());
            if (!cat.equals(lastH)) { visibleItems.add(new TaskItem(TaskItem.TYPE_HEADER, cat)); lastH = cat; }
            visibleItems.add(t);
        }
    }
    
    @Override public int getItemViewType(int p) { return visibleItems.get(p).getType(); }
    
    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView headerText;
        HeaderViewHolder(View v) { super(v); headerText = v.findViewById(R.id.header_text); }
    }
    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskText, taskTime;
        CheckBox checkbox;
        ImageButton btnDelete;
        TaskViewHolder(View v) {
            super(v);
            taskText = v.findViewById(R.id.task_text);
            taskTime = v.findViewById(R.id.task_time);
            checkbox = v.findViewById(R.id.checkbox);
            btnDelete = v.findViewById(R.id.btn_delete);
        }
    }
    
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup p, int vt) {
        LayoutInflater inf = LayoutInflater.from(p.getContext());
        if (vt == TaskItem.TYPE_HEADER) return new HeaderViewHolder(inf.inflate(R.layout.item_header, p, false));
        return new TaskViewHolder(inf.inflate(cardView ? R.layout.item_todo : R.layout.item_todo_compact, p, false));
    }
    
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder h, int pos) {
        TaskItem item = visibleItems.get(pos);
        if (h instanceof HeaderViewHolder) { ((HeaderViewHolder)h).headerText.setText(item.getTitle()); return; }
        TaskViewHolder t = (TaskViewHolder) h;
        t.taskText.setText(item.getTaskText());
        t.taskTime.setText(formatTime(item.getTimestamp()));
        
        t.checkbox.setOnCheckedChangeListener(null);
        t.checkbox.setChecked(item.isCompleted());
        t.checkbox.setOnCheckedChangeListener((b, ch) -> {
            item.setCompleted(ch);
            if (ch && hideCompleted) { rebuildVisibleList(); notifyDataSetChanged(); }
            else updateAppearance(t, item);
        });
        updateAppearance(t, item);
        
        t.btnDelete.setOnClickListener(v -> {
            int adapterPos = t.getAdapterPosition();
            if (adapterPos != RecyclerView.NO_POSITION) {
                TaskItem del = visibleItems.get(adapterPos);
                new AlertDialog.Builder(v.getContext())
                    .setTitle("Delete Task")
                    .setMessage("Delete \"" + del.getTaskText() + "\"?")
                    .setPositiveButton("Delete", (d, w) -> {
                        sourceList.remove(del);
                        GlobalData.getInstance().saveToFile(v.getContext());
                        rebuildVisibleList();
                        notifyDataSetChanged();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            }
        });
    }
    
    private void updateAppearance(TaskViewHolder t, TaskItem item) {
        if (item.isCompleted()) {
            t.taskText.setPaintFlags(t.taskText.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
            t.taskText.setTextColor(0xFF666666); t.taskTime.setTextColor(0xFF444444);
        } else {
            t.taskText.setPaintFlags(t.taskText.getPaintFlags() & ~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
            t.taskText.setTextColor(0xFFE0E0E0); t.taskTime.setTextColor(0xFF666666);
        }
    }
    
    private String formatTime(long ts) {
        long diff = System.currentTimeMillis() - ts;
        long hrs = TimeUnit.MILLISECONDS.toHours(diff);
        if (hrs < 24) return "Today at " + timeFmt.format(new Date(ts));
        if (hrs < 48) return "Yesterday at " + timeFmt.format(new Date(ts));
        return dateFmt.format(new Date(ts));
    }
    
    @Override public int getItemCount() { return visibleItems.size(); }
    
    private String getCategory(long ts) {
        long days = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - ts);
        if (days == 0) return "Today";
        if (days <= 7) return "Previous 7 Days";
        if (days <= 30) return "Previous 30 Days";
        return new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(new Date(ts));
    }
}
