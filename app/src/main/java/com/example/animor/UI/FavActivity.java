package com.example.animor.UI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.animor.App.MyApplication;
import com.example.animor.Model.entity.AnimalListing;
import com.example.animor.R;
import com.example.animor.Utils.ApiRequests;
import com.example.animor.Utils.ListingAdapter;
import com.example.animor.Utils.NavigationHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FavActivity extends AppCompatActivity implements ListingAdapter.OnListingInteractionListener {

    private static final String TAG = "FavActivity";

    private RecyclerView recyclerViewFavorites;
    private ListingAdapter adapter;
    private NavigationHelper navigationHelper;
    private TextView tvEmptyState;
    private List<AnimalListing> favoritesList;
    private ApiRequests api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav);

        initializeViews();
        setupRecyclerView();
        setupBottomNavigation();

        // Inicializar API y executor
        api = new ApiRequests();
        favoritesList = new ArrayList<>();

        // Cargar favoritos
        loadFavorites();
    }

    private void initializeViews() {
        recyclerViewFavorites = findViewById(R.id.recyclerViewFavorites);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        if (tvEmptyState == null) {
            Log.w(TAG, "Empty state TextView not found in layout");
        }
    }

    private void setupRecyclerView() {
        recyclerViewFavorites.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar adapter con ViewType.INICIO_ACTIVITY para mostrar botones de favoritos
        adapter = new ListingAdapter(favoritesList, this, ListingAdapter.ViewType.FAVORITES_ACTIVITY);
        recyclerViewFavorites.setAdapter(adapter);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_favs);
        navigationHelper = NavigationHelper.create(this, NavigationHelper.ActivityType.FAVORITES);
        navigationHelper.setupBottomNavigation(bottomNavigationView);
    }

    private void loadFavorites() {

        MyApplication.executor.execute(() -> {
            try {
                Set<AnimalListing> favoritesSet = api.getMyFavs();

                runOnUiThread(() -> {

                    if (favoritesSet != null && !favoritesSet.isEmpty()) {
                        favoritesList.clear();
                        favoritesList.addAll(favoritesSet);
                        adapter.updateData(favoritesList);
                        showEmptyState(false);

                        Log.d(TAG, "Favoritos cargados: " + favoritesList.size());
                    } else {
                        favoritesList.clear();
                        adapter.updateData(favoritesList);
                        showEmptyState(true);

                        Log.d(TAG, "No hay favoritos para mostrar");
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Error cargando favoritos: ", e);

                runOnUiThread(() -> {
                    showEmptyState(true);
                    Toast.makeText(FavActivity.this,
                            "Error cargando favoritos. Inténtalo de nuevo.",
                            Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void showEmptyState(boolean show) {
        if (tvEmptyState != null) {
            tvEmptyState.setVisibility(show ? View.VISIBLE : View.GONE);
            if (show) {
                tvEmptyState.setText("No tienes favoritos aún.\n¡Explora y agrega algunos!");
            }
        }
        recyclerViewFavorites.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onListingSelected(AnimalListing animalListing) {
        // Navegar a detalle del listing
        Log.d(TAG, "Listing seleccionado: " + animalListing.getAnimal().getAnimalName());

        // Aquí puedes implementar la navegación al detalle
         Intent intent = new Intent(FavActivity.this, ShowMyListingActivity.class);
         intent.putExtra("listing", animalListing);
        intent.putExtra("mode", "adoptive");
        startActivity(intent);
    }

    @Override
    public void onFavoriteClick(AnimalListing animalListing) {
        // En FavActivity, el click en favorito significa eliminar de favoritos
        Log.d(TAG, "Eliminando de favoritos: " + animalListing.getAnimal().getAnimalName());

        removeFavorite(animalListing);
    }

    private void removeFavorite(AnimalListing animalListing) {
        // Mostrar feedback inmediato
        Toast.makeText(this, "Eliminando de favoritos...", Toast.LENGTH_SHORT).show();

        MyApplication.executor.execute(() -> {
            try {
                boolean success = api.deleteFav(animalListing.getListingId());

                runOnUiThread(() -> {
                    if (success) {
                        // Eliminar de la lista local y actualizar adapter
                        int position = favoritesList.indexOf(animalListing);
                        if (position != -1) {
                            favoritesList.remove(position);
                            adapter.removeListing(position);

                            // Mostrar estado vacío si no quedan favoritos
                            if (favoritesList.isEmpty()) {
                                showEmptyState(true);
                            }
                        }

                        Toast.makeText(FavActivity.this,
                                "Eliminado de favoritos",
                                Toast.LENGTH_SHORT).show();

                        Log.d(TAG, "Favorito eliminado exitosamente");
                    } else {
                        Toast.makeText(FavActivity.this,
                                "Error eliminando favorito. Inténtalo de nuevo.",
                                Toast.LENGTH_SHORT).show();

                        Log.e(TAG, "Error eliminando favorito");
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Excepción eliminando favorito: ", e);

                runOnUiThread(() -> {
                    Toast.makeText(FavActivity.this,
                            "Error eliminando favorito. Inténtalo de nuevo.",
                            Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //refrescar favoritos si es necesario (no implementado aún)
    public void refreshFavorites() {
        loadFavorites();
    }
}