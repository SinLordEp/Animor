package com.example.animor.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.animor.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MyAnimalsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_animals);
        // Configurar navegación inferior
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_animals); // marcar como activo

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_inicio) {
                    startActivity(new Intent(MyAnimalsActivity.this, InicioActivity.class));
                    return true;
                } else if (id == R.id.nav_favs) {
                    startActivity(new Intent(MyAnimalsActivity.this, FavActivity.class));
                    return true;
                } else if (id == R.id.nav_listing) {
                    startActivity(new Intent(MyAnimalsActivity.this, CreateActivity.class));
                    return true;
                } else if (id == R.id.nav_user) {
                    startActivity(new Intent(MyAnimalsActivity.this, ProfileActivity.class));
                    return true;
                } else if (id == R.id.nav_animals) {
                    return true;
                }

                return false;
            }
        });
    }
}

