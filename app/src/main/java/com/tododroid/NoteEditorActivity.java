package com.tododroid;

import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.style.BulletSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.AlignmentSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class NoteEditorActivity extends AppCompatActivity {
    
    private EditText editorTitle, editorContent;
    private TextView btnSave;
    private int noteIndex = -1;
    
    private boolean isBold = false;
    private boolean isItalic = false;
    private boolean isStrike = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);
        
        editorTitle = findViewById(R.id.editor_title);
        editorContent = findViewById(R.id.editor_content);
        btnSave = findViewById(R.id.btn_save);
        ImageButton btnBack = findViewById(R.id.btn_back);
        
        if (getIntent().hasExtra("note_title")) {
            noteIndex = getIntent().getIntExtra("note_index", -1);
            editorTitle.setText(getIntent().getStringExtra("note_title"));
            editorContent.setText(getIntent().getStringExtra("note_content"));
        }
        
        btnBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveNote());
        
        // Formatting buttons
        setupFormatting();
    }
    
    private void setupFormatting() {
        TextView fmtBold = findViewById(R.id.fmt_bold);
        TextView fmtItalic = findViewById(R.id.fmt_italic);
        TextView fmtStrike = findViewById(R.id.fmt_strike);
        TextView fmtBullet = findViewById(R.id.fmt_bullet);
        TextView fmtNumber = findViewById(R.id.fmt_number);
        TextView fmtAlignLeft = findViewById(R.id.fmt_align_left);
        TextView fmtAlignCenter = findViewById(R.id.fmt_align_center);
        TextView fmtHeading = findViewById(R.id.fmt_heading);
        TextView fmtUndo = findViewById(R.id.fmt_undo);
        
        fmtBold.setOnClickListener(v -> {
            isBold = !isBold;
            fmtBold.setTextColor(isBold ? 0xFF7C3AED : 0xFF888888);
            applySpan(new StyleSpan(android.graphics.Typeface.BOLD), isBold);
        });
        
        fmtItalic.setOnClickListener(v -> {
            isItalic = !isItalic;
            fmtItalic.setTextColor(isItalic ? 0xFF7C3AED : 0xFF888888);
            applySpan(new StyleSpan(android.graphics.Typeface.ITALIC), isItalic);
        });
        
        fmtStrike.setOnClickListener(v -> {
            isStrike = !isStrike;
            fmtStrike.setTextColor(isStrike ? 0xFF7C3AED : 0xFF888888);
            applySpan(new StrikethroughSpan(), isStrike);
        });
        
        fmtBullet.setOnClickListener(v -> {
            int start = editorContent.getSelectionStart();
            Editable text = editorContent.getText();
            
            // Find start of line
            int lineStart = start;
            while (lineStart > 0 && text.charAt(lineStart - 1) != '\n') {
                lineStart--;
            }
            
            // Check if already has bullet
            if (text.length() > lineStart + 2 && 
                text.charAt(lineStart) == '•' && text.charAt(lineStart + 1) == ' ') {
                text.delete(lineStart, lineStart + 2);
            } else {
                text.insert(lineStart, "• ");
            }
        });
        
        fmtNumber.setOnClickListener(v -> {
            int start = editorContent.getSelectionStart();
            Editable text = editorContent.getText();
            
            int lineStart = start;
            while (lineStart > 0 && text.charAt(lineStart - 1) != '\n') {
                lineStart--;
            }
            
            text.insert(lineStart, "1. ");
        });
        
        fmtAlignLeft.setOnClickListener(v -> {
            fmtAlignLeft.setTextColor(0xFF7C3AED);
            fmtAlignCenter.setTextColor(0xFF555555);
            applySpan(new AlignmentSpan.Standard(android.text.Layout.Alignment.ALIGN_NORMAL), true);
        });
        
        fmtAlignCenter.setOnClickListener(v -> {
            fmtAlignCenter.setTextColor(0xFF7C3AED);
            fmtAlignLeft.setTextColor(0xFF555555);
            applySpan(new AlignmentSpan.Standard(android.text.Layout.Alignment.ALIGN_CENTER), true);
        });
        
        fmtHeading.setOnClickListener(v -> {
            applySpan(new RelativeSizeSpan(1.5f), true);
            applySpan(new StyleSpan(android.graphics.Typeface.BOLD), true);
        });
        
        fmtUndo.setOnClickListener(v -> {
            editorContent.setText(editorContent.getText().toString());
            isBold = false;
            isItalic = false;
            isStrike = false;
            fmtBold.setTextColor(0xFF888888);
            fmtItalic.setTextColor(0xFF888888);
            fmtStrike.setTextColor(0xFF888888);
        });
    }
    
    private void applySpan(Object span, boolean add) {
        Editable text = editorContent.getText();
        int start = editorContent.getSelectionStart();
        int end = editorContent.getSelectionEnd();
        
        if (start == end) return; // No selection
        
        if (add) {
            text.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            // Remove existing spans of this type
            Object[] spans = text.getSpans(start, end, span.getClass());
            for (Object s : spans) {
                text.removeSpan(s);
            }
        }
    }
    
    private void saveNote() {
        String title = editorTitle.getText().toString().trim();
        String content = editorContent.getText().toString().trim();
        
        if (title.isEmpty()) {
            Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (noteIndex >= 0) {
            GlobalData.getInstance().getItems().remove(noteIndex);
        }
        
        TodoItem note = new TodoItem("Note", title, content, 
            getIntent().getLongExtra("note_timestamp", System.currentTimeMillis()));
        GlobalData.getInstance().addItem(note);
        
        Toast.makeText(this, "Note saved!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
