package com.example.animor.UI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.animor.App.MyApplication;
import com.example.animor.Model.dto.SpeciesDTO;
import com.example.animor.Model.entity.Animal;
import com.example.animor.Model.entity.Photo;
import com.example.animor.Model.entity.Tag;
import com.example.animor.R;
import com.example.animor.Utils.ApiRequests;
import com.example.animor.Utils.PreferenceUtils;
import com.squareup.picasso.Picasso;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ShowMyAnimalActivity extends AppCompatActivity {

    private ImageView imgAnimal;
    private TextView txtName, txtSex, textViewSpecies;
    private TextView textViewAnimalBirthdate, textViewEstimatedBirthdate;
    private TextView textViewAnimalSize;
    private TextView textViewAnimalDescription;
    private TextView textViewMicroNumber, textViewAnimalMicroNumber;
    private TextView textViewAnimalNeutered;
    private TextView textViewNeutered;
    private ListView listTags;

    //botones
    private Button btnedit;
    private Button btndel;

    //variables globales
    Animal animal = null;
    String speciesName = "";
    List<Tag>tags;
    List<Photo> photos;
    String photoUrl = "";
    LocalDate birthDate = LocalDate.now();
    private final String TAG = "ShowMyAnimalActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_my_animal_buttons);

        // Obtener los datos del Intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("animal")) {
            animal = (Animal) intent.getSerializableExtra("animal");

            if (animal != null) {
                birthDate = animal.getBirthDate();
                Log.d(TAG, "BirthDate: " + birthDate);
                Log.d("Animal in show_my_animal", animal.getAnimalName() + "\n" + animal.getBirthDate() + "\n" + animal.getTagList().size());

                // Usar las tags si las necesitas
                if (tags != null) {
                    Log.d(TAG, "Tags recibidas: " + tags.size());
                    // Aquí puedes usar las tags como necesites
                }
                initializeData();
                initViews();
                setupListeners();
            } else {
                Log.e(TAG, "Animal is null");
                Toast.makeText(this, "Error: No se pudo cargar la información del animal", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Log.e(TAG, "Intent or animal extra is null");
            Toast.makeText(this, "Error: No se pudo cargar la información del animal", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initializeData() {
        Log.d(TAG, "Lista de tags: "+animal.getTagList().toString());
        List<SpeciesDTO> species = PreferenceUtils.getSpeciesList();

        for (SpeciesDTO s : species) {
            if (s.getSpeciesId() == animal.getSpeciesId()) {
                speciesName = s.getSpeciesName();
            }
        }

        List<Photo> photoList = animal.getAnimalPhotoList();
        for (Photo a : photoList) {
            if (a.getIsCoverPhoto()) {
                photoUrl = a.getPhotoUrl();
                break;
            }
        }
        Log.d(TAG, "se ha conseguido la foto: " + photoUrl);
        tags = animal.getTagList();
    }


    public void initViews() {
        imgAnimal = findViewById(R.id.imgUser);
        btndel = findViewById(R.id.btndel);
        btnedit = findViewById(R.id.btnedit);
        textViewAnimalNeutered = findViewById(R.id.textViewAnimalNeutered);


        Picasso.get()
                .load(photoUrl)
                .placeholder(R.drawable.gatoinicio)
                .error(R.drawable.gatoinicio)
                .into(imgAnimal);

        txtName = findViewById(R.id.txtName);
        txtName.setText(animal.getAnimalName());

        txtSex = findViewById(R.id.txtSex);
        Log.d(TAG, "Tags: "+animal.getSex().toString());
        switch(animal.getSex()){
            case Male:
                txtSex.setText("Macho");
                break;
            case Female:
                txtSex.setText("Hembra");
                break;
            case Unknown:
                txtSex.setText("Desconocido");
                break;
        }

        textViewSpecies = findViewById(R.id.tvSpecies);
        textViewSpecies.setText(speciesName);

        textViewAnimalBirthdate = findViewById(R.id.textViewAnimalBirthdate);
        DateTimeFormatter formatoSalida = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String fechaFormateada = animal.getBirthDate().format(formatoSalida);
        LocalDate birthDate = animal.getBirthDate();
        textViewAnimalBirthdate.setText(birthDate.toString());

        if (animal.getIsBirthDateEstimated()) {
            textViewEstimatedBirthdate = findViewById(R.id.textViewEstimatedBirthdate);
            textViewEstimatedBirthdate.setVisibility(View.VISIBLE);
        }

        textViewAnimalSize = findViewById(R.id.textViewAnimalSize);
        textViewAnimalSize.setText(animal.getSize());

        textViewAnimalDescription = findViewById(R.id.textViewAnimalDescription);
        textViewAnimalDescription.setText(animal.getAnimalDescription());

        if (animal.getMicrochipNumber() != null) {
            textViewMicroNumber = findViewById(R.id.textViewAnimalMicroNumber);
            textViewMicroNumber.setText(animal.getMicrochipNumber());
        }

        if (animal.getSpeciesId() == 1 || animal.getSpeciesId() == 2) {
            if (animal.getIsNeutered()) {
                textViewAnimalNeutered.setText("sí");
            } else {
                textViewAnimalNeutered.setText("no");
            }
        }else{
            textViewNeutered = findViewById(R.id.textViewNeutered);
            textViewNeutered.setVisibility(View.GONE);
            textViewAnimalNeutered.setVisibility(View.GONE);
        }

        listTags = findViewById(R.id.listTags);
        listTags.setVisibility(View.VISIBLE);

        new Thread(() -> {
            //List<Tag> animalTags = animal.getTagList();
            ArrayAdapter<Tag> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1,
                    tags);
            runOnUiThread(() -> {
                listTags.setAdapter(adapter);
            });
        }).start();
    }

    private void setupListeners() {
        btndel.setOnClickListener(v -> {
            ApiRequests api = new ApiRequests();
            MyApplication.executor.execute(()->{
             api.deleteAnimal(animal.getAnimalId());
            });
            Toast.makeText(this, "Animal eliminado", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, CreateActivity.class));
            finish(); // Cierra la actividad actual
        });

        btnedit.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateActivity.class);
            intent.putExtra("animal", animal);
            intent.putExtra("mode", "edit");
            startActivity(intent);
        });
    }

    // method estático para crear un Intent y iniciar esta Activity
    public static void startActivity(AppCompatActivity fromActivity, Animal animal) {
        Intent intent = new Intent(fromActivity, ShowMyAnimalActivity.class);
        intent.putExtra("animal", animal);
        fromActivity.startActivity(intent);
    }
}