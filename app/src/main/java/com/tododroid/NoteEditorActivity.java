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
    private int currentTextColor = 0xFFE0E0E0;
    private boolean isBold = false, isItalic = false, isStrike = false;
    private boolean bulletMode = false, numberMode = false;
    private boolean isHandlingNewline = false;
    
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
        btnSave = findViewById(R.id.btn_save);
        ImageButton btnBack = findViewById(R.id.btn_back);
        
        if (getIntent().hasExtra("note_title")) {
            noteIndex = getIntent().getIntExtra("note_index", -1);
            editorTitle.setText(getIntent().getStringExtra("note_title"));
            String htmlContent = getIntent().getStringExtra("note_content");
            if (htmlContent != null && !htmlContent.isEmpty()) {
                editorContent.setText(Html.fromHtml(htmlContent, Html.FROM_HTML_MODE_LEGACY));
            }
            currentColor = getIntent().getIntExtra("note_color", 0xFF121212);
            currentTextColor = getIntent().getIntExtra("note_text_color", 0xFFE0E0E0);
            int colorIndex = 7;
            for (int i = 0; i < COLORS.length; i++) {
                if (COLORS[i][0] == currentColor) { colorIndex = i; break; }
            }
            applyColorTheme(currentColor, currentTextColor);
            markColorSelected(colorIndex);
        }
        
        editorContent.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (isHandlingNewline) return;
                int selEnd = editorContent.getSelectionEnd();
                if (selEnd > 0 && s.charAt(selEnd - 1) == '\n') {
                    handleNewline(s, selEnd);
                }
            }
        });
        
        btnBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveNote());
        setupFormatting();
        setupColorPicker();
    }
    
    private void handleNewline(Editable s, int cursorPos) {
        int prevLineStart = cursorPos - 2;
        while (prevLineStart >= 0 && s.charAt(prevLineStart) != '\n') prevLineStart--;
        prevLineStart++;
        if (prevLineStart >= cursorPos - 1) return;
        
        String prevLine = s.subSequence(prevLineStart, cursorPos - 1).toString();
        isHandlingNewline = true;
        
        if (prevLine.matches("^\\d+\\.\\s+.*")) {
            String numPart = prevLine.replaceAll("^(\\d+)\\..*", "$1");
            int num = Integer.parseInt(numPart);
            if (prevLine.trim().matches("^\\d+\\.\\s*$")) {
                s.replace(prevLineStart, cursorPos, "\n");
                numberMode = false;
                highlightButton(R.id.fmt_number, false);
            } else {
                s.insert(cursorPos, (num + 1) + ". ");
            }
        } else if (prevLine.startsWith("• ")) {
            if (prevLine.trim().equals("•")) {
                s.replace(prevLineStart, cursorPos, "\n");
                bulletMode = false;
                highlightButton(R.id.fmt_bullet, false);
            } else {
                s.insert(cursorPos, "• ");
            }
        }
        isHandlingNewline = false;
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
    
    private void applyColorTheme(int bgColor, int textColor) {
        editorRoot.setBackgroundColor(bgColor);
        editorTitle.setTextColor(textColor);
        editorTitle.setHintTextColor(adjustAlpha(textColor, 0.5f));
        editorContent.setTextColor(textColor);
        editorContent.setHintTextColor(adjustAlpha(textColor, 0.4f));
    }
    
    private void markColorSelected(int index) {
        for (int id : colorViewIds) findViewById(id).setBackgroundResource(0);
        findViewById(colorViewIds[index]).setBackgroundResource(R.drawable.color_circle_selected);
    }
    
    private int adjustAlpha(int color, float factor) {
        return Color.argb(Math.round(Color.alpha(color) * factor), Color.red(color), Color.green(color), Color.blue(color));
    }
    
    private void setupFormatting() {
        findViewById(R.id.fmt_bold).setOnClickListener(v -> {
            isBold = !isBold;
            applySpan(new StyleSpan(android.graphics.Typeface.BOLD), isBold);
            highlightButton(R.id.fmt_bold, isBold);
        });
        findViewById(R.id.fmt_italic).setOnClickListener(v -> {
            isItalic = !isItalic;
            applySpan(new StyleSpan(android.graphics.Typeface.ITALIC), isItalic);
            highlightButton(R.id.fmt_italic, isItalic);
        });
        findViewById(R.id.fmt_strike).setOnClickListener(v -> {
            isStrike = !isStrike;
            applySpan(new StrikethroughSpan(), isStrike);
            highlightButton(R.id.fmt_strike, isStrike);
        });
        findViewById(R.id.fmt_bullet).setOnClickListener(v -> {
            bulletMode = !bulletMode;
            numberMode = false;
            highlightButton(R.id.fmt_bullet, bulletMode);
            highlightButton(R.id.fmt_number, false);
            if (bulletMode) editorContent.getText().insert(editorContent.getSelectionStart(), "• ");
        });
        findViewById(R.id.fmt_number).setOnClickListener(v -> {
            numberMode = !numberMode;
            bulletMode = false;
            highlightButton(R.id.fmt_number, numberMode);
            highlightButton(R.id.fmt_bullet, false);
            if (numberMode) editorContent.getText().insert(editorContent.getSelectionStart(), "1. ");
        });
        findViewById(R.id.fmt_align_left).setOnClickListener(v -> { editorContent.setGravity(Gravity.START); highlightAlign(R.id.fmt_align_left); });
        findViewById(R.id.fmt_align_center).setOnClickListener(v -> { editorContent.setGravity(Gravity.CENTER); highlightAlign(R.id.fmt_align_center); });
        findViewById(R.id.fmt_align_right).setOnClickListener(v -> { editorContent.setGravity(Gravity.END); highlightAlign(R.id.fmt_align_right); });
        findViewById(R.id.fmt_indent_inc).setOnClickListener(v -> increaseIndent());
        findViewById(R.id.fmt_indent_dec).setOnClickListener(v -> decreaseIndent());
    }
    
    private void applySpan(Object span, boolean add) {
        int start = editorContent.getSelectionStart();
        int end = editorContent.getSelectionEnd();
        if (start == end) return;
        Spannable str = editorContent.getText();
        if (add) str.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        else for (Object s : str.getSpans(start, end, span.getClass())) str.removeSpan(s);
    }
    
    private void increaseIndent() {
        int s = editorContent.getSelectionStart(), e = editorContent.getSelectionEnd();
        if (s != e) editorContent.getText().setSpan(new LeadingMarginSpan.Standard(40), s, e, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
    
    private void decreaseIndent() {
        int s = editorContent.getSelectionStart(), e = editorContent.getSelectionEnd();
        if (s == e) return;
        for (LeadingMarginSpan span : editorContent.getText().getSpans(s, e, LeadingMarginSpan.class))
            editorContent.getText().removeSpan(span);
    }
    
    private void highlightButton(int id, boolean active) {
        ((TextView) findViewById(id)).setTextColor(active ? 0xFF7C3AED : 0xFF888888);
    }
    
    private void highlightAlign(int id) {
        for (int i : new int[]{R.id.fmt_align_left, R.id.fmt_align_center, R.id.fmt_align_right})
            ((TextView) findViewById(i)).setTextColor(i == id ? 0xFF7C3AED : 0xFF888888);
    }
    
    private void saveNote() {
        String title = editorTitle.getText().toString().trim();
        if (title.isEmpty()) { Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show(); return; }
        String htmlContent = Html.toHtml(editorContent.getText(), Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL);
        if (noteIndex >= 0) GlobalData.getInstance().getItems().remove(noteIndex);
        TodoItem note = new TodoItem("Note", title, htmlContent, getIntent().getLongExtra("note_timestamp", System.currentTimeMillis()));
        note.setThemeColor(currentColor);
        GlobalData.getInstance().addItem(note);
        Toast.makeText(this, "Note saved!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
