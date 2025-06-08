package com.example.animor.UI;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.animor.App.MyApplication;
import com.example.animor.Model.dto.SpeciesDTO;
import com.example.animor.Model.entity.AnimalListing;
import com.example.animor.R;
import com.example.animor.Utils.AnimalAdapter;
import com.example.animor.Utils.ApiRequests;
import com.example.animor.Utils.Geolocalization;
import com.example.animor.Utils.ListingAdapter;
import com.example.animor.Utils.NavigationHelper;
import com.example.animor.Utils.PreferenceUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class InicioActivity extends AppCompatActivity implements
        ListingAdapter.OnListingInteractionListener,
        Geolocalization.LocationCallback {

    private static final String TAG = "InicioActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    private RecyclerView rvListings;
    private ListingAdapter adapter;
    private List<AnimalListing> lista;
    AnimalAdapter.OnAnimalClickListener listener;

    // Variables para geolocalización
    private Geolocalization geolocalization;
    private double longitude = 0.0;
    private double latitude = 0.0;
    private boolean locationObtained = false;

    private EditText searchEditText;
    private Button btnLupa;
    private Button btnFiltros;

    // Variables para almacenar filtros actuales
    private String currentCity = null;
    private String currentCountry = null;
    private Integer currentSpeciesId = null;
    private String currentSearchText = null;
    private boolean isUsingLocationFilter = true; // Para saber si usar getNearMe o getListing
    ApiRequests api = new ApiRequests();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inicio);

        Log.d(TAG, "onCreate started");

        initViews();
        initializeGeolocation();

        lista = new ArrayList<>();
        adapter = new ListingAdapter(lista, this, ListingAdapter.ViewType.INICIO_ACTIVITY);
        rvListings.setLayoutManager(new LinearLayoutManager(this));
        rvListings.setAdapter(adapter);

        requestLocation();
    }

    private void initViews() {
        // Configurar RecyclerView
        rvListings = findViewById(R.id.recyclerViewAnimals);
        rvListings.setLayoutManager(new LinearLayoutManager(this));
        // Configurar elementos de búsqueda
        searchEditText = findViewById(R.id.searchEditText);
        btnLupa = findViewById(R.id.btnLupa);
        btnFiltros = findViewById(R.id.btnFiltros);

        // Configurar listeners
        setupSearchListeners();

        // Configurar navegación inferior
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_inicio);
        NavigationHelper navigationHelper = NavigationHelper.create(this, NavigationHelper.ActivityType.HOME);
        navigationHelper.setupBottomNavigation(bottomNavigationView);
    }
    private void setupSearchListeners() {
        // Listener para el botón de búsqueda
        btnLupa.setOnClickListener(v -> {
            currentSearchText = searchEditText.getText().toString().trim();
            if (!currentSearchText.isEmpty()) {
                isUsingLocationFilter = false;
                loadNearMeListings(); // Esto ahora cargará con el filtro de búsqueda
                rvListings.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(this, "Ingresa un término de búsqueda", Toast.LENGTH_SHORT).show();
            }
        });

        btnFiltros.setOnClickListener(v -> showFilterDialog());

        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            btnLupa.performClick();
            return true;
        });
    }
    private void showFilterDialog() {
        AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);

        // Inflar el layout del diálogo
        android.view.LayoutInflater inflater = getLayoutInflater();
        android.view.View dialogView = inflater.inflate(R.layout.dialog_filtros, null);

        EditText etCity = dialogView.findViewById(R.id.etCity);
        EditText etCountry = dialogView.findViewById(R.id.etCountry);
        Spinner spinnerSpecies = dialogView.findViewById(R.id.spinnerSpecies);
        MyApplication.executor.execute(() -> {
            List<SpeciesDTO> receivedSpecies = PreferenceUtils.getSpeciesList();
            ArrayAdapter<SpeciesDTO> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                receivedSpecies);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerSpecies.setAdapter(adapter);
            // Obtener especie seleccionada
            int selectedPosition = spinnerSpecies.getSelectedItemPosition();
            currentSpeciesId = getSpeciesIdFromPosition(selectedPosition);
            try {
                runOnUiThread(() -> {
                        // Establecer selección actual si existe
                        if (currentSpeciesId != null) {
                            for (int i = 0; i < receivedSpecies.size(); i++) {
                                if (receivedSpecies.get(i).getSpeciesId() == currentSpeciesId){
                                    spinnerSpecies.setSelection(i);
                                    break;
                                }
                            }
                        }

                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    // Manejar error silenciosamente o mostrar mensaje
                    Log.e("FilterDialog", "Error cargando especies", e);
                });
            }
        });

        // Prellenar con valores actuales
        if (currentCity != null) etCity.setText(currentCity);
        if (currentCountry != null) etCountry.setText(currentCountry);
        String newSpeciesId = spinnerSpecies.toString();

        builder.setView(dialogView)
                .setTitle("Filtros de búsqueda")
                .setPositiveButton("Aplicar", (dialog, id) -> {
                    // Obtener valores del diálogo
                    currentCity = etCity.getText().toString().trim();
                    currentCountry = etCountry.getText().toString().trim();
                    currentSpeciesId  = getSelectedSpeciesId(spinnerSpecies);


                    // Si se aplicaron filtros, no usar ubicación
                    if (!currentCity.isEmpty() || !currentCountry.isEmpty() || currentSpeciesId != null) {
                        isUsingLocationFilter = false;
                    }

                    // Aplicar filtros
                    loadFilteredListings(); // Usar el método corregido

                    // Actualizar UI para mostrar que hay filtros activos
                    updateFilterUI();
                })
                .setNegativeButton("Cancelar", null)
                .setNeutralButton("Limpiar", (dialog, id) -> {
                    // Limpiar todos los filtros
                    currentCity = null;
                    currentCountry = null;
                    currentSpeciesId = null;
                    currentSearchText = null;
                    isUsingLocationFilter = true;

                    // Limpiar campo de búsqueda
                    searchEditText.setText("");

                    // Recargar con ubicación
                    loadNearMeListings();

                    // Actualizar UI
                    updateFilterUI();
                });

        builder.create().show();
    }

    private Integer getSelectedSpeciesId(Spinner spinnerSpecies) {
        int selectedPosition = spinnerSpecies.getSelectedItemPosition();

        if (selectedPosition <= 0) {
            return null;
        }

        ArrayAdapter<SpeciesDTO> adapter = (ArrayAdapter<SpeciesDTO>) spinnerSpecies.getAdapter();
        if (adapter != null && selectedPosition < adapter.getCount()) {
            SpeciesDTO selectedSpecies = adapter.getItem(selectedPosition);
            return selectedSpecies != null ? selectedSpecies.getSpeciesId() : null;
        }

        return null;
    }

    private void updateFilterUI() {
        // Cambiar el texto del botón de filtros si hay filtros activos
        boolean hasActiveFilters = (currentCity != null && !currentCity.isEmpty()) ||
                (currentCountry != null && !currentCountry.isEmpty()) ||
                currentSpeciesId != null ||
                (currentSearchText != null && !currentSearchText.isEmpty());

        if (hasActiveFilters) {
            btnFiltros.setText("Filtros*");
            btnFiltros.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    androidx.core.content.ContextCompat.getColor(this, R.color.primarytwo)
            ));
        } else {
            btnFiltros.setText("Filtros");
            btnFiltros.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    androidx.core.content.ContextCompat.getColor(this, android.R.color.black)
            ));
        }
    }

    private void initializeGeolocation() {
        checkLocationPermissions();
        geolocalization = new Geolocalization(this, this);
    }

    private void requestLocation() {
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
                requestLocation();
            } else {
                Log.d(TAG, "Permisos de ubicación denegados");
                Toast.makeText(this, "Sin permisos de ubicación. Mostrando todos los animales.", Toast.LENGTH_LONG).show();
                loadNearMeListings();
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
        loadNearMeListings();
    }

    @Override
    public void onLocationError(String error) {
        Log.e(TAG, "Error de geolocalización: " + error);
        Toast.makeText(this, "Error al obtener ubicación: " + error + ". Mostrando todos los animales.", Toast.LENGTH_LONG).show();

        // Si hay error, cargar listings sin filtro de ubicación
        loadNearMeListings();
    }
    private void loadFilteredListings() {
        Log.d(TAG, "Llamando a API con filtros - Ciudad: " + currentCity +
                ", País: " + currentCountry + ", Especie: " + currentSpeciesId);

        MyApplication.executor.execute(() -> {
            try {
                // getListing con filtros
                String city = (currentCity != null && !currentCity.isEmpty()) ? currentCity : null;
                String country = (currentCountry != null && !currentCountry.isEmpty()) ? currentCountry : null;

                List<AnimalListing> filteredListings = api.getListing(city, country, currentSpeciesId, 0);

                runOnUiThread(() -> {
                    if (filteredListings != null && !filteredListings.isEmpty()) {
                        // Filtrar por texto de búsqueda si existe
                        List<AnimalListing> finalList = filteredListings;
                        if (currentSearchText != null && !currentSearchText.isEmpty()) {
                            finalList = filterBySearchText(filteredListings, currentSearchText);
                        }

                        // Actualizar la lista
                        actualizarListaAnimales(finalList);

                        // Mostrar RecyclerView
                        rvListings.setVisibility(View.VISIBLE);

                        // Actualizar subtítulo
                        updateSubtitle(finalList.size());

                        Log.d(TAG, "Listings filtrados actualizados: " + finalList.size() + " animales encontrados");
                    } else {
                        // Lista vacía
                        actualizarListaAnimales(new ArrayList<>());
                        rvListings.setVisibility(View.VISIBLE);
                        updateSubtitle(0);
                        Toast.makeText(InicioActivity.this, "No se encontraron animales con estos filtros", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Error al obtener listings filtrados: " + e.getMessage());
                runOnUiThread(() -> {
                    Toast.makeText(InicioActivity.this, "Error al cargar animales", Toast.LENGTH_SHORT).show();
                    updateSubtitle(0);
                });
            }
        });
    }
    private void loadNearMeListings() {
        MyApplication.executor.execute(() -> {
            try {
                List<AnimalListing> loadedListings;

                if (isUsingLocationFilter && locationObtained && latitude != 0.0 && longitude != 0.0) {
                    Log.d(TAG, "Llamando a API con coordenadas: " + latitude + ", " + longitude);
                    loadedListings = api.getListingNearMe(latitude, longitude, 1);
                } else {
                    Log.d(TAG, "Llamando a API sin filtros de ubicación");
                    // Si no hay ubicación o no se está usando filtro de ubicación, cargar todos
                    loadedListings = api.getListing(null, null, null, 0);
                }

                runOnUiThread(() -> {
                    if (loadedListings != null && !loadedListings.isEmpty()) {
                        // Filtrar por texto de búsqueda si existe
                        List<AnimalListing> filteredList = loadedListings;
                        if (currentSearchText != null && !currentSearchText.isEmpty()) {
                            filteredList = filterBySearchText(loadedListings, currentSearchText);
                        }

                        // Actualizar la lista
                        actualizarListaAnimales(filteredList);

                        // Mostrar RecyclerView
                        rvListings.setVisibility(View.VISIBLE);

                        // Actualizar subtítulo
                        updateSubtitle(filteredList.size());

                        Log.d(TAG, "Listings actualizados: " + filteredList.size() + " animales encontrados");
                    } else {
                        // Lista vacía
                        actualizarListaAnimales(new ArrayList<>());
                        rvListings.setVisibility(View.VISIBLE);
                        updateSubtitle(0);
                        Toast.makeText(InicioActivity.this, "No se encontraron animales", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Error al obtener listings: " + e.getMessage());
                runOnUiThread(() -> {
                    Toast.makeText(InicioActivity.this, "Error al cargar animales", Toast.LENGTH_SHORT).show();
                    updateSubtitle(0);
                });
            }
        });
    }

    private List<AnimalListing> filterBySearchText(List<AnimalListing> listings, String searchText) {
        List<AnimalListing> filtered = new ArrayList<>();
        String searchLower = searchText.toLowerCase();

        for (AnimalListing listing : listings) {
            if (listing.getAnimal() != null &&
                    listing.getAnimal().getAnimalName() != null &&
                    listing.getAnimal().getAnimalName().toLowerCase().contains(searchLower)) {
                filtered.add(listing);
            }
        }

        return filtered;
    }

    private void updateSubtitle(int count) {
        TextView textViewSubtitulo = findViewById(R.id.textViewSubtitulo);
        if (count == 0) {
            textViewSubtitulo.setText("No se encontraron animales");
            rvListings.setVisibility(View.GONE);
        } else if (isUsingLocationFilter) {
            textViewSubtitulo.setText("Animales cerca de ti (" + count + ")");
        } else {
            textViewSubtitulo.setText("Resultados encontrados (" + count + ")");
        }
    }


    private Integer getSpeciesIdFromPosition(int position) {
        // Mapear la posición del spinner al ID de la especie
        // Esto depende de cómo tengas organizadas tus especies
        switch (position) {
            case 1: return 1; // Perro
            case 2: return 2; // Gato
            case 3: return 3; // Conejo
            case 4: return 4; // Ave
            case 5: return 5; // Otros
            default: return null;
        }
    }
    // actualizar la lista desde fuera (si es necesario)
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
        Intent intent = new Intent(InicioActivity.this, ShowMyListingActivity.class);
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