package com.example.animor.UI.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.animor.Model.Animal;
import com.example.animor.Model.AnimalPhoto;
import com.example.animor.R;
import com.example.animor.Utils.NonScrollListView;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class CreateOneListingFragment extends Fragment {
    // Vistas de información del animal
    private ImageView imgUser;
    private TextView txtName;
    private TextView txtCity;
    private TextView txtSex;
    private TextView tvSpecies;
    private TextView textViewAnimalBirthdate;
    private TextView textViewEstimatedBirthdate;
    private TextView textViewAnimalSize;
    private TextView textViewAnimalDescription;
    private TextView textViewAnimalMicroNumber;
    private TextView textViewAnimalNeutered;

    // Campos de contacto/dirección (EditText)
    private EditText editTextPhone;
    private EditText editTextTextEmailAddress;
    private EditText etAddress;
    private EditText etCiudad;
    private EditText etProvincia;
    private EditText etPostalCode;
    private EditText etCountry;

    // Botones y otros controles
    private Button buttonSave;
    private ImageButton btnMenu;
    private NonScrollListView listTags;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    public static CreateOneListingFragment newInstance(Animal animal) {
        CreateOneListingFragment fragment = new CreateOneListingFragment();
        Bundle args = new Bundle();
        args.putSerializable("animal", animal);  // o putParcelable si usas Parcelable
        fragment.setArguments(args);
        return fragment;
    }
    private Animal animal;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            animal = (Animal) getArguments().getSerializable("animal");
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_one_listing, container, false);
        initViews(view);
        setupListeners();
        if (animal != null) {
            loadAnimalData(animal);
        }
        return view;
    }



    private void initViews(View view) {
        // Inicialización de todas las vistas de información del animal
        imgUser = view.findViewById(R.id.imgUser);
        txtName = view.findViewById(R.id.txtName);
        txtCity = view.findViewById(R.id.txtCity);
        txtSex = view.findViewById(R.id.txtSex);
        tvSpecies = view.findViewById(R.id.tvSpecies);
        textViewAnimalBirthdate = view.findViewById(R.id.textViewAnimalBirthdate);
        textViewEstimatedBirthdate = view.findViewById(R.id.textViewEstimatedBirthdate);
        textViewAnimalSize = view.findViewById(R.id.textViewAnimalSize);
        textViewAnimalDescription = view.findViewById(R.id.textViewAnimalDescription);
        textViewAnimalMicroNumber = view.findViewById(R.id.textViewAnimalMicroNumber);
        textViewAnimalNeutered = view.findViewById(R.id.textViewAnimalNeutered);

        // Campos de formulario (EditText)
        editTextPhone = view.findViewById(R.id.editTextPhone);
        editTextTextEmailAddress = view.findViewById(R.id.editTextTextEmailAddress);
        etAddress = view.findViewById(R.id.etAddress);
        etCiudad = view.findViewById(R.id.etCiudad);
        etProvincia = view.findViewById(R.id.etProvincia);
        etPostalCode = view.findViewById(R.id.etPostalCode);
        etCountry = view.findViewById(R.id.etCountry);

        // Controles interactivos
        buttonSave = view.findViewById(R.id.buttonSave);
        btnMenu = view.findViewById(R.id.btn_menu);
        listTags = view.findViewById(R.id.listTags);

        // Componentes del drawer
        navigationView = view.findViewById(R.id.navigation_view);
        drawerLayout = view.findViewById(R.id.drawer_layout);
    }

    private void setupListeners() {
        buttonSave.setOnClickListener(v -> saveListing());
        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Puedes agregar más listeners aquí según necesites
    }

    // Método para validar el formulario
    public boolean validateForm() {
        boolean isValid = true;

        if (etCiudad.getText().toString().trim().isEmpty()) {
            etCiudad.setError("Ciudad requerida");
            isValid = false;
        }

        // Agrega validaciones para otros campos requeridos
        if (editTextPhone.getText().toString().trim().isEmpty()) {
            editTextPhone.setError("Teléfono requerido");
            isValid = false;
        }

        return isValid;
    }

    // Método para guardar el listing
    public void saveListing() {
        if (!validateForm()) {
            Toast.makeText(getContext(), "Por favor complete todos los campos requeridos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Aquí iría la lógica para guardar los datos
        String ciudad = etCiudad.getText().toString().trim();
        String telefono = editTextPhone.getText().toString().trim();
        // ... obtener otros valores

        Toast.makeText(getContext(), "Registro guardado correctamente", Toast.LENGTH_SHORT).show();
    }

    // Método para limpiar el formulario
    public void clearForm() {
        // Limpiar campos de información del animal
        txtName.setText("");
        txtCity.setText("");
        txtSex.setText("");
        tvSpecies.setText("");
        textViewAnimalBirthdate.setText("");
        textViewAnimalSize.setText("");
        textViewAnimalDescription.setText("");
        textViewAnimalMicroNumber.setText("");
        textViewAnimalNeutered.setText("");
        imgUser.setImageResource(R.drawable.gatoinicio); // Imagen por defecto

        // Limpiar campos de formulario
        editTextPhone.setText("");
        editTextTextEmailAddress.setText("");
        etAddress.setText("");
        etCiudad.setText("");
        etProvincia.setText("");
        etPostalCode.setText("");
        etCountry.setText("");
    }

    // Método para cargar datos del animal
    public void loadAnimalData(Animal animal) {
        if (animal != null) {
            txtName.setText(animal.getAnimalName());
            txtSex.setText(animal.getSex().toString());
            tvSpecies.setText(animal.getSpeciesId());
            DateTimeFormatter formatoSalida = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            String fechaFormateada = animal.getBirthDate().format(formatoSalida);
            textViewAnimalBirthdate.setText(fechaFormateada);
            textViewAnimalSize.setText(animal.getSize());
            textViewAnimalDescription.setText(animal.getAnimalDescription());
            textViewAnimalNeutered.setText(animal.getIsNeutered() ? "Sí" : "No");
            String photoUrl = "";
            ArrayList<AnimalPhoto> photoList = animal.getAnimalPhotoList();
            for (AnimalPhoto a : photoList){
                if(a.getIsCoverPhoto()){
                    photoUrl=a.getPhotoUrl();
                }
            }
            Picasso.get()
                    .load(photoUrl)
                    .placeholder(R.drawable.gatoinicio)
                    .error(R.drawable.gatoinicio)
                    .into(imgUser);
        }
    }
}