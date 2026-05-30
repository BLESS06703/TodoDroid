package com.tododroid;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {
    
    private TasksFragment tasksFragment;
    private NotesFragment notesFragment;
    
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }
    
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            tasksFragment = new TasksFragment();
            return tasksFragment;
        } else {
            notesFragment = new NotesFragment();
            return notesFragment;
        }
    }
    
    public Fragment getFragment(int position) {
        if (position == 0) return tasksFragment;
        return notesFragment;
    }
    
    @Override
    public int getItemCount() {
        return 2;
    }
}
