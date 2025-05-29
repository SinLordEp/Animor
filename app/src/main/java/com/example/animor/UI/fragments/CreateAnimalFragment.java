package com.example.animor.UI.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.animor.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CreateAnimalFragment extends Fragment {

    // Componentes de fecha
    private Calendar calendar;
    private SimpleDateFormat dateFormatter;

    // Componentes de la UI
    private EditText etNombre, etEspecie, etFechaNacimiento, etCiudad, etTamano, etDescripcion, etMicrochip;
    private CheckBox cbNacimientoAproximado, cbCastrado;
    private RadioGroup rgSexo;
    private RadioButton rbMacho, rbHembra, rbDesconocido;
    private ImageView imgAnimal;
    private Button btnSeleccionarImagen;
    private Button btnGuardar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_animal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializar formateador de fecha
        calendar = Calendar.getInstance();
        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        // Inicializar vistas
        initViews(view);

        // Configurar listeners
        setupListeners();
    }

    private void initViews(View view) {
        // EditTexts
        etNombre = view.findViewById(R.id.etNombre);
        etEspecie = view.findViewById(R.id.etEspecie);
        etFechaNacimiento = view.findViewById(R.id.etFechaNacimiento);
        etCiudad = view.findViewById(R.id.etCiudad);
        etTamano = view.findViewById(R.id.etTamano);
        etDescripcion = view.findViewById(R.id.etDescripcion);
        etMicrochip = view.findViewById(R.id.etMicrochip);

        // CheckBoxes
        cbNacimientoAproximado = view.findViewById(R.id.cbNacimientoAproximado);
        cbCastrado = view.findViewById(R.id.cbCastrado);

        // RadioGroup y RadioButtons
        rgSexo = view.findViewById(R.id.rgSexo);
        rbMacho = view.findViewById(R.id.rbMacho);
        rbHembra = view.findViewById(R.id.rbHembra);
        rbDesconocido = view.findViewById(R.id.rbDesconocido);

        // Imagen y botón
        imgAnimal = view.findViewById(R.id.imgAnimal);
        btnSeleccionarImagen = view.findViewById(R.id.btnSeleccionarImagen);
        btnGuardar = view.findViewById(R.id.buttonSave);
    }

    private void setupListeners() {
        // Listener para seleccionar fecha
        etFechaNacimiento.setOnClickListener(v -> showDatePickerDialog());

        // Listener para seleccionar imagen
        btnSeleccionarImagen.setOnClickListener(v -> selectImage());

        btnGuardar.setOnClickListener(v -> {
            saveAnimal();
            clearForm();
        });

        // Listener para ciudad (podrías implementar un diálogo de selección)
        etCiudad.setOnClickListener(v -> selectCity());
    }

    private void showDatePickerDialog() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    calendar.set(selectedYear, selectedMonth, selectedDay);
                    String formattedDate = dateFormatter.format(calendar.getTime());
                    etFechaNacimiento.setText(formattedDate);
                },
                year, month, day);

        datePickerDialog.show();
    }

    private void selectImage() {
        // Implementar lógica para seleccionar imagen de galería o cámara
        Toast.makeText(getContext(), "Seleccionar imagen", Toast.LENGTH_SHORT).show();
        // Ejemplo:
        // Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void selectCity() {
        // Implementar lógica para seleccionar ciudad
        Toast.makeText(getContext(), "Seleccionar ciudad", Toast.LENGTH_SHORT).show();
        // Podrías usar un diálogo con lista de ciudades o una API
    }

    // Método para validar el formulario
    public boolean validateForm() {
        boolean isValid = true;

        if (etNombre.getText().toString().trim().isEmpty()) {
            etNombre.setError("Nombre requerido");
            isValid = false;
        }

        if (etEspecie.getText().toString().trim().isEmpty()) {
            etEspecie.setError("Especie requerida");
            isValid = false;
        }

        if (etFechaNacimiento.getText().toString().trim().isEmpty()) {
            etFechaNacimiento.setError("Fecha de nacimiento requerida");
            isValid = false;
        }

        if (etCiudad.getText().toString().trim().isEmpty()) {
            etCiudad.setError("Ciudad requerida");
            isValid = false;
        }

        if (etTamano.getText().toString().trim().isEmpty()) {
            etTamano.setError("Tamaño requerido");
            isValid = false;
        }

        if (etDescripcion.getText().toString().trim().isEmpty()) {
            etDescripcion.setError("Descripción requerida");
            isValid = false;
        }

        return isValid;
    }

    // Método para guardar el animal (llamado desde la Activity)
    public void saveAnimal() {
        if (!validateForm()) {
            Toast.makeText(getContext(), "Por favor complete todos los campos requeridos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener datos del formulario
        String nombre = etNombre.getText().toString().trim();
        String especie = etEspecie.getText().toString().trim();
        String fechaNacimiento = etFechaNacimiento.getText().toString().trim();
        boolean nacimientoAprox = cbNacimientoAproximado.isChecked();
        String ciudad = etCiudad.getText().toString().trim();

        String sexo = "";
        int selectedId = rgSexo.getCheckedRadioButtonId();
        if (selectedId == R.id.rbMacho) {
            sexo = "Macho";
        } else if (selectedId == R.id.rbHembra) {
            sexo = "Hembra";
        } else if (selectedId == R.id.rbDesconocido) {
            sexo = "Desconocido";
        }

        String tamaño = etTamano.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        boolean castrado = cbCastrado.isChecked();
        String microchip = etMicrochip.getText().toString().trim();

        // Aquí iría la lógica para guardar en tu base de datos
        // Ejemplo:
        // Animal animal = new Animal(nombre, especie, fechaNacimiento, ...);
        // databaseReference.child("animales").push().setValue(animal);

        Toast.makeText(getContext(), "Animal guardado correctamente", Toast.LENGTH_SHORT).show();
    }

    // Método para limpiar el formulario
    public void clearForm() {
        etNombre.setText("");
        etEspecie.setText("");
        etFechaNacimiento.setText("");
        cbNacimientoAproximado.setChecked(false);
        etCiudad.setText("");
        rgSexo.clearCheck();
        etTamano.setText("");
        etDescripcion.setText("");
        cbCastrado.setChecked(false);
        etMicrochip.setText("");
        imgAnimal.setImageResource(R.drawable.gatoinicio);
    }
}