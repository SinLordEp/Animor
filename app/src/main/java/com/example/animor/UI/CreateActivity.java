package com.example.animor.UI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.animor.R;
import com.example.animor.Utils.TabsAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class CreateActivity extends AppCompatActivity {
    private static final String TAG = "CreateActivity";
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private TabsAdapter tabsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate started");
        setContentView(R.layout.fragment_create);

        // Usar las variables de instancia, no crear variables locales
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        if (viewPager == null || tabLayout == null) {
            Log.e(TAG, "Error: ViewPager or TabLayout is null");
            return;
        }

        tabsAdapter = new TabsAdapter(this);
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
                startActivity(new Intent(CreateActivity.this, ProfileActivity.class));
                return true;
            } else if (id == R.id.nav_animals) {
                startActivity(new Intent(CreateActivity.this, MyAnimalsActivity.class));
                return true;
            }
            return false;
        });

        Log.d(TAG, "onCreate completed successfully");
    }
}