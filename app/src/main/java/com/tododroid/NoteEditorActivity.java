package com.tododroid;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.style.BulletSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
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
    private boolean ignoreTextChange = false;
    
    private static final int[][] COLORS = {
        {0xFFFFFFFF, 0xFF1A1A1A}, {0xFFFEE2E2, 0xFF7F1D1D},
        {0xFFFFEDD5, 0xFF7C2D12}, {0xFFFEF9C3, 0xFF713F12},
        {0xFFDCFCE7, 0xFF14532D}, {0xFFDBEAFE, 0xFF1E3A5F},
        {0xFFEDE9FE, 0xFF3B0764}, {0xFF1F2937, 0xFFE5E7EB},
    };
    private int[] colorIds = {
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
        ImageButton btnBack = findViewById(R.id.btn_back);
        TextView btnSave = findViewById(R.id.btn_save);
        
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
            for (int i = 0; i < COLORS.length; i++)
                if (COLORS[i][0] == currentColor) { ci = i; break; }
            applyColor(currentColor, currentTextColor);
            markColor(ci);
        }
        
        // Auto numbering + bullets via TextWatcher
        editorContent.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (ignoreTextChange) return;
                int pos = editorContent.getSelectionStart();
                if (pos < 2) return;
                if (s.charAt(pos - 1) != '\n') return;
                
                int lineStart = pos - 2;
                while (lineStart >= 0 && s.charAt(lineStart) != '\n') lineStart--;
                lineStart++;
                String prev = s.subSequence(lineStart, pos - 1).toString();
                
                ignoreTextChange = true;
                
                if (prev.matches("\\d+\\.\\s+.*")) {
                    int num = Integer.parseInt(prev.replaceAll("(\\d+)\\..*", "$1"));
                    if (prev.trim().matches("\\d+\\.\\s*")) {
                        s.replace(lineStart, pos, "\n");
                    } else {
                        s.insert(pos, (num + 1) + ". ");
                    }
                } else if (prev.startsWith("• ") && prev.length() > 2) {
                    if (prev.trim().equals("•")) {
                        s.replace(lineStart, pos, "\n");
                    } else {
                        s.insert(pos, "• ");
                    }
                }
                
                ignoreTextChange = false;
            }
        });
        
        btnBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> save());
        
        // Formatting
        findViewById(R.id.fmt_bold).setOnClickListener(v -> toggleSpan(new StyleSpan(android.graphics.Typeface.BOLD), R.id.fmt_bold));
        findViewById(R.id.fmt_italic).setOnClickListener(v -> toggleSpan(new StyleSpan(android.graphics.Typeface.ITALIC), R.id.fmt_italic));
        findViewById(R.id.fmt_strike).setOnClickListener(v -> toggleSpan(new StrikethroughSpan(), R.id.fmt_strike));
        findViewById(R.id.fmt_bullet).setOnClickListener(v -> insertAtCursor("\n• "));
        findViewById(R.id.fmt_number).setOnClickListener(v -> insertAtCursor("\n1. "));
        findViewById(R.id.fmt_align_left).setOnClickListener(v -> { editorContent.setGravity(Gravity.START); hlAlign(R.id.fmt_align_left); });
        findViewById(R.id.fmt_align_center).setOnClickListener(v -> { editorContent.setGravity(Gravity.CENTER); hlAlign(R.id.fmt_align_center); });
        findViewById(R.id.fmt_align_right).setOnClickListener(v -> { editorContent.setGravity(Gravity.END); hlAlign(R.id.fmt_align_right); });
        findViewById(R.id.fmt_indent_inc).setOnClickListener(v -> indent(40));
        findViewById(R.id.fmt_indent_dec).setOnClickListener(v -> indent(-1));
        
        // Colors
        for (int i = 0; i < colorIds.length; i++) {
            final int idx = i;
            findViewById(colorIds[i]).setOnClickListener(v -> {
                currentColor = COLORS[idx][0];
                currentTextColor = COLORS[idx][1];
                applyColor(currentColor, currentTextColor);
                markColor(idx);
            });
        }
    }
    
    private void insertAtCursor(String text) {
        int pos = editorContent.getSelectionStart();
        editorContent.getText().insert(pos, text);
    }
    
    private void toggleSpan(Object span, int btnId) {
        int s = editorContent.getSelectionStart(), e = editorContent.getSelectionEnd();
        if (s == e) return;
        Spannable str = editorContent.getText();
        Object[] existing = str.getSpans(s, e, span.getClass());
        boolean has = false;
        for (Object ex : existing) { str.removeSpan(ex); has = true; }
        if (!has) str.setSpan(span, s, e, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ((TextView)findViewById(btnId)).setTextColor(has ? 0xFF888888 : 0xFF7C3AED);
    }
    
    private void indent(int px) {
        int s = editorContent.getSelectionStart(), e = editorContent.getSelectionEnd();
        if (s == e) return;
        if (px < 0) {
            for (LeadingMarginSpan sp : editorContent.getText().getSpans(s, e, LeadingMarginSpan.class))
                editorContent.getText().removeSpan(sp);
        } else {
            editorContent.getText().setSpan(new LeadingMarginSpan.Standard(px), s, e, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }
    
    private void hlAlign(int id) {
        for (int i : new int[]{R.id.fmt_align_left, R.id.fmt_align_center, R.id.fmt_align_right})
            ((TextView)findViewById(i)).setTextColor(i == id ? 0xFF7C3AED : 0xFF888888);
    }
    
    private void applyColor(int bg, int txt) {
        editorRoot.setBackgroundColor(bg);
        editorTitle.setTextColor(txt);
        editorTitle.setHintTextColor(adjust(txt, 0.5f));
        editorContent.setTextColor(txt);
        editorContent.setHintTextColor(adjust(txt, 0.4f));
    }
    
    private void markColor(int idx) {
        for (int id : colorIds) findViewById(id).setBackgroundResource(0);
        findViewById(colorIds[idx]).setBackgroundResource(R.drawable.color_circle_selected);
    }
    
    private int adjust(int c, float f) {
        return Color.argb((int)(Color.alpha(c)*f), Color.red(c), Color.green(c), Color.blue(c));
    }
    
    private void save() {
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
