package com.example.animor.UI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.app.AppCompatActivity;

import com.example.animor.App.MyApplication;
import com.example.animor.Model.dto.SpeciesDTO;
import com.example.animor.Model.entity.Animal;
import com.example.animor.Model.entity.AnimalListing;
import com.example.animor.Model.entity.Photo;
import com.example.animor.Model.entity.Location;
import com.example.animor.Model.entity.Species;
import com.example.animor.Model.entity.Tag;
import com.example.animor.R;
import com.example.animor.UI.CreateListingActivity;
import com.example.animor.Utils.ApiRequests;
import com.example.animor.Utils.NonScrollListView;
import com.example.animor.Utils.PreferenceUtils;
import com.squareup.picasso.Picasso;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ShowMyListingActivity extends AppCompatActivity {
    // Variables para datos del animal
    private ImageView imgAnimal;
    private TextView txtName, txtSex, textViewSpecies;
    private TextView textViewAnimalBirthdate, textViewEstimatedBirthdate;
    private TextView textViewAnimalSize;
    private TextView textViewAnimalDescription;
    private TextView textViewAnimalMicroNumber;
    private TextView textViewAnimalNeutered;
    private NonScrollListView listTags;

    // Variables para datos del listing
    private TextView tvPhone, tvEmail;
    private TextView tvAddress, tvCity, tvProvince, tvPostalCode, tvCountry;
    private SwitchCompat switchAdoptado;

    // Botones
    private Button btnedit;
    private Button btndel;

    // Variables globales
    private Animal animal = null;
    private AnimalListing animalListing = null;
    private Location location = null;
    private String speciesName = "";
    private String photoUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_show_my_listing); // Cambiar el nombre del layout si es necesario

        // Obtener datos del Intent
        Intent intent = getIntent();
        if (intent != null) {
            animal = (Animal) intent.getSerializableExtra("animal");
            animalListing = (AnimalListing) intent.getSerializableExtra("animalListing");
            location = (Location) intent.getSerializableExtra("location");
        }

        // Verificar que tenemos los datos necesarios
        if (animal == null || animalListing == null) {
            Log.e("ShowMyListingActivity", "Datos del animal o listing faltantes");
            Toast.makeText(this, "Error: Datos incompletos", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Obtener nombre de la especie
        List<SpeciesDTO> species = PreferenceUtils.getSpeciesList();
        for (SpeciesDTO s : species) {
            if (s.getSpeciesId() == animal.getSpeciesId()) {
                speciesName = s.getSpeciesName();
                break;
            }
        }

        // Obtener URL de la foto de portada
        List<Photo> photoList = animal.getAnimalPhotoList();
        if (photoList != null) {
            for (Photo a : photoList) {
                if (a.getIsCoverPhoto()) {
                    photoUrl = a.getPhotoUrl();
                    break;
                }
            }
        }

        initViews();
        setupListeners();
        loadAnimalData();
        loadListingData();
    }

    private void initViews() {
        // Inicializar vistas del animal (tabla tableanimal)
        imgAnimal = findViewById(R.id.imgUser);
        txtName = findViewById(R.id.txtName);
        txtSex = findViewById(R.id.txtSex);
        textViewSpecies = findViewById(R.id.tvSpecies);
        textViewAnimalBirthdate = findViewById(R.id.textViewAnimalBirthdate);
        textViewEstimatedBirthdate = findViewById(R.id.textViewEstimatedBirthdate);
        textViewAnimalSize = findViewById(R.id.textViewAnimalSize);
        textViewAnimalDescription = findViewById(R.id.textViewAnimalDescription);
        textViewAnimalMicroNumber = findViewById(R.id.textViewAnimalMicroNumber);
        textViewAnimalNeutered = findViewById(R.id.textViewAnimalNeutered);
        listTags = findViewById(R.id.listTags);

        // Inicializar vistas del listing (tabla tableDatos)
        tvPhone = findViewById(R.id.tvPhone);
        tvEmail = findViewById(R.id.tvEmail);
        tvAddress = findViewById(R.id.tvAddress);
        tvCity = findViewById(R.id.tvCity);
        tvProvince = findViewById(R.id.tvProvince);
        tvPostalCode = findViewById(R.id.tvPostalCode);
        tvCountry = findViewById(R.id.tvCountry);
        switchAdoptado = findViewById(R.id.switchadop);

        // Inicializar botones
        btnedit = findViewById(R.id.btnedit);
        btndel = findViewById(R.id.btndel);
    }

    private void loadAnimalData() {
        // Cargar imagen del animal
        Picasso.get()
                .load(photoUrl)
                .placeholder(R.drawable.gatoinicio)
                .error(R.drawable.gatoinicio)
                .into(imgAnimal);

        // Cargar datos básicos del animal
        txtName.setText(animal.getAnimalName());
        txtSex.setText(animal.getSex() != null ? animal.getSex().toString() : "Sin especificar");
        textViewSpecies.setText(speciesName);

        // Cargar fecha de nacimiento
        if (animal.getBirthDate() != null) {
            DateTimeFormatter formatoSalida = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            String fechaFormateada = animal.getBirthDate().format(formatoSalida);
            textViewAnimalBirthdate.setText(fechaFormateada);
        }

        // Mostrar si la fecha es estimada
        if (animal.getIsBirthDateEstimated()) {
            textViewEstimatedBirthdate.setVisibility(View.VISIBLE);
        } else {
            textViewEstimatedBirthdate.setVisibility(View.GONE);
        }

        // Cargar tamaño y descripción
        textViewAnimalSize.setText(animal.getSize() != null ? animal.getSize() : "Sin especificar");
        textViewAnimalDescription.setText(animal.getAnimalDescription() != null ?
                animal.getAnimalDescription() : "Sin descripción");

        // Cargar número de microchip si existe
        if (animal.getMicrochipNumber() != null && !animal.getMicrochipNumber().isEmpty()) {
            textViewAnimalMicroNumber.setText(animal.getMicrochipNumber());
            // Mostrar la fila del microchip
            View tableRowMicro = findViewById(R.id.tableRowAnimalMicroNumber);
            if (tableRowMicro != null) {
                tableRowMicro.setVisibility(View.VISIBLE);
            }
        } else {
            View tableRowMicro = findViewById(R.id.tableRowAnimalMicroNumber);
            if (tableRowMicro != null) {
                tableRowMicro.setVisibility(View.GONE);
            }
        }

        // Cargar estado de esterilización (solo para perros y gatos)
        if (animal.getSpeciesId() == 1 || animal.getSpeciesId() == 2) {
            textViewAnimalNeutered.setText(animal.getIsNeutered() ? "Sí" : "No");
        } else {
            textViewAnimalNeutered.setText("N/A");
        }

        // Cargar etiquetas en segundo plano
        loadAnimalTags();
    }

    private void loadAnimalTags() {
        new Thread(() -> {
            List<Tag> animalTags = animal.getTagList();
            if (animalTags != null && !animalTags.isEmpty()) {
                ArrayAdapter<Tag> adapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_list_item_1,
                        animalTags);

                runOnUiThread(() -> {
                    listTags.setAdapter(adapter);
                    listTags.setVisibility(View.VISIBLE);
                });
            } else {
                runOnUiThread(() -> {
                    listTags.setVisibility(View.GONE);
                });
            }
        }).start();
    }

    private void loadListingData() {
        if (animalListing == null) {
            Log.w("ShowMyListingActivity", "AnimalListing es null");
            return;
        }

        // Cargar datos de contacto
        tvPhone.setText(animalListing.getContactPhone() != null ?
                animalListing.getContactPhone() : "Sin especificar");
        tvEmail.setText(animalListing.getContactEmail() != null ?
                animalListing.getContactEmail() : "Sin especificar");

        // Cargar datos de ubicación
        if (location != null) {
            tvAddress.setText(location.getAddress() != null ?
                    location.getAddress() : "Sin especificar");
            tvCity.setText(location.getCity() != null ?
                    location.getCity() : "Sin especificar");
            tvProvince.setText(location.getProvince() != null ?
                    location.getProvince() : "Sin especificar");
            tvPostalCode.setText(location.getPostalCode() != null ?
                    location.getPostalCode() : "Sin especificar");
            tvCountry.setText(location.getCountry() != null ?
                    location.getCountry() : "Sin especificar");
        } else {
            // Si no hay ubicación, mostrar valores por defecto
            tvAddress.setText("Sin especificar");
            tvCity.setText("Sin especificar");
            tvProvince.setText("Sin especificar");
            tvPostalCode.setText("Sin especificar");
            tvCountry.setText("Sin especificar");
        }

        // Configurar switch de adoptado
        switchAdoptado.setChecked(false); // Ajusta según tu modelo
    }

    private void setupListeners() {
        // Listener para editar listing
        btnedit.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateListingActivity.class);
            intent.putExtra("animal", animal);
            intent.putExtra("listing", animalListing);
            intent.putExtra("mode", "edit");
            startActivity(intent);
        });

        // Listener para eliminar listing
        btndel.setOnClickListener(v -> {
            ApiRequests api = new ApiRequests();
            MyApplication.executor.execute(() -> {
                // Llamada para eliminar el listing
                boolean success = api.deleteListing(animalListing.getListingId());
                if(success){
                    Toast.makeText(this, "Registro borrado", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "No se ha podido borrar", Toast.LENGTH_SHORT).show();

                }

                runOnUiThread(() -> {
                    // if (success) {
                    //     Toast.makeText(this, "Listing eliminado", Toast.LENGTH_SHORT).show();
                    //     finish(); // Cerrar la activity
                    // } else {
                    //     Toast.makeText(this, "Error al eliminar listing", Toast.LENGTH_SHORT).show();
                    // }
                });
            });
        });

        // Listener para el switch de adoptado
        // switchAdoptado.setOnCheckedChangeListener((buttonView, isChecked) -> {
        //     updateAdoptionStatus(isChecked);
        // });
    }

    // actualizar el estado de adopción (comentado por ahora)
    // private void updateAdoptionStatus(boolean isAdopted) {
    //     ApiRequests api = new ApiRequests();
    //     new Thread(() -> {
    //         boolean success = api.updateAdoptionStatus(animalListing.getListingId(), isAdopted);
    //
    //         runOnUiThread(() -> {
    //             if (success) {
    //                 Toast.makeText(this,
    //                         isAdopted ? "Marcado como adoptado" : "Marcado como disponible",
    //                         Toast.LENGTH_SHORT).show();
    //             } else {
    //                 Toast.makeText(this, "Error actualizando estado", Toast.LENGTH_SHORT).show();
    //                 switchAdoptado.setChecked(!isAdopted);
    //             }
    //         });
    //     }).start();
    // }

    // Método estático para crear el Intent desde otras activities
    public static Intent createIntent(android.content.Context context, Animal animal,
                                      AnimalListing animalListing, Location location) {
        Intent intent = new Intent(context, ShowMyListingActivity.class);
        intent.putExtra("animal", animal);
        intent.putExtra("animalListing", animalListing);
        intent.putExtra("location", location);
        return intent;
    }
}