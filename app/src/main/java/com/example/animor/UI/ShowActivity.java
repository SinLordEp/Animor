package com.example.animor.UI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.animor.Model.entity.Animal;
import com.example.animor.Model.entity.AnimalListing;
import com.example.animor.R;
import com.example.animor.UI.ViewsFrames.ShowMyAnimalsFragment;
import com.example.animor.UI.ViewsFrames.ShowMyListingsFragment;
import com.example.animor.Utils.NavigationHelper;
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
    NavigationHelper navigationHelper;
    BottomNavigationView bottomNavigationView;


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
        navigationHelper = new NavigationHelper(this, NavigationHelper.ActivityType.CREATE);
        currentTab = getIntent().getIntExtra("currentTab", 1);

        if (viewPager == null || tabLayout == null) {
            Log.e(TAG, "Error: ViewPager or TabLayout is null");
            return;
        }

        setupViewPager();
        navigationHelper = NavigationHelper.create(this, NavigationHelper.ActivityType.SHOW);

// Configurar bottom navigation
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        navigationHelper.setupBottomNavigation(bottomNavigationView);
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

                // Configurar listeners cuando se carguen los fragments
                viewPager.post(() -> {
                    Fragment currentFragment = getSupportFragmentManager()
                            .findFragmentByTag("f" + position);

                    if (position == 0 && currentFragment instanceof ShowMyAnimalsFragment) {
                        // Configurar listener para animales
                        ((ShowMyAnimalsFragment) currentFragment).setAnimalSelectedListener(ShowActivity.this);
                    } else if (position == 1 && currentFragment instanceof ShowMyListingsFragment) {
                        // Configurar listener para listings
                        ((ShowMyListingsFragment) currentFragment).setListingSelectedListener(ShowActivity.this);
                    }
                });
            }
        });

        // Configurar listeners para fragments iniciales
        viewPager.post(() -> {
            // Fragment de animales (posición 0)
            Fragment animalsFragment = getSupportFragmentManager().findFragmentByTag("f0");
            if (animalsFragment instanceof ShowMyAnimalsFragment) {
                ((ShowMyAnimalsFragment) animalsFragment).setAnimalSelectedListener(this);
            }

            // Fragment de listings (posición 1)
            Fragment listingsFragment = getSupportFragmentManager().findFragmentByTag("f1");
            if (listingsFragment instanceof ShowMyListingsFragment) {
                ((ShowMyListingsFragment) listingsFragment).setListingSelectedListener(this);
            }
        });
    }



    @Override
    public void onAnimalSelected(Animal animal) {
        Intent intent = new Intent(ShowActivity.this, ShowMyAnimalActivity.class);
        Log.d(TAG, "PHOTOURL ANTES DE MANDARSE: "+animal.getPhotoList());
        intent.putExtra("animal", animal);
        intent.putExtra("tags", (Serializable) animal.getTagList());
        intent.putExtra("photos", (Serializable) animal.getPhotoList());
        startActivity(intent);
        finish();
    }


    // Implementación del interface para listings (asumiendo que tienes un modelo Listing)
    @Override
    public void onListingSelected(AnimalListing listing) {
        Intent intent = new Intent(ShowActivity.this, ShowMyListingActivity.class);
        intent.putExtra("listing", listing);
        startActivity(intent);
    }

    private void showListingDetail(AnimalListing listing) {
        Intent intent = new Intent(ShowActivity.this, ShowMyListingActivity.class);
        intent.putExtra("listing", listing);
        startActivity(intent);

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