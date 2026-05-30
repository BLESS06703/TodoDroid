package com.tododroid;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.style.BulletSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class NoteEditorActivity extends AppCompatActivity {
    
    private EditText editorTitle, editorContent;
    private TextView btnSave;
    private LinearLayout editorRoot;
    private int noteIndex = -1;
    private int currentColor = 0xFF121212;
    private View currentColorView;
    
    // Theme colors with their text tints
    private static final int[][] COLORS = {
        {0xFFFFFFFF, 0xFF1A1A1A}, // White bg, dark text
        {0xFFFEE2E2, 0xFF7F1D1D}, // Red
        {0xFFFFEDD5, 0xFF7C2D12}, // Orange
        {0xFFFEF9C3, 0xFF713F12}, // Yellow
        {0xFFDCFCE7, 0xFF14532D}, // Green
        {0xFFDBEAFE, 0xFF1E3A5F}, // Blue
        {0xFFEDE9FE, 0xFF3B0764}, // Purple
        {0xFF1F2937, 0xFFE5E7EB}, // Dark
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);
        
        editorRoot = findViewById(R.id.editor_root);
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
        
        setupFormatting();
        setupColorPicker();
    }
    
    private void setupColorPicker() {
        int[] colorIds = {
            R.id.color_default, R.id.color_red, R.id.color_orange,
            R.id.color_yellow, R.id.color_green, R.id.color_blue,
            R.id.color_purple, R.id.color_dark
        };
        
        currentColorView = findViewById(R.id.color_dark);
        
        for (int i = 0; i < colorIds.length; i++) {
            View colorView = findViewById(colorIds[i]);
            final int index = i;
            colorView.setOnClickListener(v -> applyColor(index, colorView));
        }
    }
    
    private void applyColor(int index, View colorView) {
        int bgColor = COLORS[index][0];
        int textColor = COLORS[index][1];
        
        // Update root background
        editorRoot.setBackgroundColor(bgColor);
        
        // Update title colors
        editorTitle.setTextColor(textColor);
        editorTitle.setHintTextColor(adjustAlpha(textColor, 0.5f));
        
        // Update content colors
        editorContent.setTextColor(textColor);
        editorContent.setHintTextColor(adjustAlpha(textColor, 0.4f));
        
        // Update selection indicator
        if (currentColorView != null) {
            currentColorView.setBackgroundResource(0);
        }
        colorView.setBackgroundResource(R.drawable.color_circle_selected);
        currentColorView = colorView;
        currentColor = bgColor;
    }
    
    private int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
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
        if (!hasBold)
            str.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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
        if (!hasItalic)
            str.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        highlightButton(R.id.fmt_italic, !hasItalic);
    }
    
    private void toggleStrikethrough() {
        int start = editorContent.getSelectionStart();
        int end = editorContent.getSelectionEnd();
        if (start == end) return;
        Spannable str = editorContent.getText();
        StrikethroughSpan[] spans = str.getSpans(start, end, StrikethroughSpan.class);
        if (spans.length > 0) {
            for (StrikethroughSpan span : spans) str.removeSpan(span);
            highlightButton(R.id.fmt_strike, false);
        } else {
            str.setSpan(new StrikethroughSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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
            str.setSpan(new BulletSpan(24, 0xFF7C3AED), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }
    
    private void insertNumber() {
        int pos = editorContent.getSelectionStart();
        String text = editorContent.getText().toString();
        int lineStart = text.lastIndexOf('\n', pos - 1);
        if (lineStart == -1) lineStart = 0;
        else lineStart++;
        editorContent.getText().insert(lineStart, "1. ");
        editorContent.setSelection(lineStart + 3);
    }
    
    private void alignLeft() { editorContent.setGravity(Gravity.START); highlightAlignButton(R.id.fmt_align_left); }
    private void alignCenter() { editorContent.setGravity(Gravity.CENTER); highlightAlignButton(R.id.fmt_align_center); }
    private void alignRight() { editorContent.setGravity(Gravity.END); highlightAlignButton(R.id.fmt_align_right); }
    
    private void increaseIndent() {
        int start = editorContent.getSelectionStart();
        int end = editorContent.getSelectionEnd();
        if (start == end) return;
        editorContent.getText().setSpan(new LeadingMarginSpan.Standard(40), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
    
    private void decreaseIndent() {
        int start = editorContent.getSelectionStart();
        int end = editorContent.getSelectionEnd();
        if (start == end) return;
        LeadingMarginSpan[] spans = editorContent.getText().getSpans(start, end, LeadingMarginSpan.class);
        for (LeadingMarginSpan span : spans) editorContent.getText().removeSpan(span);
    }
    
    private void highlightButton(int id, boolean active) {
        ((TextView) findViewById(id)).setTextColor(active ? 0xFF7C3AED : 0xFF888888);
    }
    
    private void highlightAlignButton(int activeId) {
        for (int id : new int[]{R.id.fmt_align_left, R.id.fmt_align_center, R.id.fmt_align_right})
            ((TextView) findViewById(id)).setTextColor(id == activeId ? 0xFF7C3AED : 0xFF888888);
    }
    
    private void saveNote() {
        String title = editorTitle.getText().toString().trim();
        String content = editorContent.getText().toString().trim();
        if (title.isEmpty()) { Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show(); return; }
        if (noteIndex >= 0) GlobalData.getInstance().getItems().remove(noteIndex);
        GlobalData.getInstance().addItem(new TodoItem("Note", title, content, getIntent().getLongExtra("note_timestamp", System.currentTimeMillis())));
        Toast.makeText(this, "Note saved!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
