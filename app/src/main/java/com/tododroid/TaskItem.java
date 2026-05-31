package com.tododroid;

public class TaskItem {
    
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_TASK = 1;
    
    private int type;
    private String title;
    private String taskText;
    private boolean completed;
    private long timestamp;
    private long dueDate = 0;
    
    public TaskItem(int type, String title) {
        this.type = type;
        this.title = title;
    }
    
    public TaskItem(int type, String taskText, long timestamp) {
        this.type = type;
        this.taskText = taskText;
        this.timestamp = timestamp;
        this.completed = false;
    }
    
    public int getType() { return type; }
    public String getTitle() { return title; }
    public String getTaskText() { return taskText; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public long getTimestamp() { return timestamp; }
    public long getDueDate() { return dueDate; }
    public void setDueDate(long dueDate) { this.dueDate = dueDate; }
    public boolean hasDueDate() { return dueDate > 0; }
}
