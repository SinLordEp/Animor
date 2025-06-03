package com.example.animor.Utils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.animor.UI.CreateActivity;
import com.example.animor.UI.ShowActivity;
import com.example.animor.UI.fragments.CreateAnimalFragment;
import com.example.animor.UI.fragments.CreateListingFragment;
import com.example.animor.UI.fragments.ShowMyAnimalsFragment;
import com.example.animor.UI.fragments.ShowMyListingsFragment;
import android.util.SparseArray;


public class TabsAdapter extends FragmentStateAdapter {
    private SparseArray<Fragment> fragmentSparseArray = new SparseArray<>();

    private final FragmentActivity fragmentActivity; // 👈 guardamos la referencia

    public TabsAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity); // Esto se mantiene como está
        this.fragmentActivity = fragmentActivity; // guardamos para usar después
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        if (fragmentActivity instanceof CreateActivity) {
            if (position == 0)
                fragment = new CreateAnimalFragment();
            else
                fragment = new CreateListingFragment();
        } else if (fragmentActivity instanceof ShowActivity) {
            if (position == 0)
                fragment = new ShowMyAnimalsFragment();
            else
                fragment = new ShowMyListingsFragment();
        } else {
            fragment = new ShowMyAnimalsFragment(); // fallback si no coincide nada
        }
        fragmentSparseArray.put(position, fragment);
        return fragment;
    }
    // Método para obtener el fragment actual
    public Fragment getCurrentFragment(int position) {
        return fragmentSparseArray.get(position);
    }
    public void clearFragments() {
        fragmentSparseArray.clear();
    }
    @Override
    public int getItemCount() {
            return 2;
    }
}
