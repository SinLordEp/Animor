package com.example.animor.UI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.animor.Model.entity.Animal;
import com.example.animor.Model.entity.Animal;
import com.example.animor.Model.entity.AnimalListing;
import com.example.animor.R;
import com.example.animor.UI.ViewsFrames.ShowMyAnimalFragment;
import com.example.animor.UI.ViewsFrames.ShowMyAnimalsFragment;
import com.example.animor.UI.ViewsFrames.ShowMyListingFragment;
import com.example.animor.UI.ViewsFrames.ShowMyListingsFragment;
import com.example.animor.Utils.TabsAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.Serializable;


public class ShowActivity extends AppCompatActivity
        implements ShowMyAnimalsFragment.OnAnimalSelectedListener,
        ShowMyListingsFragment.OnListingSelectedListener {

    private static final String TAG = "ShowActivity";
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private TabsAdapter tabsAdapter;



    // Estados para manejar la navegación
    private boolean isShowingDetails = false;
    private int currentTab = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate started");
        setContentView(R.layout.activity_base);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);


        if (viewPager == null || tabLayout == null) {
            Log.e(TAG, "Error: ViewPager or TabLayout is null");
            return;
        }

        setupViewPager();
        setupBottomNavigation();

        Log.d(TAG, "onCreate completed successfully");
    }

    private void setupViewPager() {
        tabsAdapter = new TabsAdapter(this);
        viewPager.setAdapter(tabsAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Ver mis animales");
            } else {
                tab.setText("Ver mis registros");
            }
        }).attach();

        // Listener para saber en qué tab estamos
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                currentTab = position;

                // Configurar el listener cuando se cargue el fragment de animales
                if (position == 0) {
                    // Esperar un poco para que el fragment se cargue completamente
                    viewPager.post(() -> {
                        Fragment currentFragment = getSupportFragmentManager()
                                .findFragmentByTag("f" + position);
                        if (currentFragment instanceof ShowMyAnimalsFragment) {
                            ((ShowMyAnimalsFragment) currentFragment).setAnimalSelectedListener(ShowActivity.this);
                        }
                    });
                }
            }
        });

        // También configurar el listener para el fragment inicial (posición 0)
        viewPager.post(() -> {
            Fragment initialFragment = getSupportFragmentManager()
                    .findFragmentByTag("f0");
            if (initialFragment instanceof ShowMyAnimalsFragment) {
                ((ShowMyAnimalsFragment) initialFragment).setAnimalSelectedListener(this);
            }
        });
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        if (bottomNavigationView == null) {
            Log.e(TAG, "Error: BottomNavigationView is null");
            return;
        }

        bottomNavigationView.setSelectedItemId(R.id.nav_animals);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_inicio) {
                startActivity(new Intent(ShowActivity.this, InicioActivity.class));
                return true;
            } else if (id == R.id.nav_favs) {
                startActivity(new Intent(ShowActivity.this, FavActivity.class));
                return true;
            } else if (id == R.id.nav_listing) {
                startActivity(new Intent(ShowActivity.this, CreateActivity.class));
                return true;
            } else if (id == R.id.nav_user) {
                startActivity(new Intent(ShowActivity.this, UserActivity.class));
                return true;
            } else return true;
        });
    }
    private void showDetailFragment(Fragment fragment, String backStackTag, String key, Serializable data) {
        isShowingDetails = true;
        tabLayout.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);

        Bundle args = new Bundle();
        args.putSerializable(key, data);
        fragment.setArguments(args);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.detail_container, fragment)
                .addToBackStack(backStackTag)
                .commit();

        findViewById(R.id.detail_container).setVisibility(View.VISIBLE);
    }


    @Override
    public void onAnimalSelected(Animal animal) {
        showAnimalDetail(animal);

    }


    // Implementación del interface para listings (asumiendo que tienes un modelo Listing)
    @Override
    public void onListingSelected(AnimalListing listing) { // Cambia Object por tu clase Listing
        showListingDetail(listing);
    }

    private void showAnimalDetail(Animal animal) {
        showDetailFragment(new ShowMyAnimalFragment(), "animal_detail", "animal", animal);

    }

    private void showListingDetail(AnimalListing listing) {
        showDetailFragment(new ShowMyListingFragment(), "listing_detail", "listing", (Serializable) listing);

    }

    @Override
    public void onBackPressed() {
        if (isShowingDetails && getSupportFragmentManager().getBackStackEntryCount() > 0) {
            // Volver a mostrar las tabs y el ViewPager
            tabLayout.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.VISIBLE);
            findViewById(R.id.detail_container).setVisibility(View.GONE);

            getSupportFragmentManager().popBackStack();
            isShowingDetails = false;
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}