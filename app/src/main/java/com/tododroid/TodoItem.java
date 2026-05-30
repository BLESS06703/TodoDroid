package com.tododroid;

public class TodoItem {
    
    public static final String TYPE_TASK = "Task";
    public static final String TYPE_NOTE = "Note";
    
    private String type;      // "Task" or "Note"
    private String title;
    private String content;
    private long timestamp;
    private boolean completed;
    
    public TodoItem(String type, String title, String content, long timestamp) {
        this.type = type;
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.completed = false;
    }
    
    public String getType() { return type; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public long getTimestamp() { return timestamp; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}
