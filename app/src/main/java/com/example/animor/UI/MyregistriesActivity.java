package com.example.animor.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.animor.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MyregistriesActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_listings);
        // Configurar navegación inferior
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_inicio); // marcar como activo

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_inicio) {
                    return true; // Ya estamos en esta actividad
                } else if (id == R.id.nav_favs) {
                    startActivity(new Intent(MyregistriesActivity.this, FavActivity.class));
                    return true;
                } else if (id == R.id.registrar) {
                    startActivity(new Intent(MyregistriesActivity.this, RegistryActivity.class));
                    return true;
                } else if (id == R.id.nav_user) {
                    startActivity(new Intent(MyregistriesActivity.this, MyregistriesActivity.class));
                    return true;
                } else if (id == R.id.nav_animals) {
                    startActivity(new Intent(MyregistriesActivity.this, MyAnimalsActivity.class));
                    return true;
                }

                return false;
            }
        });
    }
}
