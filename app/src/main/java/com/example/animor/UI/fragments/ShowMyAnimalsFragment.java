package com.example.animor.UI.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.animor.Model.Animal;
import com.example.animor.R;
import com.example.animor.Utils.AnimalAdapter;
import com.example.animor.Utils.ApiRequests;

import java.util.ArrayList;
import java.util.List;

public class ShowMyAnimalsFragment extends Fragment implements AnimalAdapter.OnAnimalClickListener {

    private RecyclerView rvAnimals;
    private AnimalAdapter adapter;
    private ArrayList<Animal> animalList;

    // Interface para comunicación con la Activity
    public interface OnAnimalSelectedListener {
        void onAnimalSelected(Animal animal);
    }

    private OnAnimalSelectedListener animalSelectedListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_my_animals, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        setupRecyclerView();
        loadAnimals();

        // Configurar listener si la activity lo implementa
        if (getActivity() instanceof OnAnimalSelectedListener) {
            animalSelectedListener = (OnAnimalSelectedListener) getActivity();
        }
    }

    private void initializeViews(View view) {
        rvAnimals = view.findViewById(R.id.recyclerViewMyAnimals);
    }

    private void setupRecyclerView() {
        animalList = new ArrayList<>();
        adapter = new AnimalAdapter(animalList, this);
        rvAnimals.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAnimals.setAdapter(adapter);

        // Opcional: añadir separadores
        rvAnimals.addItemDecoration(new DividerItemDecoration(
                rvAnimals.getContext(), LinearLayoutManager.VERTICAL));
    }

    private void loadAnimals() {
        ApiRequests api = new ApiRequests();
        new Thread(() -> {
            List<Animal> newAnimalList = api.askForMyAnimalsToDatabase();

            if (getActivity() != null) {
                requireActivity().runOnUiThread(() -> {
                    animalList.clear();
                    animalList.addAll(newAnimalList);
                    adapter.notifyDataSetChanged();

                    if (animalList.isEmpty()) {
                        Toast.makeText(getContext(),
                                "No tienes animales registrados", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    @Override
    public void onAnimalClick(Animal animal) {
        // Opción 1: Usar interface para comunicarse con la Activity
        if (animalSelectedListener != null) {
            animalSelectedListener.onAnimalSelected(animal);
            return;
        }

        // Opción 2: Intentar navegar si hay un NavController disponible
        try {
            NavController navController = Navigation.findNavController(requireView().findViewById(R.id.nav_host_fragment_animals));
           // NavController navController = NavHostFragment.findNavController(this);
            Bundle args = new Bundle();
            args.putSerializable("animal", animal);
            // Cambia R.id.action_to_animal_detail por tu acción real
            navController.navigate(R.id.action_showMyAnimals_to_showMyAnimal, args);

        } catch (IllegalStateException e) {
            Log.e("Navigation", "No se pudo navegar: " + e.getMessage());
            // Fallback: mostrar información básica
            Toast.makeText(getContext(),
                    "Animal seleccionado: " + animal.getAnimalName(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFavoriteClick(Animal animal) {
        // Implementa tu lógica para favoritos
        ApiRequests api = new ApiRequests();
        new Thread(() -> {
            // Aquí harías la llamada para actualizar favoritos
            // boolean success = api.updateFavoriteStatus(animal);

            if (getActivity() != null) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(),
                            "Favorito actualizado", Toast.LENGTH_SHORT).show();
                    // Actualizar el adapter si es necesario
                    adapter.notifyDataSetChanged();
                });
            }
        }).start();
    }

    public void updateAnimalList(ArrayList<Animal> newAnimalList) {
        if (animalList != null && adapter != null) {
            animalList.clear();
            animalList.addAll(newAnimalList);
            adapter.notifyDataSetChanged();
        }
    }

    // Método para refrescar la lista desde la Activity
    public void refreshAnimalList() {
        loadAnimals();
    }
}