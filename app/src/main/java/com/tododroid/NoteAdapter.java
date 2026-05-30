package com.tododroid;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {
    
    private ArrayList<TodoItem> notes;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, hh:mm a", Locale.getDefault());
    private OnNoteClickListener listener;
    
    public interface OnNoteClickListener {
        void onNoteClick(TodoItem note, int position);
    }
    
    public NoteAdapter(ArrayList<TodoItem> notes, OnNoteClickListener listener) {
        this.notes = notes;
        this.listener = listener;
    }
    
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title, preview, time;
        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.note_title);
            preview = itemView.findViewById(R.id.note_preview);
            time = itemView.findViewById(R.id.note_time);
        }
    }
    
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_note, parent, false));
    }
    
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TodoItem note = notes.get(position);
        holder.title.setText(note.getTitle());
        
        // Strip HTML for preview
        String plain = Html.fromHtml(note.getContent(), Html.FROM_HTML_MODE_LEGACY).toString();
        if (plain.length() > 80) plain = plain.substring(0, 80) + "...";
        holder.preview.setText(plain.isEmpty() ? "No additional text" : plain);
        holder.time.setText(dateFormat.format(new Date(note.getTimestamp())));
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onNoteClick(note, position);
        });
    }
    
    @Override
    public int getItemCount() { return notes.size(); }
}
