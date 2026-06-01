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
    private String currentFilter = "";
    
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
        ArrayList<TodoItem> allNotes = GlobalData.getInstance().getNotes();
        ArrayList<TodoItem> filtered = new ArrayList<>();
        for (TodoItem note : allNotes) {
            if (currentFilter.isEmpty() || note.getTitle().toLowerCase().contains(currentFilter.toLowerCase())) {
                filtered.add(note);
            }
        }
        if (filtered.isEmpty()) {
            recyclerNotes.setVisibility(View.GONE);
            emptyNotes.setVisibility(View.VISIBLE);
        } else {
            recyclerNotes.setVisibility(View.VISIBLE);
            emptyNotes.setVisibility(View.GONE);
            adapter = new NoteAdapter(filtered, (note, pos) -> {
                Intent i = new Intent(getActivity(), NoteEditorActivity.class);
                i.putExtra("note_index", GlobalData.getInstance().getItems().indexOf(note));
                i.putExtra("note_title", note.getTitle());
                i.putExtra("note_content", note.getContent());
                i.putExtra("note_timestamp", note.getTimestamp());
                i.putExtra("note_color", note.getThemeColor());
                startActivity(i);
            });
            recyclerNotes.setAdapter(adapter);
        }
    }
    
    public void filter(String q) { currentFilter = q; loadNotes(); }
    public void refreshData() { loadNotes(); }
}
