package com.example.animor.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.animor.Model.Animal;
import com.example.animor.R;
import com.example.animor.Utils.*;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InicioActivity extends AppCompatActivity implements AnimalAdapter.OnAnimalClickListener {

    private RecyclerView recyclerView;
    private AnimalAdapter adapter;
    private List<Animal> lista;
    AnimalAdapter.OnAnimalClickListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inicio);

        // Configurar RecyclerView
        recyclerView = findViewById(R.id.recyclerViewAnimals);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar lista de ejemplo
        lista = obtenerAnimales();
       // adapter = new AnimalAdapter(this, lista, listener);
        recyclerView.setAdapter(adapter);

        // Configurar navegación inferior
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_inicio); // marcar como activo

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_inicio) {
                    return true; // Ya estamos en esta actividad
                } else if (id == R.id.nav_favs) {
                    startActivity(new Intent(InicioActivity.this, FavActivity.class));
                    return true;
                } else if (id == R.id.nav_listing) {
                    startActivity(new Intent(InicioActivity.this, CreateActivity.class));
                    return true;
                } else if (id == R.id.nav_user) {
                    startActivity(new Intent(InicioActivity.this, ProfileActivity.class));
                    return true;
                } else if (id == R.id.nav_animals) {
                    startActivity(new Intent(InicioActivity.this, MyAnimalsActivity.class));
                    return true;
                }

                return false;
            }
        });
    }

    private List<Animal> obtenerAnimales() {
        ArrayList<Animal>lista= new ArrayList<>();
        return lista;
    }

    @Override
    public void onAnimalClick(Animal animal) {
        String mensaje = String.format("%s - %s\nEdad: %s años\nTamaño: %s\n%s",
                animal.getName(),
                animal.getSex() == Animal.Sex.MALE ? "Macho" :
                        animal.getSex() == Animal.Sex.FEMALE ? "Hembra" : "Desconocido",
                calcularEdad(animal.getBirthDate()),
                animal.getSize(),
                Boolean.TRUE.equals(animal.getIsAdopted()) ? "Adoptado" : "Disponible");

        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, AnimalActivity.class);
        intent.putExtra("ANIMAL_OBJECT", animal);
        startActivity(intent);
    }

    @Override
    public void onFavoriteClick(Animal animal) {

    }

    private int calcularEdad(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null) return 0;
        return LocalDate.now().getYear() - fechaNacimiento.getYear();
    }

//    public void actualizarListaAnimales(List<Animal> nuevaLista) {
//        if (nuevaLista != null) {
//            animals.clear();
//            animals.addAll(nuevaLista);
//            adapter.notifyDataSetChanged();
//        }
//    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
