package com.example.animor.Utils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.animor.UI.CreateAnimalFragment;
import com.example.animor.UI.CreateListingFragment;

public class TabsAdapter extends FragmentStateAdapter {
    public TabsAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0)
            return new CreateAnimalFragment();
        else
            return new CreateListingFragment();
    }

    @Override
    public int getItemCount() {
        return 2; // dos pestañas
    }
}