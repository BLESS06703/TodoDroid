package com.tododroid;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        
        ((ImageButton) findViewById(R.id.btn_back_about)).setOnClickListener(v -> finish());
        
        findViewById(R.id.link_github).setOnClickListener(v -> 
            openLink("https://github.com/BLESS06703"));
        
        findViewById(R.id.link_fb).setOnClickListener(v -> 
            openLink("https://facebook.com/bless.kaundah"));
        
        findViewById(R.id.link_ig).setOnClickListener(v -> 
            openLink("https://instagram.com/bless.kaundah0"));
    }
    
    private void openLink(String url) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception e) {
            Toast.makeText(this, "No browser found", Toast.LENGTH_SHORT).show();
        }
    }
}
