package com.tododroid;

public class TaskItem {
    
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_TASK = 1;
    
    private int type;
    private String title;      // For headers
    private String taskText;   // For tasks
    private boolean completed;
    private long timestamp;
    
    // Constructor for headers
    public TaskItem(int type, String title) {
        this.type = type;
        this.title = title;
    }
    
    // Constructor for tasks
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
}
