package com.example.animor.UI.ViewsFrames;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.animor.App.MyApplication;
import com.example.animor.Model.Animal;
import com.example.animor.Model.AnimalListing;
import com.example.animor.Model.AnimalPhoto;
import com.example.animor.Model.Location;
import com.example.animor.Model.Species;
import com.example.animor.Model.Tag;
import com.example.animor.R;
import com.example.animor.UI.CreateListingActivity;
import com.example.animor.Utils.ApiRequests;
import com.example.animor.Utils.NonScrollListView;
import com.example.animor.Utils.PreferenceUtils;
import com.squareup.picasso.Picasso;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ShowMyListingFragment extends Fragment {
    // Variables para datos del animal
    private ImageView imgAnimal;
    private TextView txtName, txtSex, textViewSpecies;
    private TextView textViewAnimalBirthdate, textViewEstimatedBirthdate;
    private TextView textViewAnimalSize;
    private TextView textViewAnimalDescription;
    private TextView textViewAnimalMicroNumber;
    private TextView textViewAnimalNeutered;
    private NonScrollListView listTags;

    // Variables para datos del listing
    private TextView tvPhone, tvEmail;
    private TextView tvAddress, tvCity, tvProvince, tvPostalCode, tvCountry;
    private SwitchCompat switchAdoptado;

    // Botones
    private Button btnedit;
    private Button btndel;

    // Variables globales
    private Animal animal = null;
    private AnimalListing animalListing = null;
    private Location location = null;
    private String speciesName = "";
    private String photoUrl = "";

    public ShowMyListingFragment() {
        // Constructor vacío requerido
    }

    public static ShowMyListingFragment newInstance(Animal animal, AnimalListing animalListing) {
        ShowMyListingFragment fragment = new ShowMyListingFragment();
        Bundle args = new Bundle();
        args.putSerializable("animal", animal);
        args.putSerializable("animalListing", animalListing);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            animal = (Animal) getArguments().getSerializable("animal");
            animalListing = (AnimalListing) getArguments().getSerializable("animalListing");

            if (animalListing != null) {
                location = (Location) getArguments().getSerializable("location");
            }
        }
        return inflater.inflate(R.layout.fragment_show_my_listing, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obtener nombre de la especie
        List<Species> species = PreferenceUtils.getSpeciesList();
        for (Species s : species) {
            if (s.getSpeciesId() == animal.getSpeciesId()) {
                speciesName = s.getSpeciesName();
                break;
            }
        }
        // Obtener URL de la foto de portada
        ArrayList<AnimalPhoto> photoList = animal.getAnimalPhotoList();
        if (photoList != null) {
            for (AnimalPhoto a : photoList) {
                if (a.getIsCoverPhoto()) {
                    photoUrl = a.getPhotoUrl();
                    break;
                }
            }
        }

        initViews(view);
        setupListeners();
        loadAnimalData();
        loadListingData();
    }

    private void initViews(View view) {
        // Inicializar vistas del animal (tabla tableanimal)
        imgAnimal = view.findViewById(R.id.imgUser);
        txtName = view.findViewById(R.id.txtName);
        txtSex = view.findViewById(R.id.txtSex);
        textViewSpecies = view.findViewById(R.id.tvSpecies);
        textViewAnimalBirthdate = view.findViewById(R.id.textViewAnimalBirthdate);
        textViewEstimatedBirthdate = view.findViewById(R.id.textViewEstimatedBirthdate);
        textViewAnimalSize = view.findViewById(R.id.textViewAnimalSize);
        textViewAnimalDescription = view.findViewById(R.id.textViewAnimalDescription);
        textViewAnimalMicroNumber = view.findViewById(R.id.textViewAnimalMicroNumber);
        textViewAnimalNeutered = view.findViewById(R.id.textViewAnimalNeutered);
        listTags = view.findViewById(R.id.listTags);

        // Inicializar vistas del listing (tabla tableDatos)
        tvPhone = view.findViewById(R.id.tvPhone);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvAddress = view.findViewById(R.id.tvAddress);
        tvCity = view.findViewById(R.id.tvCity);
        tvProvince = view.findViewById(R.id.tvProvince);
        tvPostalCode = view.findViewById(R.id.tvPostalCode);
        tvCountry = view.findViewById(R.id.tvCountry);
        switchAdoptado = view.findViewById(R.id.switchadop);

        // Inicializar botones
        btnedit = view.findViewById(R.id.btnedit);
        btndel = view.findViewById(R.id.btndel);
    }

    private void loadAnimalData() {
        // Cargar imagen del animal
        Picasso.get()
                .load(photoUrl)
                .placeholder(R.drawable.gatoinicio)
                .error(R.drawable.gatoinicio)
                .into(imgAnimal);

        // Cargar datos básicos del animal
        txtName.setText(animal.getAnimalName());
        txtSex.setText(animal.getSex() != null ? animal.getSex().toString() : "Sin especificar");
        textViewSpecies.setText(speciesName);

        // Cargar fecha de nacimiento
        if (animal.getBirthDate() != null) {
            DateTimeFormatter formatoSalida = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            String fechaFormateada = animal.getBirthDate().format(formatoSalida);
            textViewAnimalBirthdate.setText(fechaFormateada);
        }

        // Mostrar si la fecha es estimada
        if (animal.getIsBirthDateEstimated()) {
            textViewEstimatedBirthdate.setVisibility(View.VISIBLE);
        } else {
            textViewEstimatedBirthdate.setVisibility(View.GONE);
        }

        // Cargar tamaño y descripción
        textViewAnimalSize.setText(animal.getSize() != null ? animal.getSize() : "Sin especificar");
        textViewAnimalDescription.setText(animal.getAnimalDescription() != null ?
                animal.getAnimalDescription() : "Sin descripción");

        // Cargar número de microchip si existe
        if (animal.getMicrochipNumber() != null && !animal.getMicrochipNumber().isEmpty()) {
            textViewAnimalMicroNumber.setText(animal.getMicrochipNumber());
            // Mostrar la fila del microchip
            View tableRowMicro = getView().findViewById(R.id.tableRowAnimalMicroNumber);
            if (tableRowMicro != null) {
                tableRowMicro.setVisibility(View.VISIBLE);
            }
        } else {
            View tableRowMicro = getView().findViewById(R.id.tableRowAnimalMicroNumber);
            if (tableRowMicro != null) {
                tableRowMicro.setVisibility(View.GONE);
            }
        }
        // Cargar estado de esterilización (solo para perros y gatos)
        if (animal.getSpeciesId() == 1 || animal.getSpeciesId() == 2) {
            textViewAnimalNeutered.setText(animal.getIsNeutered() ? "Sí" : "No");
        } else {
            textViewAnimalNeutered.setText("N/A");
        }

        // Cargar etiquetas en segundo plano
        loadAnimalTags();
    }

    private void loadAnimalTags() {
        new Thread(() -> {
            ArrayList<Tag> animalTags = animal.getTags();
            if (animalTags != null && !animalTags.isEmpty()) {
                ArrayAdapter<Tag> adapter = new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_list_item_1,
                        animalTags);

                requireActivity().runOnUiThread(() -> {
                    listTags.setAdapter(adapter);
                    listTags.setVisibility(View.VISIBLE);
                });
            } else {
                requireActivity().runOnUiThread(() -> {
                    listTags.setVisibility(View.GONE);
                });
            }
        }).start();
    }

    private void loadListingData() {
        if (animalListing == null) {
            Log.w("ShowMyListingFragment", "AnimalListing es null");
            return;
        }

        // Cargar datos de contacto
        tvPhone.setText(animalListing.getContactPhone() != null ?
                animalListing.getContactPhone() : "Sin especificar");
        tvEmail.setText(animalListing.getContactEmail() != null ?
                animalListing.getContactEmail() : "Sin especificar");

        // Cargar datos de ubicación
        if (location != null) {
            tvAddress.setText(location.getAddress() != null ?
                    location.getAddress() : "Sin especificar");
            tvCity.setText(location.getCity() != null ?
                    location.getCity() : "Sin especificar");
            tvProvince.setText(location.getProvince() != null ?
                    location.getProvince() : "Sin especificar");
            tvPostalCode.setText(location.getPostalCode() != null ?
                    location.getPostalCode() : "Sin especificar");
            tvCountry.setText(location.getCountry() != null ?
                    location.getCountry() : "Sin especificar");
        } else {
            // Si no hay ubicación, mostrar valores por defecto
            tvAddress.setText("Sin especificar");
            tvCity.setText("Sin especificar");
            tvProvince.setText("Sin especificar");
            tvPostalCode.setText("Sin especificar");
            tvCountry.setText("Sin especificar");
        }

        // Configurar switch de adoptado (asumo que tienes este campo en AnimalListing)
        // switchAdoptado.setChecked(animalListing.isAdopted());
        switchAdoptado.setChecked(false); // Por ahora, ajusta según tu modelo
    }

    private void setupListeners() {
        // Listener para editar listing
        btnedit.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreateListingActivity.class);
            intent.putExtra("animal", animal);
            intent.putExtra("listing", animalListing);
            intent.putExtra("mode", "edit");
            startActivity(intent);
        });

        // Listener para eliminar listing
        btndel.setOnClickListener(v -> {
            ApiRequests api = new ApiRequests();
            MyApplication.executor.execute(() -> {
                // Llamada para eliminar el listing
               // boolean success = api.deleteListing(animalListing.getListingId());

                requireActivity().runOnUiThread(() -> {
//                    if (success) {
//                        Toast.makeText(getActivity(), "Listing eliminado", Toast.LENGTH_SHORT).show();
//                        // Volver a la activity anterior o actualizar la lista
//                        if (getActivity() != null) {
//                            getActivity().onBackPressed();
//                        }
//                    } else {
//                        Toast.makeText(getActivity(), "Error al eliminar listing", Toast.LENGTH_SHORT).show();
//                    }
                });
           });
        });

        // Listener para el switch de adoptado
//        switchAdoptado.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            // Aquí puedes actualizar el estado en la base de datos
//            updateAdoptionStatus(isChecked);
       // });
            // }

//    private void updateAdoptionStatus(boolean isAdopted) {
//        ApiRequests api = new ApiRequests();
//        new Thread(() -> {
//            // Actualizar estado de adopción
//            boolean success = api.updateAdoptionStatus(animalListing.getListingId(), isAdopted);
//
//            requireActivity().runOnUiThread(() -> {
//                if (success) {
//                    Toast.makeText(getActivity(),
//                            isAdopted ? "Marcado como adoptado" : "Marcado como disponible",
//                            Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(getActivity(), "Error actualizando estado", Toast.LENGTH_SHORT).show();
//                    // Revertir el switch si falló
//                    switchAdoptado.setChecked(!isAdopted);
//                }
//            });
//        }).start();
    }

}
