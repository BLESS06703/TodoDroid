package com.tododroid;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
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
    private LinearLayout editorRoot;
    private int noteIndex = -1;
    private int currentColor = 0xFF121212;
    private int currentTextColor = 0xFFE0E0E0;
    
    private static final int[][] COLORS = {
        {0xFFFFFFFF, 0xFF1A1A1A},
        {0xFFFEE2E2, 0xFF7F1D1D},
        {0xFFFFEDD5, 0xFF7C2D12},
        {0xFFFEF9C3, 0xFF713F12},
        {0xFFDCFCE7, 0xFF14532D},
        {0xFFDBEAFE, 0xFF1E3A5F},
        {0xFFEDE9FE, 0xFF3B0764},
        {0xFF1F2937, 0xFFE5E7EB},
    };
    
    private int[] colorViewIds = {
        R.id.color_default, R.id.color_red, R.id.color_orange,
        R.id.color_yellow, R.id.color_green, R.id.color_blue,
        R.id.color_purple, R.id.color_dark
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);
        
        editorRoot = findViewById(R.id.editor_root);
        editorTitle = findViewById(R.id.editor_title);
        editorContent = findViewById(R.id.editor_content);
        TextView btnSave = findViewById(R.id.btn_save);
        ImageButton btnBack = findViewById(R.id.btn_back);
        
        if (getIntent().hasExtra("note_title")) {
            noteIndex = getIntent().getIntExtra("note_index", -1);
            editorTitle.setText(getIntent().getStringExtra("note_title"));
            String html = getIntent().getStringExtra("note_content");
            if (html != null && !html.isEmpty()) {
                editorContent.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY));
            }
            currentColor = getIntent().getIntExtra("note_color", 0xFF121212);
            currentTextColor = getIntent().getIntExtra("note_text_color", 0xFFE0E0E0);
            int ci = 7;
            for (int i = 0; i < COLORS.length; i++) {
                if (COLORS[i][0] == currentColor) { ci = i; break; }
            }
            applyColorTheme(currentColor, currentTextColor);
            markColorSelected(ci);
        }
        
        btnBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveNote());
        setupFormatting();
        setupColorPicker();
    }
    
    private void setupColorPicker() {
        for (int i = 0; i < colorViewIds.length; i++) {
            final int index = i;
            findViewById(colorViewIds[i]).setOnClickListener(v -> {
                currentColor = COLORS[index][0];
                currentTextColor = COLORS[index][1];
                applyColorTheme(currentColor, currentTextColor);
                markColorSelected(index);
            });
        }
    }
    
    private void applyColorTheme(int bg, int txt) {
        editorRoot.setBackgroundColor(bg);
        editorTitle.setTextColor(txt);
        editorTitle.setHintTextColor(adjustAlpha(txt, 0.5f));
        editorContent.setTextColor(txt);
        editorContent.setHintTextColor(adjustAlpha(txt, 0.4f));
    }
    
    private void markColorSelected(int index) {
        for (int id : colorViewIds) findViewById(id).setBackgroundResource(0);
        findViewById(colorViewIds[index]).setBackgroundResource(R.drawable.color_circle_selected);
    }
    
    private int adjustAlpha(int c, float f) {
        return Color.argb((int)(Color.alpha(c)*f), Color.red(c), Color.green(c), Color.blue(c));
    }
    
    private void setupFormatting() {
        // Bold
        findViewById(R.id.fmt_bold).setOnClickListener(v -> {
            int s = editorContent.getSelectionStart(), e = editorContent.getSelectionEnd();
            if (s == e) return;
            Spannable str = editorContent.getText();
            StyleSpan[] spans = str.getSpans(s, e, StyleSpan.class);
            boolean has = false;
            for (StyleSpan sp : spans) {
                if (sp.getStyle() == android.graphics.Typeface.BOLD) { str.removeSpan(sp); has = true; }
            }
            if (!has) str.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), s, e, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ((TextView)findViewById(R.id.fmt_bold)).setTextColor(has ? 0xFF888888 : 0xFF7C3AED);
        });
        
        // Italic
        findViewById(R.id.fmt_italic).setOnClickListener(v -> {
            int s = editorContent.getSelectionStart(), e = editorContent.getSelectionEnd();
            if (s == e) return;
            Spannable str = editorContent.getText();
            StyleSpan[] spans = str.getSpans(s, e, StyleSpan.class);
            boolean has = false;
            for (StyleSpan sp : spans) {
                if (sp.getStyle() == android.graphics.Typeface.ITALIC) { str.removeSpan(sp); has = true; }
            }
            if (!has) str.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), s, e, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ((TextView)findViewById(R.id.fmt_italic)).setTextColor(has ? 0xFF888888 : 0xFF7C3AED);
        });
        
        // Strikethrough
        findViewById(R.id.fmt_strike).setOnClickListener(v -> {
            int s = editorContent.getSelectionStart(), e = editorContent.getSelectionEnd();
            if (s == e) return;
            Spannable str = editorContent.getText();
            StrikethroughSpan[] spans = str.getSpans(s, e, StrikethroughSpan.class);
            if (spans.length > 0) {
                for (StrikethroughSpan sp : spans) str.removeSpan(sp);
                ((TextView)findViewById(R.id.fmt_strike)).setTextColor(0xFF888888);
            } else {
                str.setSpan(new StrikethroughSpan(), s, e, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                ((TextView)findViewById(R.id.fmt_strike)).setTextColor(0xFF7C3AED);
            }
        });
        
        // Bullet
        findViewById(R.id.fmt_bullet).setOnClickListener(v -> {
            int pos = editorContent.getSelectionStart();
            editorContent.getText().insert(pos, "\n• ");
        });
        
        // Number
        findViewById(R.id.fmt_number).setOnClickListener(v -> {
            int pos = editorContent.getSelectionStart();
            editorContent.getText().insert(pos, "\n1. ");
        });
        
        // Alignment
        findViewById(R.id.fmt_align_left).setOnClickListener(v -> { editorContent.setGravity(Gravity.START); highlightAlign(R.id.fmt_align_left); });
        findViewById(R.id.fmt_align_center).setOnClickListener(v -> { editorContent.setGravity(Gravity.CENTER); highlightAlign(R.id.fmt_align_center); });
        findViewById(R.id.fmt_align_right).setOnClickListener(v -> { editorContent.setGravity(Gravity.END); highlightAlign(R.id.fmt_align_right); });
        
        // Indent
        findViewById(R.id.fmt_indent_inc).setOnClickListener(v -> {
            int s = editorContent.getSelectionStart(), e = editorContent.getSelectionEnd();
            if (s != e) editorContent.getText().setSpan(new LeadingMarginSpan.Standard(40), s, e, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        });
        findViewById(R.id.fmt_indent_dec).setOnClickListener(v -> {
            int s = editorContent.getSelectionStart(), e = editorContent.getSelectionEnd();
            if (s == e) return;
            for (LeadingMarginSpan sp : editorContent.getText().getSpans(s, e, LeadingMarginSpan.class))
                editorContent.getText().removeSpan(sp);
        });
    }
    
    private void highlightAlign(int id) {
        for (int i : new int[]{R.id.fmt_align_left, R.id.fmt_align_center, R.id.fmt_align_right})
            ((TextView)findViewById(i)).setTextColor(i == id ? 0xFF7C3AED : 0xFF888888);
    }
    
    private void saveNote() {
        String title = editorTitle.getText().toString().trim();
        if (title.isEmpty()) { Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show(); return; }
        String html = Html.toHtml(editorContent.getText(), Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL);
        if (noteIndex >= 0) GlobalData.getInstance().getItems().remove(noteIndex);
        TodoItem note = new TodoItem("Note", title, html, getIntent().getLongExtra("note_timestamp", System.currentTimeMillis()));
        note.setThemeColor(currentColor);
        GlobalData.getInstance().addItem(note);
        Toast.makeText(this, "Note saved!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
