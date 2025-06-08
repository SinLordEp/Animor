package com.example.animor.UI;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.animor.App.MyApplication;
import com.example.animor.Model.entity.AnimalListing;
import com.example.animor.R;
import com.example.animor.Utils.AnimalAdapter;
import com.example.animor.Utils.ApiRequests;
import com.example.animor.Utils.Geolocalization;
import com.example.animor.Utils.ListingAdapter;
import com.example.animor.Utils.NavigationHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class InicioActivity extends AppCompatActivity implements
        ListingAdapter.OnListingInteractionListener,
        Geolocalization.LocationCallback {

    private static final String TAG = "InicioActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    private RecyclerView recyclerView;
    private ListingAdapter adapter;
    private List<AnimalListing> lista;
    AnimalAdapter.OnAnimalClickListener listener;
    private NavigationHelper navigationHelper;
    private ImageButton btnFavorite;

    // Variables para geolocalización
    private Geolocalization geolocalization;
    private double longitude = 0.0;
    private double latitude = 0.0;
    private boolean locationObtained = false;

    ApiRequests api = new ApiRequests();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inicio);

        Log.d(TAG, "onCreate started");

        initViews();
        initializeGeolocation();

        // Inicializar lista vacía y adapter
        lista = new ArrayList<>();
        adapter = new ListingAdapter(lista, this, ListingAdapter.ViewType.INICIO_ACTIVITY);
        recyclerView.setAdapter(adapter);

        // Solicitar ubicación para cargar los listings
        requestLocationAndLoadListings();
    }

    private void initViews() {
        // Configurar RecyclerView
        recyclerView = findViewById(R.id.recyclerViewAnimals);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Configurar navegación inferior
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_inicio);
        navigationHelper = NavigationHelper.create(this, NavigationHelper.ActivityType.HOME);
        navigationHelper.setupBottomNavigation(bottomNavigationView);
    }

    private void initializeGeolocation() {
        geolocalization = new Geolocalization(this, this);
    }

    private void requestLocationAndLoadListings() {
        if (checkLocationPermissions()) {
            Log.d(TAG, "Solicitando ubicación para cargar listings...");
            Toast.makeText(this, "Obteniendo ubicación para mostrar animales cercanos...", Toast.LENGTH_SHORT).show();
            geolocalization.requestLocation();
        } else {
            requestLocationPermissions();
        }
    }

    private boolean checkLocationPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                },
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permisos de ubicación concedidos");
                requestLocationAndLoadListings();
            } else {
                Log.d(TAG, "Permisos de ubicación denegados");
                Toast.makeText(this, "Sin permisos de ubicación. Mostrando todos los animales.", Toast.LENGTH_LONG).show();
                // Cargar listings sin filtro de ubicación
                obtenerListings();
            }
        }
    }

    // Implementación de LocationCallback
    @Override
    public void onLocationReady(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        locationObtained = true;

        Log.d(TAG, "Ubicación obtenida - Lat: " + latitude + ", Lng: " + longitude);
        Toast.makeText(this, "Ubicación obtenida. Cargando animales cercanos...", Toast.LENGTH_SHORT).show();

        // Ahora que tenemos la ubicación, cargar los listings
        obtenerListings();
    }

    @Override
    public void onLocationError(String error) {
        Log.e(TAG, "Error de geolocalización: " + error);
        Toast.makeText(this, "Error al obtener ubicación: " + error + ". Mostrando todos los animales.", Toast.LENGTH_LONG).show();

        // Si hay error, cargar listings sin filtro de ubicación
        obtenerListings();
    }

    private List<AnimalListing> obtenerListings() {
        // Ejecutar la llamada a la API en segundo plano
        new Thread(() -> {
            try {
                if (locationObtained && latitude != 0.0 && longitude != 0.0) {
                    Log.d(TAG, "Llamando a API con coordenadas: " + latitude + ", " + longitude);
                    lista=api.getListingNearMe(longitude, latitude, 0);
                } else {
                    Log.d(TAG, "Llamando a API sin filtro de ubicación");
                    // Si no tienes un método para obtener todos los listings,
                    // podrías usar coordenadas por defecto o implementar getAllListings()
                    latitude=40.0;
                    longitude=-3.0;
                    lista=api.getListingNearMe(longitude, latitude, 0);
                }

                // Aquí necesitarías obtener el resultado de la API
                // Esto depende de cómo tu ApiRequests devuelve los datos
                // Por ejemplo, si tienes un callback o un método que devuelve la lista:
                // List<AnimalListing> newListings = api.getLastListings();

                // Actualizar la UI en el hilo principal
                runOnUiThread(() -> {
                    // Temporalmente, hasta que tengas el método para obtener los resultados
                    Toast.makeText(InicioActivity.this, "Cargando animales...", Toast.LENGTH_SHORT).show();

                    // Cuando tengas los datos:
                    // if (newListings != null && !newListings.isEmpty()) {
                    //     lista.clear();
                    //     lista.addAll(newListings);
                    //     adapter.notifyDataSetChanged();
                    //     Log.d(TAG, "Listings actualizados: " + newListings.size() + " animales encontrados");
                    // }
                });

            } catch (Exception e) {
                Log.e(TAG, "Error al obtener listings: " + e.getMessage());
                runOnUiThread(() -> {
                    Toast.makeText(InicioActivity.this,
                            "Error al cargar animales",
                            Toast.LENGTH_SHORT).show();
                });
            }
        }).start();

        return lista;
    }

    // Método para actualizar la lista desde fuera (si es necesario)
    public void actualizarListaAnimales(List<AnimalListing> nuevaLista) {
        if (nuevaLista != null) {
            lista.clear();
            lista.addAll(nuevaLista);
            adapter.notifyDataSetChanged();
            Log.d(TAG, "Lista actualizada con " + nuevaLista.size() + " elementos");
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    @Override
    public void onListingSelected(AnimalListing listing) {
        Intent intent = new Intent(this, ShowMyListingActivity.class);
        intent.putExtra("listing", listing);
        intent.putExtra("mode", "adoptive");
        startActivity(intent);
    }

    @Override
    public void onFavoriteClick(AnimalListing animalListing) {
        MyApplication.executor.execute(() -> {
            try {
                boolean success = api.addFav(animalListing.getListingId());

                runOnUiThread(() -> {
                    if (success) {
                        Toast.makeText(this, "Añadido a favoritos: " +
                                        animalListing.getAnimal().getAnimalName(),
                                Toast.LENGTH_SHORT).show();

                        Log.d(TAG, "Favorito añadido exitosamente: " + animalListing.getListingId());
                    } else {
                        Toast.makeText(this, "Error al añadir a favoritos",
                                Toast.LENGTH_SHORT).show();

                        // Opcional: revertir el icono a corazón vacío si falló
                        // Tendrías que encontrar el ViewHolder y cambiar el icono
                        Log.e(TAG, "Error al añadir favorito para listing: " + animalListing.getListingId());
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Error al añadir favorito: " + e.getMessage());
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error al añadir a favoritos",
                            Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Detener actualizaciones de ubicación para evitar memory leaks
        if (geolocalization != null) {
            geolocalization.stopUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Detener actualizaciones de ubicación cuando la actividad no está visible
        if (geolocalization != null) {
            geolocalization.stopUpdates();
        }
    }
}