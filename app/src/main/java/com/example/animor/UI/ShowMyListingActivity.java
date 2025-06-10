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

import androidx.annotation.NonNull;
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
import com.example.animor.Model.request.AnimalRequest;
import com.example.animor.Model.request.PhotoRequest;
import com.example.animor.Model.request.TagRequest;
import com.example.animor.R;
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
    private TextView tvCity, tvProvince, tvCountry;
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
    private String TAG="ShowMyListingActivity";
    String mode;
    ApiRequests api = new ApiRequests();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_my_listing);
        btnedit = findViewById(R.id.btnedit);
        switchAdoptado = findViewById(R.id.switchadop);
        btndel = findViewById(R.id.btndel);
        Intent intent = getIntent();
        if (intent != null) {
            animalListing = (AnimalListing) intent.getSerializableExtra("listing");
            if (animalListing != null) {
                Log.d(TAG, "Listing pasado por intent: "+animalListing.getListingId());
                Log.d(TAG, "Animal del listing: "+animalListing.getAnimal().getAnimalName());
            }else{
                Log.d(TAG, "Listing pasado por intent: ES NULO");
            }
            mode = intent.getStringExtra("mode");

            if (animalListing != null) {
                animal = animalListing.getAnimal();
                location = animalListing.getLocation();
            }
            if(mode!=null && mode.equals("adoptive")){
                switchAdoptado.setVisibility(View.GONE);
                btndel.setVisibility(View.GONE);
                btnedit.setVisibility(View.GONE);
            }
        }
        initViews();

        // Verificar que tenemos los datos necesarios
        if (animalListing == null) {
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
        Log.d(TAG, "tamaño lista de fotos: "+photoList.size());
        if (photoList != null) {
            for (Photo a : photoList) {
                Log.d(TAG, "¿La foto es cover?: "+a.getIsCoverPhoto());
                //if (a.getIsCoverPhoto()) {
                    photoUrl = a.getPhotoUrl();
                    break;
                //}
            }
        }
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
        tvCity = findViewById(R.id.tvCity);
        tvProvince = findViewById(R.id.tvProvince);
        tvCountry = findViewById(R.id.tvCountry);
    }

    private void loadAnimalData() {
        // Cargar imagen del animal
        Picasso.get()
                .load(photoUrl)
                .placeholder(R.drawable.gatoinicio)
                .error(R.drawable.gatoinicio)
                .fit()
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
        Log.d(TAG, "Adoptado: "+animal.isAdopted());

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
            tvCity.setText(location.getCity() != null ?
                    location.getCity() : "Sin especificar");
            tvProvince.setText(location.getProvince() != null ?
                    location.getProvince() : "Sin especificar");
            tvCountry.setText(location.getCountry() != null ?
                    location.getCountry() : "Sin especificar");
        } else {
            // Si no hay ubicación, mostrar valores por defecto
            tvCity.setText("Sin especificar");
            tvProvince.setText("Sin especificar");
            tvCountry.setText("Sin especificar");
        }

    }
    private void setupListeners() {
        // Listener para editar listing
        btnedit.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateListingActivity.class);
            intent.putExtra("animal", animalListing.getAnimal());
            intent.putExtra("listing", animalListing);
            intent.putExtra("mode", "edit");
            startActivity(intent);
        });

        // Listener para eliminar listing
        btndel.setOnClickListener(v -> {
            MyApplication.executor.execute(() -> {
                // Llamada para eliminar el listing
                boolean success = api.deleteListing(animalListing.getListingId());
                if(success){
                    runOnUiThread(()-> Toast.makeText(this, "Registro borrado", Toast.LENGTH_SHORT).show());
                }else{
                    runOnUiThread(()-> Toast.makeText(this, "No se ha podido borrar", Toast.LENGTH_SHORT).show());
                }

                runOnUiThread(() -> {
                     if (success) {
                         Toast.makeText(this, "Listing eliminado", Toast.LENGTH_SHORT).show();
                         finish(); // Cerrar la activity
                     } else {
                         Toast.makeText(this, "Error al eliminar listing", Toast.LENGTH_SHORT).show();
                     }
                });
            });
        });

        switchAdoptado.setChecked(animal.isAdopted());
        switchAdoptado.setOnCheckedChangeListener((buttonView, isChecked) -> {
             animal.setIsAdopted(isChecked);
             AnimalRequest animalRequest = new AnimalRequest();
             animalRequest.setAnimalId(animal.getAnimalId());
             animalRequest.setSpeciesId(animal.getSpeciesId());
             animalRequest.setAnimalName(animal.getAnimalName());
             animalRequest.setBirthDate(animal.getBirthDate());
             animalRequest.setBirthDateEstimated(animal.getIsBirthDateEstimated());
             animalRequest.setSex(animal.getSex());
             animalRequest.setSize(animal.getSize());
             animalRequest.setAnimalDescription(animal.getAnimalDescription());
             animalRequest.setNeutered(animal.getIsNeutered());
             animalRequest.setMicrochipNumber(animal.getMicrochipNumber());
             animalRequest.setAdopted(animal.isAdopted());
             List<PhotoRequest> photoRequests = getPhotoRequests();
             List<TagRequest>tagRequestList=new ArrayList<>();
             for(Tag t: animal.getTagList()){
                 TagRequest tr = new TagRequest();
                 tr.setTagId(t.getTagId());
                 tr.setTagName(t.getTagName());
                 tagRequestList.add(tr);
             }
             animalRequest.setPhotoList(photoRequests);
             animalRequest.setTagList(tagRequestList);
             MyApplication.executor.execute(()-> {
                 boolean success= api.editAnimal(animalRequest);
                 if(success){
                     Log.d(TAG, "ANIMAL ACTUALIZADO: ADOPTADO");
                     //Intent intent = new Intent(ShowMyListingActivity.this, ShowActivity.class);
                    // startActivity(intent);
                 }else{
                     runOnUiThread(()->{
                         animal.setIsAdopted(!isChecked);
                         switchAdoptado.setChecked(!isChecked);

                     });
                 }
             });
         });
    }

    @NonNull
    private List<PhotoRequest> getPhotoRequests() {
        List<Photo>photoList = animal.getPhotoList();
        List<PhotoRequest>photoRequests= new ArrayList<>();
        for(Photo p: photoList){
            PhotoRequest pr=new PhotoRequest();
            pr.setPhotoId(p.getPhotoId());
            pr.setPhotoUrl(p.getPhotoUrl());
            pr.setFilePath(p.getFilePath());
            pr.setCoverPhoto(p.getIsCoverPhoto());
            pr.setDisplayOrder(p.getDisplayOrder());
            photoRequests.add(pr);
        }
        return photoRequests;
    }

    public static Intent createIntent(android.content.Context context, Animal animal,
                                      AnimalListing animalListing, Location location) {
        Intent intent = new Intent(context, ShowMyListingActivity.class);
        intent.putExtra("animal", animal);
        intent.putExtra("animalListing", animalListing);
        intent.putExtra("location", location);
        return intent;
    }
}