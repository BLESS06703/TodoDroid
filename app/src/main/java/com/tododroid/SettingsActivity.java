package com.tododroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SettingsActivity extends AppCompatActivity {
    
    private boolean isDark;
    private TextView themeStatus;
    private SharedPreferences prefs;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = getSharedPreferences("settings", MODE_PRIVATE);
        isDark = prefs.getBoolean("dark_mode", true);
        applyTheme();
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        ((ImageButton) findViewById(R.id.btn_back)).setOnClickListener(v -> finish());
        
        themeStatus = findViewById(R.id.theme_status);
        updateThemeLabel();
        
        findViewById(R.id.setting_theme).setOnClickListener(v -> {
            isDark = !isDark;
            prefs.edit().putBoolean("dark_mode", isDark).apply();
            applyTheme();
            updateThemeLabel();
        });
        
        findViewById(R.id.setting_backup).setOnClickListener(v -> {
            CloudBackup.backupToDrive(this);
        });
        
        findViewById(R.id.setting_restore).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("application/json");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            restoreLauncher.launch(intent);
        });
    }
    
    private final ActivityResultLauncher<Intent> restoreLauncher = 
        registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Uri uri = result.getData().getData();
                CloudBackup.restoreFromFile(this, uri);
            }
        });
    
    private void applyTheme() {
        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
    
    private void updateThemeLabel() {
        if (themeStatus != null) {
            themeStatus.setText(isDark ? "On" : "Off");
            themeStatus.setTextColor(isDark ? 0xFF7C3AED : 0xFF888888);
        }
    }
}
