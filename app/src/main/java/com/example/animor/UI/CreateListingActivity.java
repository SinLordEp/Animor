package com.example.animor.UI;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.animor.App.MyApplication;
import com.example.animor.Model.dto.SpeciesDTO;
import com.example.animor.Model.entity.Animal;
import com.example.animor.Model.entity.AnimalListing;
import com.example.animor.Model.entity.Photo;
import com.example.animor.Model.entity.Tag;
import com.example.animor.Model.request.ListingRequest;
import com.example.animor.Model.request.LocationRequest;
import com.example.animor.R;
import com.example.animor.Utils.ApiRequests;
import com.example.animor.Utils.Geolocalization;
import com.example.animor.Utils.NonScrollListView;
import com.example.animor.Utils.PreferenceUtils;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;


public class CreateListingActivity extends AppCompatActivity implements Geolocalization.LocationCallback{
    // Vistas de información del animal
    private ImageView imgAnimal;
    private TextView tvName;
    private TextView tvCity;
    private TextView tvSex;
    private TextView tvSpecies;
    private TextView tvAnimalBirthdate;
    private TextView tvEstimatedBirthdate;
    private TextView tvAnimalSize;
    private TextView tvAnimalDescription;
    private TextView tvAnimalMicroNumber;
    private TextView tvAnimalNeutered;

    // Campos de contacto/dirección (EditText)
    private EditText editTextPhone;
    private EditText editTextTextEmailAddress;
    private EditText etAddress;
    private EditText etCity;
    private EditText etProvince;
    private EditText etPostalCode;
    private EditText etCountry;

    // Botones y otros controles
    private Button buttonSave;
    private Button btnGetLocation;
    private NonScrollListView listTags;
    private NavigationView navigationView;

    // Geolocalización
    private Geolocalization geolocalization;
    private Geocoder geocoder;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    // Coordenadas actuales
    private double currentLatitude = 0.0;
    private double currentLongitude = 0.0;
    double latitude;
    double longitude;

    //otras variables
    private String lastGeocodedAddress = "";
    boolean isUserTyping = false;

    private static final String TAG = "CreateListingActivity";

    private Animal animal=null;
    private AnimalListing listing=null;
    long listingId =-1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate started");
        setContentView(R.layout.activity_create_one_listing);
        listing = (AnimalListing) getIntent().getSerializableExtra("listing");
        animal = (Animal) getIntent().getSerializableExtra("animal");
        initViews();

