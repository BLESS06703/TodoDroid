package com.tododroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class NotesFragment extends Fragment {
    
    private RecyclerView recyclerNotes;
    private LinearLayout emptyNotes;
    private NoteAdapter adapter;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes, container, false);
        
        recyclerNotes = view.findViewById(R.id.recycler_notes);
        emptyNotes = view.findViewById(R.id.empty_notes);
        
        recyclerNotes.setLayoutManager(new LinearLayoutManager(getContext()));
        
        loadNotes();
        
        return view;
    }
    
    private void loadNotes() {
        ArrayList<TodoItem> notes = GlobalData.getInstance().getNotes();
        
        if (notes.isEmpty()) {
            recyclerNotes.setVisibility(View.GONE);
            emptyNotes.setVisibility(View.VISIBLE);
        } else {
            recyclerNotes.setVisibility(View.VISIBLE);
            emptyNotes.setVisibility(View.GONE);
            
            adapter = new NoteAdapter(notes, (note, position) -> {
                // Open note editor
                Intent intent = new Intent(getActivity(), NoteEditorActivity.class);
                intent.putExtra("note_index", position);
                intent.putExtra("note_title", note.getTitle());
                intent.putExtra("note_content", note.getContent());
                intent.putExtra("note_timestamp", note.getTimestamp());
                startActivity(intent);
            });
            recyclerNotes.setAdapter(adapter);
        }
    }
    
    public void refreshData() {
        loadNotes();
    }
}
