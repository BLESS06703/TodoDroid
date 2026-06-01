package com.tododroid;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class CloudBackup {
    
    public static void backupToDrive(Context context) {
        try {
            File file = new File(context.getFilesDir(), "tododroid_data.json");
            if (!file.exists()) {
                Toast.makeText(context, "No data to backup", Toast.LENGTH_SHORT).show();
                return;
            }
            
            Uri uri = FileProvider.getUriForFile(context, 
                context.getPackageName() + ".fileprovider", file);
            
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("application/json");
            share.putExtra(Intent.EXTRA_STREAM, uri);
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            share.putExtra(Intent.EXTRA_TEXT, "TodoDroid Backup - Save this file to restore your data");
            
            context.startActivity(Intent.createChooser(share, "Backup to Drive"));
        } catch (Exception e) {
            Toast.makeText(context, "Backup failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    public static void restoreFromFile(Context context, Uri uri) {
        try {
            File dest = new File(context.getFilesDir(), "tododroid_data.json");
            FileInputStream in = (FileInputStream) context.getContentResolver().openInputStream(uri);
            FileOutputStream out = new FileOutputStream(dest);
            
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            out.close();
            in.close();
            
            GlobalData.getInstance().loadFromFile(context);
            Toast.makeText(context, "Data restored successfully!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, "Restore failed", Toast.LENGTH_SHORT).show();
        }
    }
}
