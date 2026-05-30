package com.tododroid;

public class TodoItem {
    
    public static final String TYPE_TASK = "Task";
    public static final String TYPE_NOTE = "Note";
    
    private String type;
    private String title;
    private String content;
    private long timestamp;
    private boolean completed;
    private int themeColor;
    
    public TodoItem(String type, String title, String content, long timestamp) {
        this.type = type;
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.completed = false;
        this.themeColor = 0xFF121212; // default dark
    }
    
    public String getType() { return type; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public long getTimestamp() { return timestamp; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public int getThemeColor() { return themeColor; }
    public void setThemeColor(int themeColor) { this.themeColor = themeColor; }
}
