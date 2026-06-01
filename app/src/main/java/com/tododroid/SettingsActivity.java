package com.tododroid;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SettingsActivity extends AppCompatActivity {
    
    private boolean isDark = true;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        ((ImageButton) findViewById(R.id.btn_back)).setOnClickListener(v -> finish());
        
        TextView themeStatus = findViewById(R.id.theme_status);
        
        findViewById(R.id.setting_theme).setOnClickListener(v -> {
            isDark = !isDark;
            if (isDark) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                themeStatus.setText("On");
                themeStatus.setTextColor(0xFF7C3AED);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                themeStatus.setText("Off");
                themeStatus.setTextColor(0xFF888888);
            }
        });
    }
}
