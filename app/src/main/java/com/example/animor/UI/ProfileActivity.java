package com.example.animor.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.animor.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
    // Bottom Navigation
    BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_favs);

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.nav_inicio) {
                startActivity(new Intent(ProfileActivity.this, InicioActivity.class));
                return true;
            } else if (id == R.id.nav_favs) {
                return true;
            } else if (id == R.id.registrar) {
                startActivity(new Intent(ProfileActivity.this, RegistryActivity.class));
                return true;
            } else if (id == R.id.nav_list) {
                startActivity(new Intent(ProfileActivity.this, MyregistriesActivity.class));
                return true;
            } else if (id == R.id.nav_animals) {
                startActivity(new Intent(ProfileActivity.this, MyAnimalsActivity.class));
                return true;
            }
            return false;
        }
    });
}
}
