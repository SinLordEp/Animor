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

public class ShowActivity extends AppCompatActivity {
    private static final String TAG = "ShowActivity";
    private TabLayout tabLayout;
    private ViewPager2 viewPager;

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

        TabsAdapter tabsAdapter = new TabsAdapter(this);
        viewPager.setAdapter(tabsAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Ver mis animales");
            } else {
                tab.setText("Ver mis registros");
            }
        }).attach();

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
            } else return id == R.id.nav_animals;
        });

        Log.d(TAG, "onCreate completed successfully");
    }

}
