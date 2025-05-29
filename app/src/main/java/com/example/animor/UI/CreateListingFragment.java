package com.example.animor.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.animor.Model.Animal;
import com.example.animor.R;
import com.example.animor.Utils.AnimalAdapter;

import java.util.ArrayList;
import java.util.List;

public class CreateListingFragment extends Fragment {

    private RecyclerView recyclerView;
    private Button crearAnimalButton;
    private AnimalAdapter adapter;

    public CreateListingFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_listing, container, false);

        recyclerView = view.findViewById(R.id.rvAnimalesUsuario);
        crearAnimalButton = view.findViewById(R.id.btnCrearNuevoAnimal);

        // Configurar RecyclerView en horizontal
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

       // adapter = new AnimalAdapter(getAnimalesDelUsuario());
        recyclerView.setAdapter(adapter);

//        crearAnimalButton.setOnClickListener(v -> {
//            // Usa Navigation Component para navegar al fragmento de creación
//            NavHostFragment.findNavController(CreateListingFragment.this)
//                    .navigate(R.id.action_createListingFragment_to_createAnimalFragment);
//        });

        return view;
    }

//    private List<Animal> getAnimalesDelUsuario() {
//        List<Animal> animales = new ArrayList<>();
//        animales.add(new Animal("Luna", R.drawable.perro));
//        animales.add(new Animal("Michi", R.drawable.gato));
//        return animales;
//    }
}

