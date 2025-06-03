package com.example.animor.UI.fragments;

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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.animor.Model.Animal;
import com.example.animor.R;
import com.example.animor.UI.LoginActivity;
import com.example.animor.UI.ShowActivity;
import com.example.animor.UI.UserActivity;
import com.example.animor.Utils.AnimalAdapter;
import com.example.animor.Utils.ApiRequests;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
            Log.d("DEBUG", "loadAnimals() llamado");
            List<Animal> newAnimalList = api.askForMyAnimalsToDatabase();
            if (newAnimalList == null){
                requireActivity().runOnUiThread(() -> {rvAnimals.setVisibility(View.GONE);});
                LinearLayout layoutNoLogin = getView().findViewById(R.id.layoutNoLogin);
                requireActivity().runOnUiThread(() -> { layoutNoLogin.setVisibility(View.VISIBLE);});
                Button btnIniciarSesion = getView().findViewById(R.id.btnIniciarSesion);
                btnIniciarSesion.setOnClickListener(v -> {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    requireActivity().finish();
                });
            }

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
            // (esto no debería pasar si ShowActivity está configurado correctamente)
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

    public void updateAnimalList(ArrayList<Animal> newAnimalList) {
        if (animalList != null && adapter != null) {
            animalList.clear();
            animalList.addAll(newAnimalList);
            adapter.notifyDataSetChanged();
        }
    }
}