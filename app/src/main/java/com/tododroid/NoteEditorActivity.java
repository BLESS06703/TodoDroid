package com.tododroid;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class NoteEditorActivity extends AppCompatActivity {
    
    private EditText editorTitle, editorContent;
    private TextView btnSave;
    private int noteIndex = -1;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);
        
        editorTitle = findViewById(R.id.editor_title);
        editorContent = findViewById(R.id.editor_content);
        btnSave = findViewById(R.id.btn_save);
        ImageButton btnBack = findViewById(R.id.btn_back);
        
        // Load existing note data if editing
        if (getIntent().hasExtra("note_title")) {
            noteIndex = getIntent().getIntExtra("note_index", -1);
            editorTitle.setText(getIntent().getStringExtra("note_title"));
            editorContent.setText(getIntent().getStringExtra("note_content"));
        }
        
        btnBack.setOnClickListener(v -> finish());
        
        btnSave.setOnClickListener(v -> saveNote());
    }
    
    private void saveNote() {
        String title = editorTitle.getText().toString().trim();
        String content = editorContent.getText().toString().trim();
        
        if (title.isEmpty()) {
            Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (noteIndex >= 0) {
            // Editing existing note - update it
            GlobalData.getInstance().getItems().remove(noteIndex);
        }
        
        TodoItem note = new TodoItem("Note", title, content, 
            getIntent().getLongExtra("note_timestamp", System.currentTimeMillis()));
        GlobalData.getInstance().addItem(note);
        
        Toast.makeText(this, "Note saved!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