        if(listing != null){
            animal=listing.getAnimal();
            listingId=listing.getListingId();
            editListing();
        }
        initializeGeolocation();
        setupListeners();
        if (animal != null) {
            loadAnimalData(animal);
        }

    }

    private void editListing() {
        // Campos del formulario de contacto/dirección
        animal=listing.getAnimal();
        editTextPhone.setText(listing.getContactPhone());
        editTextTextEmailAddress.setText(listing.getContactEmail());
        etAddress.setText(listing.getLocation().getAddress());
        etCity.setText(listing.getLocation().getCity());
        etProvince.setText(listing.getLocation().getProvince());
        etPostalCode.setText(listing.getLocation().getPostalCode());
        etCountry.setText(listing.getLocation().getCountry());
    }

    private void initViews() {
        // Inicialización de vistas de información del animal
        imgAnimal = findViewById(R.id.imgUser);
        tvName = findViewById(R.id.txtName);
        tvCity = findViewById(R.id.txtCity);
        tvSex = findViewById(R.id.txtSex);
        tvSpecies = findViewById(R.id.tvSpecies);
        tvAnimalBirthdate = findViewById(R.id.textViewAnimalBirthdate);
        tvEstimatedBirthdate = findViewById(R.id.textViewEstimatedBirthdate);
        tvAnimalSize = findViewById(R.id.textViewAnimalSize);
        tvAnimalDescription = findViewById(R.id.textViewAnimalDescription);
        tvAnimalMicroNumber = findViewById(R.id.textViewAnimalMicroNumber);
        tvAnimalNeutered = findViewById(R.id.textViewAnimalNeutered);
        listTags = findViewById(R.id.listTags);

        // Campos del formulario de contacto/dirección
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextTextEmailAddress = findViewById(R.id.editTextTextEmailAddress);
        etAddress = findViewById(R.id.etAddress);
        etCity = findViewById(R.id.etCiudad);
        etProvince = findViewById(R.id.etProvincia);
        etPostalCode = findViewById(R.id.etPostalCode);
        etCountry = findViewById(R.id.etCountry);

        // Botones
        buttonSave = findViewById(R.id.buttonSave);
        btnGetLocation = findViewById(R.id.btnGetLocation);
    }
    private void setupListeners() {
        btnGetLocation.setOnClickListener(v -> requestCurrentLocation());
        buttonSave.setOnClickListener(v -> {
            saveListing();
            MyApplication.executor.execute(()->{
                Intent intent =new Intent(CreateListingActivity.this, ShowActivity.class);
                Log.d(TAG, "Animal en listing: "+animal.toString());
                intent.putExtra("animal", animal);
                intent.putExtra("listing", listing);
                startActivity(intent);
            });
        });
    // listeners para geocodificar cuando el usuario termine de escribir
        setupAddressChangeListeners();
    }
    // validar el formulario
    public boolean validateForm() {
        boolean isValid = true;

        if (etCity.getText().toString().trim().isEmpty()) {
            etCity.setError("Ciudad requerida");
            isValid = false;
        }

        // Agrega validaciones para otros campos requeridos
        if (editTextPhone.getText().toString().trim().isEmpty()) {
            editTextPhone.setError("Teléfono requerido");
            isValid = false;
        }
        // Validar que se tengan coordenadas
        if (latitude == 0.0 && longitude == 0.0) {
            Toast.makeText(this, "Use el botón de ubicación o complete la dirección.", Toast.LENGTH_LONG).show();
            isValid = false;
        }

        return isValid;
    }
    //  guardar el listing
    public void saveListing() {
        if (!validateForm()) {
            Toast.makeText(this, "Por favor, complete todos los campos requeridos", Toast.LENGTH_SHORT).show();
            return;
        }
        LocationRequest location = new LocationRequest();

        location.setAddress(etAddress.getText().toString().trim());
        location.setProvince(etProvince.getText().toString().trim());
        location.setPostalCode(etPostalCode.getText().toString().trim());
        location.setCountry(etCountry.getText().toString().trim());
        location.setCity(etCity.getText().toString().trim());
        Log.d(TAG, "Latitud: "+latitude+"\nlongitud:"+longitude);
        location.setLongitude(longitude);
        location.setLatitude(latitude);
        ListingRequest listingRequest = new ListingRequest();
        listingRequest.setLocation(location);
        listingRequest.setContactEmail(editTextTextEmailAddress.getText().toString().trim());
        listingRequest.setContactPhone(editTextPhone.getText().toString().trim());
        MyApplication.executor.execute(()->{
                ApiRequests api = new ApiRequests();
            if(listingId !=-1){
                    listingRequest.setListingId(listingId);
                    api.editListing(listingRequest, animal.getAnimalId());
                    Log.d(TAG, "ID DEL LISTING EDITADO: "+listingRequest.getListingId());
            }else{
                    api.addListingIntoDatabase(listingRequest, animal.getAnimalId());
            }
        });
        Intent intent= new Intent(CreateListingActivity.this, ShowActivity.class);
        startActivity(intent);
    }

    private void initializeGeolocation() {
        geolocalization = new Geolocalization(this, this);
        geocoder = new Geocoder(this, Locale.getDefault());
    }

    private void requestCurrentLocation() {
        if (checkLocationPermissions()) {
            Log.d(TAG, "Solicitando ubicación actual...");
            Toast.makeText(this, "Obteniendo ubicación...", Toast.LENGTH_SHORT).show();
            geolocalization.requestLocation();
        } else {
            requestLocationPermissions();
        }
    }

    private void requestLocationPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                },
                LOCATION_PERMISSION_REQUEST_CODE);
    }


    private boolean checkLocationPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permisos de ubicación concedidos");
                requestCurrentLocation();
            } else {
                Log.d(TAG, "Permisos de ubicación denegados");
                Toast.makeText(this, "Rellene la dirección manualmente", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Implementación de LocationCallback
    @Override
    public void onLocationReady(android.location.Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
        latitude = currentLatitude;
        longitude = currentLongitude;
        Log.d(TAG, "Ubicación obtenida - Lat: " + currentLatitude + ", Lng: " + currentLongitude);
        reverseGeocode(currentLatitude, currentLongitude);

        Log.d(TAG, "Ubicación obtenida - Lat: " + currentLatitude + ", Lng: " + currentLongitude);

        // Obtener dirección a partir de coordenadas
        reverseGeocode(currentLatitude, currentLongitude);
    }

    @Override
    public void onLocationError(String error) {
        Log.e(TAG, "Error de geolocalización: " + error);
        Toast.makeText(this, "Error al obtener ubicación: " + error, Toast.LENGTH_LONG).show();
    }

    private void reverseGeocode(double latitude, double longitude) {
        new Thread(() -> {
            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);

                    // Actualizar UI en el hilo principal
                    this.runOnUiThread(() -> {
                        updateAddressFields(address);
                        Toast.makeText(this, "Dirección obtenida automáticamente", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    this.runOnUiThread(() -> {
                        Toast.makeText(this, "No se pudo obtener la dirección", Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (IOException e) {
                Log.e(TAG, "Error en geocodificación inversa", e);
                this.runOnUiThread(() -> {
                    Toast.makeText(this, "Error al obtener dirección", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void updateAddressFields(Address address) {
        String streetName = address.getThoroughfare();
        if (streetName != null) {
            etAddress.setText(streetName);
        }

        // Ciudad
        String city = address.getLocality();
        if (city != null) {
            etCity.setText(city);
        }

        // Provincia/Estado
        String province = address.getAdminArea();
        if (province != null) {
            etProvince.setText(province);
        }

        // Código postal
        String postalCode = address.getPostalCode();
        if (postalCode != null) {
            etPostalCode.setText(postalCode);
        }

        // País
        String country = address.getCountryName();
        if (country != null) {
            etCountry.setText(country);
        }

        Log.d(TAG, "Campos de dirección actualizados automáticamente");
    }

    private void geocodeFromAddress() {
        String address = etAddress.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String province = etProvince.getText().toString().trim();
        String country = etCountry.getText().toString().trim();

        if (address.isEmpty() && city.isEmpty()) {
            Toast.makeText(this, "Ingresa al menos una dirección y ciudad", Toast.LENGTH_SHORT).show();
            return;
        }

        // Construir dirección completa
        StringBuilder fullAddress = new StringBuilder();
        if (!address.isEmpty()) fullAddress.append(address).append(", ");
        if (!city.isEmpty()) fullAddress.append(city).append(", ");
        if (!province.isEmpty()) fullAddress.append(province).append(", ");
        if (!country.isEmpty()) fullAddress.append(country);
        String addressToGeocode = fullAddress.toString();

        if (addressToGeocode.equals(lastGeocodedAddress)) {
            return;
        }
        lastGeocodedAddress = addressToGeocode;
        Log.d(TAG, "Geocodificando dirección: " + addressToGeocode);


        MyApplication.executor.execute(()->{
            try {
                List<Address> addresses = geocoder.getFromLocationName(addressToGeocode, 1);

                if (addresses != null && !addresses.isEmpty()) {
                    Address foundAddress = addresses.get(0);
                    currentLatitude = foundAddress.getLatitude();
                    currentLongitude = foundAddress.getLongitude();

                    this.runOnUiThread(() -> {
                        latitude = currentLatitude;
                        longitude = currentLongitude;
                        Toast.makeText(this, "Coordenadas obtenidas de la dirección", Toast.LENGTH_SHORT).show();

                        Log.d(TAG, "Coordenadas obtenidas - Lat: " + currentLatitude + ", Lng: " + currentLongitude);
                    });
                } else {
                    this.runOnUiThread(() -> {
                        latitude = 0.0;
                        longitude = 0.0;
                        Toast.makeText(this, "No se encontraron coordenadas para esta dirección", Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (IOException e) {
                Log.e(TAG, "Error en geocodificación", e);
                latitude = 0.0;
                longitude = 0.0;
                this.runOnUiThread(() -> {
                    Toast.makeText(this, "Error al buscar coordenadas", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    private void setupAddressChangeListeners() {
        // TextWatcher para detectar cambios en los campos de dirección
        android.text.TextWatcher addressWatcher = new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                isUserTyping = true;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                if (geocodeHandler != null) {
                    geocodeHandler.removeCallbacks(geocodeRunnable);
                }
                geocodeHandler.postDelayed(() -> {
                    isUserTyping = false;
                    geocodeFromAddress();
                }, 2000); // Esperar 2 segundos después de que el usuario deje de escribir
            }
        };

        etAddress.addTextChangedListener(addressWatcher);
        etCity.addTextChangedListener(addressWatcher);
        etProvince.addTextChangedListener(addressWatcher);
        etCountry.addTextChangedListener(addressWatcher);
        }

    // Agregar estas variables de clase
    private android.os.Handler geocodeHandler = new android.os.Handler();
    private Runnable geocodeRunnable = this::geocodeFromAddress;

    // limpiar el formulario
    public void clearForm() {
        // Limpiar campos de información del animal
        tvName.setText("");
        tvCity.setText("");
        tvSex.setText("");
        tvSpecies.setText("");
        tvAnimalBirthdate.setText("");
        tvAnimalSize.setText("");
        tvAnimalDescription.setText("");
        tvAnimalMicroNumber.setText("");
        tvAnimalNeutered.setText("");
        imgAnimal.setImageResource(R.drawable.gatoinicio); // Imagen por defecto

        // Limpiar campos de formulario
        editTextPhone.setText("");
        editTextTextEmailAddress.setText("");
        etAddress.setText("");
        etCity.setText("");
        etProvince.setText("");
        etPostalCode.setText("");
        etCountry.setText("");
    }

    // cargar datos del animal
    public void loadAnimalData(Animal animal) {
        // Nombre especie
        List<SpeciesDTO> species = PreferenceUtils.getSpeciesList();
        String speciesName = "";
        for (SpeciesDTO s : species) {
            if (s.getSpeciesId() == animal.getSpeciesId()) {
                speciesName = s.getSpeciesName();
                break;
            }
        }

        // Foto de portada
        String photoUrl = "";
        for (Photo photo : animal.getAnimalPhotoList()) {
            //if (photo.getIsCoverPhoto()) {
                photoUrl = photo.getPhotoUrl();
                break;
           // }
        }

        Picasso.get()
                .load(photoUrl)
                .placeholder(R.drawable.gatoinicio)
                .error(R.drawable.gatoinicio)
                .fit()
                .into(imgAnimal);

        // Datos del animal
        tvName.setText(animal.getAnimalName());
        switch(animal.getSex()){
            case Male:
                tvSex.setText("Macho");
                break;
            case Female:
                tvSex.setText("Hembra");
                break;
            case Unknown:
                tvSex.setText("Desconocido");
                break;
        }
        tvSpecies.setText(speciesName);

        DateTimeFormatter formatoSalida = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        tvAnimalBirthdate.setText(animal.getBirthDate().format(formatoSalida));

        if (animal.getIsBirthDateEstimated()) {
            tvEstimatedBirthdate.setVisibility(View.VISIBLE);
        } else {
            tvEstimatedBirthdate.setVisibility(View.GONE);
        }

        tvAnimalSize.setText(animal.getSize());
        tvAnimalDescription.setText(animal.getAnimalDescription());

        if (animal.getMicrochipNumber() != null) {
            tvAnimalMicroNumber.setText(animal.getMicrochipNumber());
        }

        if (animal.getSpeciesId() == 1 || animal.getSpeciesId() == 2) {
            tvAnimalNeutered.setText(animal.getIsNeutered() ? "sí" : "no");
        }else{
            TextView textViewNeutered = findViewById(R.id.textViewNeutered);
            textViewNeutered.setVisibility(View.INVISIBLE);
            tvAnimalNeutered.setVisibility(View.INVISIBLE);
        }

        // Cargar etiquetas en segundo plano, no hay prisa
        new Thread(() -> {
            List<Tag> animalTags = animal.getTagList();
            ArrayAdapter<Tag> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_1,
                    animalTags
            );
            this.runOnUiThread(() -> listTags.setAdapter(adapter));
        }).start();
    }
}