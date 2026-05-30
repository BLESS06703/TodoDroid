package com.tododroid;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;

public class NotesFragment extends Fragment {
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes, container, false);
        updateNoteCount(view);
        return view;
    }
    
    public void refreshData() {
        if (getView() != null) {
            updateNoteCount(getView());
        }
    }
    
    private void updateNoteCount(View view) {
        ArrayList<TodoItem> notes = GlobalData.getInstance().getNotes();
        TextView countText = view.findViewById(R.id.note_count);
        if (countText != null) {
            countText.setText(notes.size() + " notes");
        }
    }
}
