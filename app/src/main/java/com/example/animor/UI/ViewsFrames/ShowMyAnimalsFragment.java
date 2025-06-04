package com.example.animor.UI.ViewsFrames;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.animor.Model.Animal;
import com.example.animor.Model.Species;
import com.example.animor.R;
import com.example.animor.UI.LoginActivity;
import com.example.animor.UI.ShowActivity;
import com.example.animor.UI.CreateActivity;
import com.example.animor.Utils.AnimalAdapter;
import com.example.animor.Utils.ApiRequests;
import com.example.animor.Utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.List;

public class ShowMyAnimalsFragment extends Fragment implements AnimalAdapter.OnAnimalClickListener {

    List<Species> speciesList = PreferenceUtils.getSpeciesList();
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
        Log.d("DEBUG", "onViewCreated() ejecutado");

        initializeViews(view);
        setupRecyclerView();
        loadAnimals();
    }

    private void initializeViews(View view) {
        rvAnimals = view.findViewById(R.id.recyclerViewMyAnimals);
    }

    private void setupRecyclerView() {
        animalList = new ArrayList<>();
        List<Species> speciesList = PreferenceUtils.getSpeciesList();
        adapter = new AnimalAdapter(animalList, speciesList, this);
        rvAnimals.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAnimals.setAdapter(adapter);

        // Opcional: añadir separadores
        rvAnimals.addItemDecoration(new DividerItemDecoration(
                rvAnimals.getContext(), LinearLayoutManager.VERTICAL));
    }

    // MÉTODO PRINCIPAL CORREGIDO
    private void loadAnimals() {
        ApiRequests api = new ApiRequests();
        new Thread(() -> {
            Log.d("DEBUG", "loadAnimals() llamado");
            List<Animal> newAnimalList = api.askForMyAnimalsToDatabase();

            // Cambiar a requireActivity().runOnUiThread() para todo el manejo de UI
            requireActivity().runOnUiThread(() -> {
                handleAnimalsResult(newAnimalList);
            });
        }).start();
    }

    // NUEVO MÉTODO: Manejo centralizado de resultados
    private void handleAnimalsResult(List<Animal> newAnimalList) {
        // Verificar si el fragment aún está adjunto antes de proceder
        if (!isAdded() || getView() == null) {
            Log.w("DEBUG", "Fragment no está adjunto, saliendo de handleAnimalsResult");
            return;
        }

        // CASO 1: Lista null = Usuario no autenticado o error de conexión grave
        if (newAnimalList == null) {
            showNoLoginLayout();
            return;
        }

        // CASO 2: Lista vacía = Usuario autenticado pero sin animales
        if (newAnimalList.isEmpty()) {
            showEmptyAnimalsLayout();
            return;
        }

        // CASO 3: Lista con animales = Mostrar normalmente
        showAnimalsLayout(newAnimalList);
    }

    // NUEVO MÉTODO: Mostrar layout cuando no hay login
    private void showNoLoginLayout() {
        Log.d("DEBUG", "Mostrando layout de no login");

        // Ocultar RecyclerView
        rvAnimals.setVisibility(View.GONE);

        // Mostrar layout de no login
        LinearLayout layoutNoLogin = getView().findViewById(R.id.layoutNoLogin);
        if (layoutNoLogin != null) {
            layoutNoLogin.setVisibility(View.VISIBLE);

            // Configurar botón de iniciar sesión
            Button btnIniciarSesion = layoutNoLogin.findViewById(R.id.btnIniciarSesion);
            if (btnIniciarSesion != null) {
                btnIniciarSesion.setOnClickListener(v -> navigateToLogin());
            }
        }
    }

    // NUEVO MÉTODO: Mostrar layout cuando no hay animales
    private void showEmptyAnimalsLayout() {
        Log.d("DEBUG", "Usuario autenticado pero sin animales");

        // Mostrar RecyclerView vacío
        rvAnimals.setVisibility(View.VISIBLE);

        // Ocultar layout de no login
        LinearLayout layoutNoLogin = getView().findViewById(R.id.layoutNoLogin);
        if (layoutNoLogin != null) {
            layoutNoLogin.setVisibility(View.GONE);
        }

        // Limpiar lista y notificar adapter
        animalList.clear();
        adapter.notifyDataSetChanged();

        // Mostrar mensaje informativo
        Toast.makeText(getContext(), "No tienes animales registrados", Toast.LENGTH_LONG).show();

    }

    // NUEVO MÉTODO: Mostrar animales normalmente
    private void showAnimalsLayout(List<Animal> newAnimalList) {
        Log.d("DEBUG", "Mostrando " + newAnimalList.size() + " animales");

        // Mostrar RecyclerView
        rvAnimals.setVisibility(View.VISIBLE);

        // Ocultar layout de no login
        LinearLayout layoutNoLogin = getView().findViewById(R.id.layoutNoLogin);
        if (layoutNoLogin != null) {
            layoutNoLogin.setVisibility(View.GONE);
        }


        // Actualizar lista de forma segura
        animalList.clear();
        animalList.addAll(newAnimalList); // Ya no será null aquí
        adapter.notifyDataSetChanged();
    }

    // NUEVO MÉTODO: Navegar a login
    private void navigateToLogin() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    // Navegar a crear animal
    private void navigateToCreateAnimal() {
        // Navegar a CreateActivity para crear un animal
        Intent intent = new Intent(getActivity(), CreateActivity.class);
        intent.putExtra("target_fragment", "create_animal");
        intent.putExtra("mode", "create");
        startActivity(intent);
    }

    // Método fallback por si no hay listener (opcional, para casos extremos)
    private void navigateToDetailFallback(Animal animal) {
        if (getActivity() instanceof ShowActivity) {
            ((ShowActivity) getActivity()).onAnimalSelected(animal);
        }
    }

    // Método para establecer el listener desde ShowActivity
    public void setAnimalSelectedListener(OnAnimalSelectedListener listener) {
        this.animalSelectedListener = listener;
    }

    @Override
    public void onAnimalClick(Animal animal) {
        // Usar únicamente el interface para comunicarse con ShowActivity
        if (animalSelectedListener != null) {
            animalSelectedListener.onAnimalSelected(animal);
        } else {
            // Fallback: Si no hay listener, intentar navegar directamente
            Log.w("ShowMyAnimalsFragment", "No listener found, attempting direct navigation");
            navigateToDetailFallback(animal);
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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnAnimalSelectedListener) {
            animalSelectedListener = (OnAnimalSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnAnimalSelectedListener");
        }
    }

    // MÉTODO MEJORADO: Actualización segura de lista
    public void updateAnimalList(ArrayList<Animal> newAnimalList) {
        if (animalList != null && adapter != null) {
            animalList.clear();
            if (newAnimalList != null) {
                animalList.addAll(newAnimalList);
            }
            adapter.notifyDataSetChanged();
        }
    }

    // MÉTODO PÚBLICO: Para refrescar desde fuera
    public void refreshAnimalList() {
        loadAnimals();
    }
}