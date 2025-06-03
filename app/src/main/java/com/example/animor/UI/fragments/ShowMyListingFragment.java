package com.example.animor.UI.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.animor.Model.Animal;
import com.example.animor.R;
import com.example.animor.Utils.AnimalAdapter;

public class ShowMyListingFragment extends Fragment{

    private RecyclerView recyclerView;
    private AnimalAdapter adapter;

    public ShowMyListingFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_listing, container, false);

        recyclerView = view.findViewById(R.id.rvAnimalesUsuario);

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

    public void onListingClick(Animal animal) {
        NavHostFragment navHostFragment = (NavHostFragment) getChildFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_animals);
        NavController navController = navHostFragment.getNavController();

        // Crea un Bundle y navega al fragmento de detalle
        Bundle args = new Bundle();
        args.putSerializable("animal", animal);
        navController.navigate(R.id.action_showMyAnimals_to_showMyAnimal, args);
    }


//    private List<Animal> getAnimalesDelUsuario() {
//        List<Animal> animales = new ArrayList<>();
//        animales.add(new Animal("Luna", R.drawable.perro));
//        animales.add(new Animal("Michi", R.drawable.gato));
//        return animales;
//    }
}

