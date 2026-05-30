package com.tododroid;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.BulletSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.KeyEvent;
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
    private int numberCount = 0;
    
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
        
        // Auto bullet/number continuation on Enter
        editorContent.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                return handleEnterKey();
            }
            return false;
        });
        
        btnBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveNote());
        
        setupFormatting();
        setupColorPicker();
    }
    
    private boolean handleEnterKey() {
        int pos = editorContent.getSelectionStart();
        String text = editorContent.getText().toString();
        int lineStart = text.lastIndexOf('\n', pos - 1);
        if (lineStart == -1) lineStart = 0; else lineStart++;
        String currentLine = text.substring(lineStart, pos).trim();
        
        if (bulletMode && currentLine.startsWith("•")) {
            // If line is just a bullet, end bullet mode
            if (currentLine.equals("•")) {
                editorContent.getText().replace(lineStart, pos, "\n");
                bulletMode = false;
                return true;
            }
            // Continue bullet
            editorContent.getText().insert(pos, "\n• ");
            return true;
        }
        
        if (numberMode && currentLine.matches("\\d+\\..*")) {
            if (currentLine.matches("\\d+\\.\\s*")) {
                editorContent.getText().replace(lineStart, pos, "\n");
                numberMode = false;
                return true;
            }
            numberCount++;
            editorContent.getText().insert(pos, "\n" + numberCount + ". ");
            return true;
        }
        
        return false;
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
        findViewById(R.id.fmt_bold).setOnClickListener(v -> toggleBold());
        findViewById(R.id.fmt_italic).setOnClickListener(v -> toggleItalic());
        findViewById(R.id.fmt_strike).setOnClickListener(v -> toggleStrikethrough());
        findViewById(R.id.fmt_bullet).setOnClickListener(v -> toggleBullet());
        findViewById(R.id.fmt_number).setOnClickListener(v -> toggleNumber());
        findViewById(R.id.fmt_align_left).setOnClickListener(v -> alignLeft());
        findViewById(R.id.fmt_align_center).setOnClickListener(v -> alignCenter());
        findViewById(R.id.fmt_align_right).setOnClickListener(v -> alignRight());
        findViewById(R.id.fmt_indent_inc).setOnClickListener(v -> increaseIndent());
        findViewById(R.id.fmt_indent_dec).setOnClickListener(v -> decreaseIndent());
    }
    
    private void toggleBold() {
        isBold = !isBold;
        applySpanToSelection(new StyleSpan(android.graphics.Typeface.BOLD), isBold);
        highlightButton(R.id.fmt_bold, isBold);
    }
    
    private void toggleItalic() {
        isItalic = !isItalic;
        applySpanToSelection(new StyleSpan(android.graphics.Typeface.ITALIC), isItalic);
        highlightButton(R.id.fmt_italic, isItalic);
    }
    
    private void toggleStrikethrough() {
        isStrike = !isStrike;
        applySpanToSelection(new StrikethroughSpan(), isStrike);
        highlightButton(R.id.fmt_strike, isStrike);
    }
    
    private void toggleBullet() {
        bulletMode = !bulletMode;
        numberMode = false;
        highlightButton(R.id.fmt_bullet, bulletMode);
        highlightButton(R.id.fmt_number, false);
        if (bulletMode) {
            int pos = editorContent.getSelectionStart();
            editorContent.getText().insert(pos, "• ");
        }
    }
    
    private void toggleNumber() {
        numberMode = !numberMode;
        bulletMode = false;
        numberCount = 1;
        highlightButton(R.id.fmt_number, numberMode);
        highlightButton(R.id.fmt_bullet, false);
        if (numberMode) {
            int pos = editorContent.getSelectionStart();
            editorContent.getText().insert(pos, "1. ");
        }
    }
    
    private void applySpanToSelection(Object span, boolean add) {
        int start = editorContent.getSelectionStart();
        int end = editorContent.getSelectionEnd();
        if (start == end) return;
        Spannable str = editorContent.getText();
        if (add) {
            str.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            Object[] spans = str.getSpans(start, end, span.getClass());
            for (Object s : spans) str.removeSpan(s);
        }
    }
    
    private void alignLeft() { editorContent.setGravity(Gravity.START); highlightAlign(R.id.fmt_align_left); }
    private void alignCenter() { editorContent.setGravity(Gravity.CENTER); highlightAlign(R.id.fmt_align_center); }
    private void alignRight() { editorContent.setGravity(Gravity.END); highlightAlign(R.id.fmt_align_right); }
    
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
        
        // Save as HTML to preserve formatting
        String htmlContent = Html.toHtml(editorContent.getText(), Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL);
        
        if (noteIndex >= 0) GlobalData.getInstance().getItems().remove(noteIndex);
        
        TodoItem note = new TodoItem("Note", title, htmlContent,
            getIntent().getLongExtra("note_timestamp", System.currentTimeMillis()));
        note.setThemeColor(currentColor);
        GlobalData.getInstance().addItem(note);
        
        Toast.makeText(this, "Note saved!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
