package com.tododroid;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BulletSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.ImageButton;
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
        
        if (getIntent().hasExtra("note_title")) {
            noteIndex = getIntent().getIntExtra("note_index", -1);
            editorTitle.setText(getIntent().getStringExtra("note_title"));
            editorContent.setText(getIntent().getStringExtra("note_content"));
        }
        
        btnBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveNote());
        
        // Formatting toolbar
        setupFormatting();
    }
    
    private void setupFormatting() {
        findViewById(R.id.fmt_bold).setOnClickListener(v -> toggleBold());
        findViewById(R.id.fmt_italic).setOnClickListener(v -> toggleItalic());
        findViewById(R.id.fmt_strike).setOnClickListener(v -> toggleStrikethrough());
        findViewById(R.id.fmt_bullet).setOnClickListener(v -> insertBullet());
        findViewById(R.id.fmt_number).setOnClickListener(v -> insertNumber());
        findViewById(R.id.fmt_align_left).setOnClickListener(v -> alignLeft());
        findViewById(R.id.fmt_align_center).setOnClickListener(v -> alignCenter());
        findViewById(R.id.fmt_align_right).setOnClickListener(v -> alignRight());
        findViewById(R.id.fmt_indent_inc).setOnClickListener(v -> increaseIndent());
        findViewById(R.id.fmt_indent_dec).setOnClickListener(v -> decreaseIndent());
    }
    
    private void toggleBold() {
        int start = editorContent.getSelectionStart();
        int end = editorContent.getSelectionEnd();
        if (start == end) return;
        
        Spannable str = editorContent.getText();
        StyleSpan[] spans = str.getSpans(start, end, StyleSpan.class);
        boolean hasBold = false;
        for (StyleSpan span : spans) {
            if (span.getStyle() == android.graphics.Typeface.BOLD) {
                str.removeSpan(span);
                hasBold = true;
            }
        }
        if (!hasBold) {
            str.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        highlightButton(R.id.fmt_bold, !hasBold);
    }
    
    private void toggleItalic() {
        int start = editorContent.getSelectionStart();
        int end = editorContent.getSelectionEnd();
        if (start == end) return;
        
        Spannable str = editorContent.getText();
        StyleSpan[] spans = str.getSpans(start, end, StyleSpan.class);
        boolean hasItalic = false;
        for (StyleSpan span : spans) {
            if (span.getStyle() == android.graphics.Typeface.ITALIC) {
                str.removeSpan(span);
                hasItalic = true;
            }
        }
        if (!hasItalic) {
            str.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), start, end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        highlightButton(R.id.fmt_italic, !hasItalic);
    }
    
    private void toggleStrikethrough() {
        int start = editorContent.getSelectionStart();
        int end = editorContent.getSelectionEnd();
        if (start == end) return;
        
        Spannable str = editorContent.getText();
        StrikethroughSpan[] spans = str.getSpans(start, end, StrikethroughSpan.class);
        if (spans.length > 0) {
            for (StrikethroughSpan span : spans) {
                str.removeSpan(span);
            }
            highlightButton(R.id.fmt_strike, false);
        } else {
            str.setSpan(new StrikethroughSpan(), start, end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            highlightButton(R.id.fmt_strike, true);
        }
    }
    
    private void insertBullet() {
        int start = editorContent.getSelectionStart();
        int end = editorContent.getSelectionEnd();
        if (start == end) return;
        
        Spannable str = editorContent.getText();
        BulletSpan[] spans = str.getSpans(start, end, BulletSpan.class);
        if (spans.length > 0) {
            for (BulletSpan span : spans) str.removeSpan(span);
        } else {
            str.setSpan(new BulletSpan(24, 0xFF7C3AED), start, end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }
    
    private void insertNumber() {
        int pos = editorContent.getSelectionStart();
        String text = editorContent.getText().toString();
        
        // Find start of line
        int lineStart = text.lastIndexOf('\n', pos - 1);
        if (lineStart == -1) lineStart = 0;
        else lineStart++;
        
        String before = text.substring(0, lineStart);
        String after = text.substring(lineStart);
        
        // Simple number insertion
        editorContent.getText().insert(lineStart, "1. ");
        editorContent.setSelection(lineStart + 3);
    }
    
    private void alignLeft() {
        editorContent.setGravity(Gravity.START);
        highlightAlignButton(R.id.fmt_align_left);
    }
    
    private void alignCenter() {
        editorContent.setGravity(Gravity.CENTER);
        highlightAlignButton(R.id.fmt_align_center);
    }
    
    private void alignRight() {
        editorContent.setGravity(Gravity.END);
        highlightAlignButton(R.id.fmt_align_right);
    }
    
    private void increaseIndent() {
        int start = editorContent.getSelectionStart();
        int end = editorContent.getSelectionEnd();
        if (start == end) return;
        
        Spannable str = editorContent.getText();
        str.setSpan(new LeadingMarginSpan.Standard(40), start, end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
    
    private void decreaseIndent() {
        int start = editorContent.getSelectionStart();
        int end = editorContent.getSelectionEnd();
        if (start == end) return;
        
        Spannable str = editorContent.getText();
        LeadingMarginSpan[] spans = str.getSpans(start, end, LeadingMarginSpan.class);
        for (LeadingMarginSpan span : spans) {
            str.removeSpan(span);
        }
    }
    
    private void highlightButton(int id, boolean active) {
        TextView btn = findViewById(id);
        if (active) {
            btn.setTextColor(0xFF7C3AED);
        } else {
            btn.setTextColor(0xFF888888);
        }
    }
    
    private void highlightAlignButton(int activeId) {
        int[] ids = {R.id.fmt_align_left, R.id.fmt_align_center, R.id.fmt_align_right};
        for (int id : ids) {
            TextView btn = findViewById(id);
            btn.setTextColor(id == activeId ? 0xFF7C3AED : 0xFF888888);
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
