package com.example.animor.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.animor.R;
import com.example.animor.Utils.FavoritesAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

//sin terminar
public class FavActivity extends AppCompatActivity {

    private RecyclerView recyclerViewFavorites;
    private FavoritesAdapter favoritesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav);

        // Inicializar RecyclerView
        recyclerViewFavorites = findViewById(R.id.recyclerViewFavorites);
        recyclerViewFavorites.setLayoutManager(new LinearLayoutManager(this));


       // recyclerViewFavorites.setAdapter(favoritesAdapter);

        // Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_favs);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_inicio) {
                startActivity(new Intent(FavActivity.this, InicioActivity.class));
                return true;
            } else if (id == R.id.nav_favs) {
                return true;
            } else if (id == R.id.nav_listing) {
                startActivity(new Intent(FavActivity.this, CreateActivity.class));
                return true;
            } else if (id == R.id.nav_user) {
                startActivity(new Intent(FavActivity.this, UserActivity.class));
                return true;
            } else if (id == R.id.nav_animals) {
                startActivity(new Intent(FavActivity.this, ShowActivity.class));
                return true;
            }
            return false;
        });
    }
}
