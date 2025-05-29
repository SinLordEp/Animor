package com.example.animor.Utils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.animor.UI.CreateActivity;
import com.example.animor.UI.ShowActivity;
import com.example.animor.UI.fragments.CreateAnimalFragment;
import com.example.animor.UI.fragments.CreateListingFragment;
import com.example.animor.UI.fragments.ShowAnimalFragment;
import com.example.animor.UI.fragments.ShowListingFragment;

public class TabsAdapter extends FragmentStateAdapter {

    private final FragmentActivity fragmentActivity; // 👈 guardamos la referencia

    public TabsAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity); // Esto se mantiene como está
        this.fragmentActivity = fragmentActivity; // guardamos para usar después
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (fragmentActivity instanceof CreateActivity) {
            if (position == 0)
                return new CreateAnimalFragment();
            else
                return new CreateListingFragment();
        } else if (fragmentActivity instanceof ShowActivity) {
            if (position == 0)
                return new ShowAnimalFragment();
            else
                return new ShowListingFragment();
        } else {
            return new Fragment(); // fallback si no coincide nada
        }
    }

    @Override
    public int getItemCount() {
        if (fragmentActivity instanceof CreateActivity || fragmentActivity instanceof ShowActivity) {
            return 2;
        } else {
            return 1;
        }
    }
}
