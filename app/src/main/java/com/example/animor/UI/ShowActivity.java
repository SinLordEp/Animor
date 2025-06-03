package com.example.animor.UI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.animor.Model.Animal;
import com.example.animor.R;
import com.example.animor.UI.fragments.ShowMyAnimalFragment;
import com.example.animor.UI.fragments.ShowMyAnimalsFragment;
import com.example.animor.UI.fragments.ShowMyListingFragment;
import com.example.animor.UI.fragments.ShowMyListingsFragment;
import com.example.animor.Utils.TabsAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

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

    // Implementación del interface para animales
    @Override
    public void onAnimalSelected(Animal animal) {
        showAnimalDetail(animal);
    }

    // Implementación del interface para listings (asumiendo que tienes un modelo Listing)
    @Override
    public void onListingSelected(Object listing) { // Cambia Object por tu clase Listing
        showListingDetail(listing);
    }

    private void showAnimalDetail(Animal animal) {
        isShowingDetails = true;

        // Ocultar tabs mientras mostramos detalles
        tabLayout.setVisibility(View.GONE);

        // Crear fragment de detalle
        ShowMyAnimalFragment detailFragment = new ShowMyAnimalFragment();
        Bundle args = new Bundle();
        args.putSerializable("animal", animal);
        detailFragment.setArguments(args);

        // Reemplazar el contenido del ViewPager
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.detail_container, detailFragment)
                .addToBackStack("animal_detail")
                .commit();

        // Hacer visible el contenedor de detalles
        findViewById(R.id.detail_container).setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.GONE);
    }

    private void showListingDetail(Object listing) {
        isShowingDetails = true;

        // Ocultar tabs mientras mostramos detalles
        tabLayout.setVisibility(View.GONE);

        // Crear fragment de detalle
        ShowMyListingFragment detailFragment = new ShowMyListingFragment();
        Bundle args = new Bundle();
        args.putSerializable("listing", (java.io.Serializable) listing);
        detailFragment.setArguments(args);

        // Reemplazar el contenido del ViewPager
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.detail_container, detailFragment)
                .addToBackStack("listing_detail")
                .commit();

        // Hacer visible el contenedor de detalles
        findViewById(R.id.detail_container).setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.GONE);
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

}