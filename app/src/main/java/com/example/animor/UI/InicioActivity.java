package com.example.animor.UI;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.animor.R;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import Model.Animal;
import com.example.animor.Utils.*;

public class InicioActivity extends AppCompatActivity implements AnimalAdapter.OnAnimalClickListener {

    private RecyclerView recyclerView;
    private AnimalAdapter adapter;
    private List<Animal> animals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Opcional: para edge-to-edge design
        setContentView(R.layout.activity_inicio);

        // Configurar RecyclerView
        recyclerView = findViewById(R.id.recyclerViewAnimals);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar la lista de animales
        animals = crearListaAnimalesEjemplo();

        // Crear el adaptador con el listener (esta actividad implementa la interfaz)
        adapter = new AnimalAdapter(animals, this, this);
        recyclerView.setAdapter(adapter);
    }

    // Método para crear datos de ejemplo
    private List<Animal> crearListaAnimalesEjemplo() {
        List<Animal> listaEjemplo = new ArrayList<>();

        // Animal 1
        listaEjemplo.add(new Animal(
                "Max",
                1, // ID de especie (perro)
                LocalDate.of(2020, 5, 15),
                false,
                Animal.Sex.MALE,
                "Mediano",
                "Perro juguetón y cariñoso",
                true,
                "123456789",
                false
        ));

        // Animal 2
        listaEjemplo.add(new Animal(
                "Luna",
                2, // ID de especie (gato)
                LocalDate.of(2021, 3, 10),
                true, // Fecha estimada
                Animal.Sex.FEMALE,
                "Pequeño",
                "Gata tranquila y mimosa",
                true,
                "987654321",
                true
        ));

        // Animal 3
        listaEjemplo.add(new Animal(
                "Rocky",
                1, // ID de especie (perro)
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

    // Implementación del método de la interfaz OnAnimalClickListener
    @Override
    public void onAnimalClick(Animal animal) {
        // Aquí manejas el clic en un animal
        startActivity(new Intent(InicioActivity.this, MyAnimalsActivity.class));

        Toast.makeText(this, "Seleccionado: " + animal.getAnimalName(), Toast.LENGTH_SHORT).show();

        // También puedes abrir una nueva actividad con los detalles del animal:
        /*
        Intent intent = new Intent(this, DetalleAnimalActivity.class);
        intent.putExtra("animal_id", animal.getAnimalId());
        startActivity(intent);
        */
    }

    /**
     * actualizar la lista
     * @param nuevaLista
     */
    public void actualizarListaAnimales(List<Animal> nuevaLista) {
        animals.clear();
        animals.addAll(nuevaLista);
        adapter.updateAnimals(animals);
    }
}