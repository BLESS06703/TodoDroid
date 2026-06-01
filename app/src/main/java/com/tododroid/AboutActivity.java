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
            openLink("https://www.facebook.com/share/1GUjS85j2e/"));
        
        findViewById(R.id.link_ig).setOnClickListener(v -> 
            openLink("https://instagram.com/bless.kaundah0"));
        
        findViewById(R.id.link_email).setOnClickListener(v -> {
            Intent email = new Intent(Intent.ACTION_SENDTO);
            email.setData(Uri.parse("mailto:blesskaunda056@gmail.com"));
            email.putExtra(Intent.EXTRA_SUBJECT, "TodoDroid Feedback");
            try { startActivity(email); }
            catch (Exception e) { Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show(); }
        });
        
        findViewById(R.id.btn_feedback).setOnClickListener(v -> {
            Intent email = new Intent(Intent.ACTION_SENDTO);
            email.setData(Uri.parse("mailto:blesskaunda056@gmail.com"));
            email.putExtra(Intent.EXTRA_SUBJECT, "TodoDroid Feedback");
            email.putExtra(Intent.EXTRA_TEXT, "My feedback about TodoDroid:\n\n");
            try { startActivity(email); }
            catch (Exception e) { Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show(); }
        });
    }
    
    private void openLink(String url) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception e) {
            Toast.makeText(this, "No browser found", Toast.LENGTH_SHORT).show();
        }
    }
}
