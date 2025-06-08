package com.example.animor.UI;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.animor.Model.entity.Animal;
import com.example.animor.Model.entity.Photo;
import com.example.animor.R;
import com.example.animor.UI.ViewsFrames.CreateAnimalFragment;
import com.example.animor.Utils.NavigationHelper;
import com.example.animor.Utils.TabsAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class CreateActivity extends AppCompatActivity {
    private static final String TAG = "CreateActivity";
    private ViewPager2 viewPager;
    private static final int IMAGE_PICK_REQUEST = 100;
    private Uri imageUri;
    private int currentMode = 0;
    private NavigationHelper navigationHelper;

    // Eliminar esta línea problemática:
    // Button subirImagenBtn = findViewById(R.id.btnSeleccionarImagen);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate started");
        setContentView(R.layout.activity_base);

        // Usar las variables de instancia, no crear variables locales
        viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        Animal animal = (Animal) getIntent().getSerializableExtra("animal");

        String mode = getIntent().getStringExtra("mode");
        if (mode != null && mode.equals("edit")) {
            CreateAnimalFragment fragment = new CreateAnimalFragment();
            Bundle args = new Bundle();
            args.putSerializable("animal", animal);
            args.putString("mode", mode);
            fragment.setArguments(args);

            // Agregar el fragment
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.detail_container, fragment)
                    .commit();

            FrameLayout frame;
            frame = findViewById(R.id.detail_container);
            frame.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.GONE);
        }else{
            System.out.println("No se ha encontrado mode: "+mode);
        }
        if (viewPager == null || tabLayout == null) {
            Log.e(TAG, "Error: ViewPager or TabLayout is null");
            return;
        }
        navigationHelper = new NavigationHelper(this, NavigationHelper.ActivityType.CREATE);
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
        navigationHelper.setupBottomNavigation(bottomNavigationView);

        Log.d(TAG, "onCreate completed successfully");

    }

    public void goToTab(int index) {
        viewPager.setCurrentItem(index, true);
    }
}