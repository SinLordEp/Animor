package com.example.animor.UI.ViewsFrames;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.animor.Model.dto.SpeciesDTO;
import com.example.animor.Model.entity.Animal;
import com.example.animor.R;
import com.example.animor.UI.CreateActivity;
import com.example.animor.UI.CreateListingActivity;
import com.example.animor.Utils.ApiRequests;
import com.example.animor.Model.entity.Species;
import com.example.animor.Utils.AnimalAdapter;
import com.example.animor.Utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.List;

public class CreateListingFragment extends Fragment implements AnimalAdapter.OnAnimalClickListener {

    private RecyclerView rvAnimalesUsuario;
    private TextView tvEligeAnimal;
    private Button btnCrearNuevoAnimal;

    private AnimalAdapter animalAdapter;
    private List<Animal> animalList;
    private List<SpeciesDTO> speciesList;
    private TextView noanimales;

    // Interface para comunicación con la Activity (opcional)
    public interface OnAnimalSelectedForListingListener {
        void onAnimalSelectedForListing(Animal animal);
    }

    private OnAnimalSelectedForListingListener animalSelectedListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_listing, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        setupRecyclerView();
        setupListeners();
        loadUserAnimals();

        // Configurar listener si la activity lo implementa
        if (getActivity() instanceof OnAnimalSelectedForListingListener) {
            animalSelectedListener = (OnAnimalSelectedForListingListener) getActivity();
        }
    }

    private void initializeViews(View view) {
        rvAnimalesUsuario = view.findViewById(R.id.rvAnimalesUsuario);
        tvEligeAnimal = view.findViewById(R.id.tvEligeAnimal);
        btnCrearNuevoAnimal = view.findViewById(R.id.btnCrearNuevoAnimal);
        noanimales = view.findViewById(R.id.noanimales);
    }

    private void setupRecyclerView() {
        animalList = new ArrayList<>();
        speciesList = PreferenceUtils.getSpeciesList(); // Obtener lista de especies

        animalAdapter = new AnimalAdapter(animalList, speciesList, this);
        rvAnimalesUsuario.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvAnimalesUsuario.setAdapter(animalAdapter);

        // Opcional: añadir separadores
        rvAnimalesUsuario.addItemDecoration(new DividerItemDecoration(
                rvAnimalesUsuario.getContext(), LinearLayoutManager.HORIZONTAL));
    }

    private void setupListeners() {
        btnCrearNuevoAnimal.setOnClickListener(v -> {
            // Navegar a la activity para crear un nuevo animal
            Intent intent = new Intent(getContext(), CreateActivity.class);

            startActivity(intent);
        });
    }

    private void loadUserAnimals() {
        ApiRequests api = new ApiRequests();
        new Thread(() -> {
            // Cargar los animales del usuario actual
            List<Animal> userAnimals = api.getMyAnimalsFromServer(); // Asegúrate de tener este método

            if (getActivity() != null) {
                requireActivity().runOnUiThread(() -> {
                    animalList.clear();
                    if (userAnimals != null && !userAnimals.isEmpty()) {
                        animalList.addAll(userAnimals);
                        animalAdapter.notifyDataSetChanged();
                    } else {
                        // No tiene animales, mostrar mensaje
                        noanimales.setText("No tienes animales registrados. Crea uno primero.");
                        noanimales.setVisibility(View.VISIBLE);
                    }
                });
            }
        }).start();
    }

    // Implementación de OnAnimalClickListener
    @Override
    public void onAnimalClick(Animal animal) {
        // Cuando selecciona un animal, navegar a CreateListingActivity para crear el listing
        Intent intent = new Intent(getActivity(), CreateListingActivity.class);
        intent.putExtra("animal", animal);
        intent.putExtra("mode", "create"); // Modo creación (no edición)
        startActivity(intent);

        // Notificar a la activity padre si implementa el listener
        if (animalSelectedListener != null) {
            animalSelectedListener.onAnimalSelectedForListing(animal);
        }

        Toast.makeText(getContext(),
                "Seleccionado: " + animal.getAnimalName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFavoriteClick(Animal animal) {
        // Implementar lógica de favoritos para animales si es necesario
        Toast.makeText(getContext(),
                "Favorito: " + animal.getAnimalName(), Toast.LENGTH_SHORT).show();
    }

    // Método público para refrescar la lista desde la Activity
    public void refreshAnimalList() {
        loadUserAnimals();
    }

    // Método para actualizar la lista externamente
    public void updateAnimalList(List<Animal> newAnimalList) {
        if (animalList != null && animalAdapter != null) {
            animalList.clear();
            if (newAnimalList != null) {
                animalList.addAll(newAnimalList);
            }
            animalAdapter.notifyDataSetChanged();
        }
    }

}