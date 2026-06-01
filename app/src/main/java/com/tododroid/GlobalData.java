package com.tododroid;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class GlobalData {
    
    private static GlobalData instance;
    private ArrayList<TodoItem> items;
    private static final String FILE_NAME = "tododroid_data.json";
    
    private GlobalData() {
        items = new ArrayList<>();
    }
    
    public static GlobalData getInstance() {
        if (instance == null) instance = new GlobalData();
        return instance;
    }
    
    public ArrayList<TodoItem> getItems() { return items; }
    
    public ArrayList<TodoItem> getTasks() {
        ArrayList<TodoItem> tasks = new ArrayList<>();
        for (TodoItem item : items) {
            if (item.getType().equals(TodoItem.TYPE_TASK)) tasks.add(item);
        }
        return tasks;
    }
    
    public ArrayList<TodoItem> getNotes() {
        ArrayList<TodoItem> notes = new ArrayList<>();
        for (TodoItem item : items) {
            if (item.getType().equals(TodoItem.TYPE_NOTE)) notes.add(item);
        }
        return notes;
    }
    
    public void addItem(TodoItem item) {
        items.add(0, item);
    }
    
    public void removeItem(TodoItem item) {
        items.remove(item);
    }
    
    public void saveToFile(Context context) {
        try {
            File file = new File(context.getFilesDir(), FILE_NAME);
            FileWriter writer = new FileWriter(file);
            new Gson().toJson(items, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void loadFromFile(Context context) {
        try {
            File file = new File(context.getFilesDir(), FILE_NAME);
            if (file.exists()) {
                FileReader reader = new FileReader(file);
                Type listType = new TypeToken<ArrayList<TodoItem>>(){}.getType();
                items = new Gson().fromJson(reader, listType);
                if (items == null) items = new ArrayList<>();
                reader.close();
            }
        } catch (IOException e) {
            items = new ArrayList<>();
        }
    }
}
