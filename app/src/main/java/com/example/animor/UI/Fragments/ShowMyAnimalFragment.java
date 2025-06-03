package com.example.animor.UI.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.animor.App.MyApplication;
import com.example.animor.Model.Animal;
import com.example.animor.Model.AnimalPhoto;
import com.example.animor.Model.Species;
import com.example.animor.Model.Tag;
import com.example.animor.R;
import com.example.animor.Utils.ApiRequests;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ShowMyAnimalFragment extends Fragment {

    private ImageView imgAnimal;
    private TextView txtName, txtSex, textViewSpecies;
    private TextView textViewAnimalBirthdate, textViewEstimatedBirthdate;
    private TextView textViewAnimalSize;
    private TextView textViewAnimalDescription;
    private TextView textViewMicroNumber, textViewAnimalMicroNumber;
    private TextView textViewAnimalNeutered;
    private ListView listTags;
    //menú hamburguesa
    private ImageButton btnMenu;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    Animal animal = null;
    public static ShowMyAnimalFragment newInstance(Animal animal) {
        ShowMyAnimalFragment fragment = new ShowMyAnimalFragment();
        Bundle args = new Bundle();
        args.putSerializable("animal", animal); // O implementa Parcelable
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            animal = (Animal) getArguments().getSerializable("animal");
            assert animal != null;
            Log.d("Animal in show_my_animal", animal.toString());
        }
        return inflater.inflate(R.layout.fragment_show_my_animal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ArrayList<Species> species = MyApplication.getSpecies();
        String speciesName="";
        for (Species s : species){
            if(s.getSpeciesId()== animal.getAnimalId()){
                speciesName=s.getSpeciesName();
            }
        }
        ArrayList<AnimalPhoto> photoList = animal.getAnimalPhotoList();
        String photoUrl = "";
        for (AnimalPhoto a : photoList){
            if(a.getIsCoverPhoto()){
                photoUrl=a.getPhotoUrl();
            }
        }

        DateTimeFormatter formatoSalida = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String fechaFormateada = animal.getBirthDate().format(formatoSalida);
        // Inicializar vistas
        btnMenu = view.findViewById(R.id.btn_menu);
        drawer = view.findViewById(R.id.drawer_layout);
        navigationView = view.findViewById(R.id.navigation_view);
        navigationView.inflateMenu(R.menu.drawer_menu_myanimal);

        imgAnimal = view.findViewById(R.id.imgUser);
        Picasso.get()
                .load(photoUrl)
                .placeholder(R.drawable.gatoinicio)
                .error(R.drawable.gatoinicio)
                .into(imgAnimal);
        txtName = view.findViewById(R.id.txtName);
        txtName.setText(animal.getAnimalName());
        txtSex = view.findViewById(R.id.txtSex);
        txtSex.setText(animal.getSex().toString());
        textViewSpecies = view.findViewById(R.id.tvSpecies);
        textViewSpecies.setText(speciesName);
        textViewAnimalBirthdate = view.findViewById(R.id.textViewAnimalBirthdate);
        textViewAnimalBirthdate.setText(fechaFormateada);
        if(animal.getIsBirthDateEstimated()){
            textViewEstimatedBirthdate = view.findViewById(R.id.textViewEstimatedBirthdate);
            textViewEstimatedBirthdate.setVisibility(View.VISIBLE);
        }
        textViewAnimalSize = view.findViewById(R.id.textViewAnimalSize);
        textViewAnimalSize.setText(animal.getSize());
        textViewAnimalDescription = view.findViewById(R.id.textViewAnimalDescription);
        textViewAnimalDescription.setText(animal.getAnimalDescription());
        if(animal.getMicrochipNumber()!=null){
            textViewMicroNumber = view.findViewById(R.id.textViewAnimalMicroNumber);
            textViewMicroNumber.setText(animal.getMicrochipNumber());

        }
        if(animal.getAnimalId()==1|animal.getAnimalId()==2) {
            textViewAnimalNeutered = view.findViewById(R.id.textViewAnimalNeutered);
            if(animal.getIsNeutered()){
                textViewAnimalNeutered.setText("sí");
            }else{
                textViewAnimalNeutered.setText("no");
            }
        }
        listTags = view.findViewById(R.id.listTags);
        listTags.setVisibility(View.VISIBLE);
        new Thread(() -> {
            ArrayList<Tag> animalTags = animal.getTags();
            ArrayAdapter<Tag> adapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_list_item_1,
                    animalTags);
            requireActivity().runOnUiThread(() -> {
                listTags.setAdapter(adapter);
            });
        }).start();

    }
    private void setupNavigation() {
        btnMenu.setOnClickListener(v -> drawer.openDrawer(GravityCompat.START));

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_edit) {
            } else if (id == R.id.nav_delete) {
                ApiRequests api = new ApiRequests();
                api.deleteAnimal(animal.getAnimalId());
            }
            drawer.closeDrawer(GravityCompat.START);
            return true;
        });
    }

}
