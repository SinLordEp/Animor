package com.example.animor.UI.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
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
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.animor.Model.Animal;
import com.example.animor.R;
import com.example.animor.Utils.ApiRequests;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Locale;

public class CreateAnimalFragment extends Fragment {

    // Activity Result Launchers
    private ActivityResultLauncher<Intent> seleccionarImagenLauncher;
    private ActivityResultLauncher<String[]> pedirPermisos;

    // Componentes de fecha
    private Calendar calendar;
    private SimpleDateFormat dateFormatter;

    // Firebase Storage
    private Uri imagenSeleccionadaUri;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private String imageDownloadUrl; // Para guardar la URL de descarga
    private boolean imagenPendienteSubir = false; // Flag para saber si hay imagen por subir

    // Componentes de la UI
    private EditText etNombre, etEspecie, etFechaNacimiento, etTamano, etDescripcion, etMicrochip;
    private CheckBox cbNacimientoAproximado, cbCastrado;
    private RadioGroup rgSexo;
    private RadioButton rbMacho, rbHembra, rbDesconocido;
    private ImageView imgAnimal;
    private Button btnSeleccionarImagen;
    private Button btnGuardar;
    LocalDate birthDate;

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

        // Inicializar Firebase Storage
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReferenceFromUrl("gs://named-icon-460311-e4.firebasestorage.app");

        // Inicializar vistas
        initViews(view);

        // Configurar listeners
        setupListeners();

