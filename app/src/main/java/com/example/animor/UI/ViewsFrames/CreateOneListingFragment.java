package com.example.animor.UI.ViewsFrames;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.animor.Model.dto.SpeciesDTO;
import com.example.animor.Model.dto.TagDTO;
import com.example.animor.Model.entity.Animal;
import com.example.animor.Model.entity.AnimalListing;
import com.example.animor.Model.entity.Location;
import com.example.animor.Model.entity.Photo;
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

public class CreateOneListingFragment extends Fragment implements Geolocalization.LocationCallback {
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
    private ImageButton btnMenu;
    private NonScrollListView listTags;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    // Geolocalización
    private Geolocalization geolocalization;
    private Geocoder geocoder;
    private static final String TAG = "CreateOneListingFragment";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    // Coordenadas actuales
    private double currentLatitude = 0.0;
    private double currentLongitude = 0.0;
    double latitude;
    double longitude;
    PreferenceUtils pu;

    public static CreateOneListingFragment newInstance(Animal animal) {
        CreateOneListingFragment fragment = new CreateOneListingFragment();
        Bundle args = new Bundle();
        args.putSerializable("animal", animal);  // o putParcelable si usas Parcelable
        fragment.setArguments(args);
        return fragment;
    }

    private Animal animal;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            animal = (Animal) getArguments().getSerializable("animal");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_create_one_listing, container, false);
        initViews(view);
        initializeGeolocation();
        setupListeners();
        if (animal != null) {
            loadAnimalData(animal);
        }
        return view;
    }

    private void initViews(View view) {
        // Inicialización de vistas de información del animal
        imgAnimal = view.findViewById(R.id.imgUser);
        tvName = view.findViewById(R.id.txtName);
        tvCity = view.findViewById(R.id.txtCity);
        tvSex = view.findViewById(R.id.txtSex);
        tvSpecies = view.findViewById(R.id.tvSpecies);
        tvAnimalBirthdate = view.findViewById(R.id.textViewAnimalBirthdate);
        tvEstimatedBirthdate = view.findViewById(R.id.textViewEstimatedBirthdate);
        tvAnimalSize = view.findViewById(R.id.textViewAnimalSize);
        tvAnimalDescription = view.findViewById(R.id.textViewAnimalDescription);
        tvAnimalMicroNumber = view.findViewById(R.id.textViewAnimalMicroNumber);
        tvAnimalNeutered = view.findViewById(R.id.textViewAnimalNeutered);
        listTags = view.findViewById(R.id.listTags);

        // Campos del formulario de contacto/dirección
        editTextPhone = view.findViewById(R.id.editTextPhone);
        editTextTextEmailAddress = view.findViewById(R.id.editTextTextEmailAddress);
        etAddress = view.findViewById(R.id.etAddress);
        etCity = view.findViewById(R.id.etCiudad);
        etProvince = view.findViewById(R.id.etProvincia);
        etPostalCode = view.findViewById(R.id.etPostalCode);
        etCountry = view.findViewById(R.id.etCountry);

        // Botones
        buttonSave = view.findViewById(R.id.buttonSave);
        btnMenu = view.findViewById(R.id.btn_menu);
        btnGetLocation = view.findViewById(R.id.btnGetLocation);

        // Componentes del drawer
        navigationView = view.findViewById(R.id.navigation_view);
        drawerLayout = view.findViewById(R.id.drawer_layout);

        // Cargar datos del animal si existe
        if (animal != null) {
            loadAnimalData(animal);
        }
    }

    private void setupListeners() {
        btnGetLocation.setOnClickListener(v -> requestCurrentLocation());
        buttonSave.setOnClickListener(v -> saveListing());
        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Puedes agregar más listeners aquí según necesites
    }

    // Método para validar el formulario
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

        return isValid;
    }

    // Método para guardar el listing
    public void saveListing() {
        if (!validateForm()) {
            Toast.makeText(getContext(), "Por favor complete todos los campos requeridos", Toast.LENGTH_SHORT).show();
            return;
        }
        Location location = new Location();

        // Aquí iría la lógica para guardar los datos
        location.setAddress(etAddress.getText().toString().trim());
        location.setProvince(etProvince.getText().toString().trim());
        location.setPostalCode(etPostalCode.getText().toString().trim());
        location.setCountry(etCountry.getText().toString().trim());
        location.setCity(etCity.getText().toString().trim());
        location.setLongitude(longitude);
        location.setLatitude(latitude);
        AnimalListing animalListing = new AnimalListing();
        animalListing.setLocationRequest(location);
        animalListing.setAnimal(animal);
        //animalListing.setUserId();
        animalListing.setContactEmail(editTextTextEmailAddress.getText().toString().trim());
        animalListing.setContactPhone(editTextPhone.getText().toString().trim());
        ApiRequests api = new ApiRequests();
        //api.createListing(animalListing);

        // ... obtener otros valores

        Toast.makeText(getContext(), "Registro guardado correctamente", Toast.LENGTH_SHORT).show();
    }

    private void initializeGeolocation() {
        geolocalization = new Geolocalization(getContext(), this);
        geocoder = new Geocoder(getContext(), Locale.getDefault());
    }

    private void requestCurrentLocation() {
        if (checkLocationPermissions()) {
            Log.d(TAG, "Solicitando ubicación actual...");
            Toast.makeText(getContext(), "Obteniendo ubicación...", Toast.LENGTH_SHORT).show();
            geolocalization.requestLocation();
        } else {
            requestLocationPermissions();
        }
    }

    private void requestLocationPermissions() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                },
                LOCATION_PERMISSION_REQUEST_CODE);
    }


    private boolean checkLocationPermissions() {
        return ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
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
                Toast.makeText(getContext(), "Permisos de ubicación necesarios para esta función", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Implementación de LocationCallback
    @Override
    public void onLocationReady(android.location.Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();

        Log.d(TAG, "Ubicación obtenida - Lat: " + currentLatitude + ", Lng: " + currentLongitude);

        // Obtener dirección a partir de coordenadas
        reverseGeocode(currentLatitude, currentLongitude);
    }

    @Override
    public void onLocationError(String error) {
        Log.e(TAG, "Error de geolocalización: " + error);
        Toast.makeText(getContext(), "Error al obtener ubicación: " + error, Toast.LENGTH_LONG).show();
    }

    private void reverseGeocode(double latitude, double longitude) {
        new Thread(() -> {
            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);

                    // Actualizar UI en el hilo principal
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            updateAddressFields(address);
                            Toast.makeText(getContext(), "Dirección obtenida automáticamente", Toast.LENGTH_SHORT).show();
                        });
                    }
                } else {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "No se pudo obtener la dirección", Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "Error en geocodificación inversa", e);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Error al obtener dirección", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        }).start();
    }

    private void updateAddressFields(Address address) {
        // Dirección (calle y número)
        String addressLine = address.getAddressLine(0);
        if (addressLine != null) {
            etAddress.setText(addressLine);
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
            Toast.makeText(getContext(), "Ingresa al menos una dirección o ciudad", Toast.LENGTH_SHORT).show();
            return;
        }

        // Construir dirección completa
        StringBuilder fullAddress = new StringBuilder();
        if (!address.isEmpty()) fullAddress.append(address).append(", ");
        if (!city.isEmpty()) fullAddress.append(city).append(", ");
        if (!province.isEmpty()) fullAddress.append(province).append(", ");
        if (!country.isEmpty()) fullAddress.append(country);
        String addressToGeocode = fullAddress.toString();
        Log.d(TAG, "Geocodificando dirección: " + addressToGeocode);

        new Thread(() -> {
            try {
                List<Address> addresses = geocoder.getFromLocationName(addressToGeocode, 1);

                if (addresses != null && !addresses.isEmpty()) {
                    Address foundAddress = addresses.get(0);
                    currentLatitude = foundAddress.getLatitude();
                    currentLongitude = foundAddress.getLongitude();

                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            latitude = currentLatitude;
                            longitude =currentLongitude;
                            Toast.makeText(getContext(), "Coordenadas obtenidas de la dirección", Toast.LENGTH_SHORT).show();

                            Log.d(TAG, "Coordenadas obtenidas - Lat: " + currentLatitude + ", Lng: " + currentLongitude);
                        });
                    }
                } else {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "No se encontraron coordenadas para esta dirección", Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "Error en geocodificación", e);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Error al buscar coordenadas", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        }).start();
    }

    // Método para limpiar el formulario
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

    // Método para cargar datos del animal
    public void loadAnimalData(Animal animal) {
        // Nombre especie
        List<SpeciesDTO> speciesDTOS = PreferenceUtils.getSpeciesList();
        String speciesName = "";
        for (SpeciesDTO s : speciesDTOS) {
            if (s.getSpeciesId() == animal.getAnimalId()) {
                speciesName = s.getSpeciesName();
                break;
            }
        }

        // Foto de portada
        String photoUrl = "";
        for (Photo photo : animal.getAnimalPhotoList()) {
            if (photo.getIsCoverPhoto()) {
                photoUrl = photo.getPhotoUrl();
                break;
            }
        }

        Picasso.get()
                .load(photoUrl)
                .placeholder(R.drawable.gatoinicio)
                .error(R.drawable.gatoinicio)
                .into(imgAnimal);

        // Datos del animal
        tvName.setText(animal.getAnimalName());
        tvSex.setText(animal.getSex().toString());
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

        if (animal.getAnimalId() == 1 || animal.getAnimalId() == 2) {
            tvAnimalNeutered.setText(animal.getIsNeutered() ? "sí" : "no");
        }

        // Cargar etiquetas en segundo plano
        new Thread(() -> {
            List<TagDTO> animalTags = animal.getTagList();
            ArrayAdapter<TagDTO> adapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    animalTags
            );
            requireActivity().runOnUiThread(() -> listTags.setAdapter(adapter));
        }).start();
    }
}



