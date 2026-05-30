package com.tododroid;

import java.util.ArrayList;

public class GlobalData {
    
    private static GlobalData instance;
    private ArrayList<TodoItem> items;
    
    private GlobalData() {
        items = new ArrayList<>();
    }
    
    public static GlobalData getInstance() {
        if (instance == null) {
            instance = new GlobalData();
        }
        return instance;
    }
    
    public ArrayList<TodoItem> getItems() {
        return items;
    }
    
    public ArrayList<TodoItem> getTasks() {
        ArrayList<TodoItem> tasks = new ArrayList<>();
        for (TodoItem item : items) {
            if (item.getType().equals(TodoItem.TYPE_TASK)) {
                tasks.add(item);
            }
        }
        return tasks;
    }
    
    public ArrayList<TodoItem> getNotes() {
        ArrayList<TodoItem> notes = new ArrayList<>();
        for (TodoItem item : items) {
            if (item.getType().equals(TodoItem.TYPE_NOTE)) {
                notes.add(item);
            }
        }
        return notes;
    }
    
    public void addItem(TodoItem item) {
        items.add(0, item); // newest first
    }
    
    public void removeItem(TodoItem item) {
        items.remove(item);
    }
}