        // Configurar Activity Result Launchers
        setupActivityResultLaunchers();
    }

    private void initViews(View view) {
        // EditTexts
        etNombre = view.findViewById(R.id.etNombre);
        etEspecie = view.findViewById(R.id.etEspecie);
        etFechaNacimiento = view.findViewById(R.id.etFechaNacimiento);
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

        // Imagen y botones
        imgAnimal = view.findViewById(R.id.imgAnimal);
        btnSeleccionarImagen = view.findViewById(R.id.btnSeleccionarImagen);
        btnGuardar = view.findViewById(R.id.buttonSave);
        btnGuardar.setVisibility(View.VISIBLE);
    }

    private void setupListeners() {
        // Listener para seleccionar fecha
        etFechaNacimiento.setOnClickListener(v -> showDatePickerDialog());

        // Listener para seleccionar imagen
        btnSeleccionarImagen.setOnClickListener(v -> solicitarPermisos());

        // Listener para guardar
        btnGuardar.setOnClickListener(v -> {
            saveAnimal();
        });
    }

    private void setupActivityResultLaunchers() {
        // Launcher para seleccionar imagen
        seleccionarImagenLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        imagenSeleccionadaUri = result.getData().getData();
                        if (imagenSeleccionadaUri != null) {
                            // Mostrar la imagen seleccionada en el ImageView
                            imgAnimal.setImageURI(imagenSeleccionadaUri);
                            // Marcar que hay una imagen pendiente de subir
                            imagenPendienteSubir = true;
                            // Cambiar el texto del botón para indicar que hay imagen seleccionada
                            btnSeleccionarImagen.setText("Imagen seleccionada ✓");
                            Toast.makeText(getContext(), "Imagen seleccionada. Se subirá al guardar.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        // Launcher para permisos
        // Launcher para permisos
        pedirPermisos = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    // Solo verificamos el permiso esencial (READ_MEDIA_IMAGES o READ_EXTERNAL_STORAGE)
                    boolean permisoEsencialConcedido = false;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        // Android 13+: Solo necesitamos READ_MEDIA_IMAGES
                        permisoEsencialConcedido = Boolean.TRUE.equals(result.get(Manifest.permission.READ_MEDIA_IMAGES));
                    } else {
                        // Android <13: Solo necesitamos READ_EXTERNAL_STORAGE
                        permisoEsencialConcedido = Boolean.TRUE.equals(result.get(Manifest.permission.READ_EXTERNAL_STORAGE));
                    }

                    if (permisoEsencialConcedido) {
                        abrirGaleria();
                    } else {
                        Toast.makeText(
                                getContext(),
                                "Se necesita permiso para acceder a las imágenes.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );
    }

    private void showDatePickerDialog() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Construyes el LocalDate
                    LocalDate fechaNacimiento = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay);

                    // Opcional: mostrarlo en formato "yyyy-MM-dd"
                    etFechaNacimiento.setText(fechaNacimiento.toString());

                    // Si quieres guardar el LocalDate para usarlo luego:
                    LocalDate birthDate = fechaNacimiento;
                },
                year, month, day
        );

        datePickerDialog.show();
    }

    private void solicitarPermisos() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 14+: Pedimos el permiso esencial + opcionales
            pedirPermisos.launch(new String[] {
                    Manifest.permission.READ_MEDIA_IMAGES,
                    // Los siguientes permisos son opcionales:
                    Manifest.permission.READ_MEDIA_VIDEO,
                    "android.permission.READ_MEDIA_VISUAL_USER_SELECTED"
            });
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13: Solo pedimos READ_MEDIA_IMAGES (obligatorio)
            pedirPermisos.launch(new String[] {
                    Manifest.permission.READ_MEDIA_IMAGES
            });
        } else {
            // Android <13: Pedimos READ_EXTERNAL_STORAGE
            pedirPermisos.launch(new String[] {
                    Manifest.permission.READ_EXTERNAL_STORAGE
            });
        }
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        seleccionarImagenLauncher.launch(intent);
    }

    private void subirImagenAFirebase(Uri uri, Runnable onSuccess) {
        if (uri == null) {
            Toast.makeText(getContext(), "No se ha seleccionado ninguna imagen", Toast.LENGTH_SHORT).show();
            if (onSuccess != null) onSuccess.run();
            return;
        }

        // Crear referencia única para la imagen
        String fileName = "animal_" + System.currentTimeMillis() + ".jpg";
        StorageReference imageRef = storageReference.child("animales/" + fileName);

        // Subir archivo
        imageRef.putFile(uri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Obtener la URL de descarga
                    imageRef.getDownloadUrl()
                            .addOnSuccessListener(downloadUri -> {
                                imageDownloadUrl = downloadUri.toString();
                                imagenPendienteSubir = false; // Ya no hay imagen pendiente
                                Toast.makeText(getContext(), "Imagen subida correctamente", Toast.LENGTH_SHORT).show();
                                if (onSuccess != null) onSuccess.run();
                            })
                            .addOnFailureListener(e -> {
                                btnSeleccionarImagen.setEnabled(true);
                                btnSeleccionarImagen.setText("Guardar");
                                Toast.makeText(getContext(), "Error al obtener URL de la imagen", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    btnSeleccionarImagen.setEnabled(true);
                    btnSeleccionarImagen.setText("Guardar");
                    Toast.makeText(getContext(), "Error al subir la imagen: " + e.getMessage(), Toast.LENGTH_LONG).show();
                })
                .addOnProgressListener(taskSnapshot -> {
                    // Mostrar progreso de subida
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    btnSeleccionarImagen.setText("Subiendo imagen... " + (int) progress + "%");
                });
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

    // Método para guardar el animal
    public void saveAnimal() {
        if (!validateForm()) {
            Toast.makeText(getContext(), "Por favor complete todos los campos requeridos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Mostrar mensaje de que se está guardando
        btnGuardar.setEnabled(false);
        btnGuardar.setText("Guardando...");

        // Si hay una imagen seleccionada, subirla primero
        if (imagenPendienteSubir && imagenSeleccionadaUri != null) {
            subirImagenAFirebase(imagenSeleccionadaUri, this::guardarDatosAnimal);
        } else {
            // Si no hay imagen, guardar directamente con imagen nula
            imageDownloadUrl = null; // Asegurarse que es null cuando no hay imagen
            guardarDatosAnimal();
        }
    }

    // Método separado para guardar los datos del animal
    private void guardarDatosAnimal() {
        // Obtener datos del formulario
        String name = etNombre.getText().toString().trim();
        //String speciesId = etEspecie.getText().toString().trim();
        //LocalDate birthDate = etFechaNacimiento.getText().toString().trim();
        boolean isBirthDateEstimated = cbNacimientoAproximado.isChecked();

        String sex = "";
        int selectedId = rgSexo.getCheckedRadioButtonId();
        if (selectedId == R.id.rbMacho) {
            sex = "Male";
        } else if (selectedId == R.id.rbHembra) {
            sex = "Female";
        } else if (selectedId == R.id.rbDesconocido) {
            sex = "Unknown";
        }

        String size = etTamano.getText().toString().trim();
        String animalDescription = etDescripcion.getText().toString().trim();
        boolean isNeutered = cbCastrado.isChecked();
        String microchip = etMicrochip.getText().toString().trim();
        Boolean isAdopted = false;
        int speciesId = 1;
        // Aquí puedes crear tu objeto Animal con todos los datos incluyendo imageDownloadUrl
        // Ejemplo:
        // Animal animal = new Animal(nombre, especie, fechaNacimiento, nacimientoAprox,
        //                           sexo, ciudad, tamaño, descripcion, castrado,
        //                           microchip, imageDownloadUrl);
        //
        // Luego guardar en tu base de datos (Firebase Realtime Database o Firestore)
        // databaseReference.child("animales").push().setValue(animal);

        // Restablecer el botón
        btnGuardar.setEnabled(true);
        btnGuardar.setText("Guardar");
        Animal animal = new Animal(name, speciesId, birthDate, isBirthDateEstimated, sex, size, animalDescription, isNeutered, microchip, isAdopted);
        new Thread(() -> {
            ApiRequests api = new ApiRequests();
            api.addAnimalIntoDatabase(animal);
        }).start();
        Toast.makeText(getContext(), "Animal guardado correctamente", Toast.LENGTH_SHORT).show();
        clearForm();
    }

    // Método para limpiar el formulario
    public void clearForm() {
        etNombre.setText("");
        etEspecie.setText("");
        etFechaNacimiento.setText("");
        cbNacimientoAproximado.setChecked(false);
        rgSexo.clearCheck();
        etTamano.setText("");
        etDescripcion.setText("");
        cbCastrado.setChecked(false);
        etMicrochip.setText("");
        imgAnimal.setImageResource(R.drawable.gatoinicio);
        btnSeleccionarImagen.setText("Seleccionar Imagen");
        btnSeleccionarImagen.setEnabled(true);
        btnGuardar.setText("Guardar");
        btnGuardar.setEnabled(true);

        // Limpiar variables de imagen
        imagenSeleccionadaUri = null;
        imageDownloadUrl = null;
        imagenPendienteSubir = false;
    }

}