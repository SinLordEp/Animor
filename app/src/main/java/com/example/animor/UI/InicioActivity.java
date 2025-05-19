package com.example.animor.UI;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.animor.Model.Animal;
import com.example.animor.R;
import com.example.animor.Utils.AnimalAdapter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InicioActivity extends AppCompatActivity implements AnimalAdapter.OnAnimalClickListener {

    private RecyclerView recyclerView;
    private AnimalAdapter adapter;
    private List<Animal> animals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inicio);

        // Configurar RecyclerView
        recyclerView = findViewById(R.id.recyclerViewAnimals);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar la lista de animales
        animals = crearListaAnimalesEjemplo();

        // Crear el adaptador con el listener
        adapter = new AnimalAdapter(animals, this);
        recyclerView.setAdapter(adapter);
    }

    // Método mejorado para crear datos de ejemplo
    private List<Animal> crearListaAnimalesEjemplo() {
        List<Animal> listaEjemplo = new ArrayList<>();

        // Animal 1
        Animal perro = new Animal();
        perro.setAnimalName("Max");
        perro.setSpeciesId(1); // Perro
        perro.setBirthDate(LocalDate.of(2020, 5, 15));
        perro.setIsBirthDateEstimated(false);
        perro.setSex(Animal.Sex.MALE);
        perro.setSize("Mediano");
        perro.setAnimalDescription("Perro juguetón y cariñoso");
        perro.setIsNeutered(true);
        perro.setMicrochipNumber("123456789");
        perro.setIsAdopted(false);
        listaEjemplo.add(perro);

        // Animal 2
        Animal gato = new Animal(
                "Luna",
                2, // Gato
                LocalDate.of(2021, 3, 10),
                true,
                Animal.Sex.FEMALE,
                "Pequeño",
                "Gata tranquila y mimosa",
                true,
                "987654321",
                true
        );
        listaEjemplo.add(gato);

        // Animal 3
        listaEjemplo.add(new Animal(
                "Rocky",
                1, // Perro
                LocalDate.of(2019, 8, 22),
                false,
                Animal.Sex.MALE,
                "Grande",
                "Perro protector y leal",
                false,
                "456123789",
                false
        ));

        return listaEjemplo;
    }

    @Override
    public void onAnimalClick(Animal animal) {
        // Mostrar información básica del animal seleccionado
        String mensaje = String.format("%s - %s\nEdad: %s años\nTamaño: %s\n%s",
                animal.getAnimalName(),
                animal.getSex() == Animal.Sex.MALE ? "Macho" :
                        animal.getSex() == Animal.Sex.FEMALE ? "Hembra" : "Desconocido",
                calcularEdad(animal.getBirthDate()),
                animal.getSize(),
                Boolean.TRUE.equals(animal.getIsAdopted()) ? "Adoptado" : "Disponible");

        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();

        // Opcional: Abrir actividad de detalle
        Intent intent = new Intent(this, AnimalActivity.class);
        intent.putExtra("ANIMAL_OBJECT", animal); // Requiere que Animal implemente Parcelable
        startActivity(intent);
    }

    // Método para calcular la edad aproximada
    private int calcularEdad(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null) return 0;
        return LocalDate.now().getYear() - fechaNacimiento.getYear();
    }

    public void actualizarListaAnimales(List<Animal> nuevaLista) {
        if (nuevaLista != null) {
            animals.clear();
            animals.addAll(nuevaLista);
            adapter.notifyDataSetChanged();
        }
    }
}