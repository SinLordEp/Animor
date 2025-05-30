package com.example.animor.UI;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.animor.R;
import com.example.animor.Utils.TabsAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class CreateActivity extends AppCompatActivity {
    private static final String TAG = "CreateActivity";
    private ViewPager2 viewPager;
    private static final int IMAGE_PICK_REQUEST = 100;
    private Uri imageUri;

    // Eliminar esta línea problemática:
    // Button subirImagenBtn = findViewById(R.id.btnSeleccionarImagen);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate started");
        setContentView(R.layout.fragment_create);

        // Usar las variables de instancia, no crear variables locales
        viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        if (viewPager == null || tabLayout == null) {
            Log.e(TAG, "Error: ViewPager or TabLayout is null");
            return;
        }

        TabsAdapter tabsAdapter = new TabsAdapter(this);
        viewPager.setAdapter(tabsAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Crear Animal");
            } else {
                tab.setText("Registrar para Adopción");
            }
        }).attach();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        if (bottomNavigationView == null) {
            Log.e(TAG, "Error: BottomNavigationView is null");
            return;
        }

        bottomNavigationView.setSelectedItemId(R.id.nav_listing);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_inicio) {
                startActivity(new Intent(CreateActivity.this, InicioActivity.class));
                return true;
            } else if (id == R.id.nav_favs) {
                startActivity(new Intent(CreateActivity.this, FavActivity.class));
                return true;
            } else if (id == R.id.nav_listing) {
                return true;
            } else if (id == R.id.nav_user) {
                startActivity(new Intent(CreateActivity.this, UserActivity.class));
                return true;
            } else if (id == R.id.nav_animals) {
                startActivity(new Intent(CreateActivity.this, ShowActivity.class));
                return true;
            }
            return false;
        });

        Log.d(TAG, "onCreate completed successfully");
    }

    public void goToTab(int index) {
        viewPager.setCurrentItem(index, true);
    }
}