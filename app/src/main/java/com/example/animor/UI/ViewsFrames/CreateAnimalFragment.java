package com.example.animor.UI.ViewsFrames;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.animor.App.MyApplication;
import com.example.animor.Model.dto.SpeciesDTO;
import com.example.animor.Model.dto.TagDTO;
import com.example.animor.Model.entity.Animal;
import com.example.animor.Model.entity.Sex;
import com.example.animor.Model.entity.Tag;
import com.example.animor.Model.entity.User;
import com.example.animor.Model.request.AnimalRequest;
import com.example.animor.Model.request.PhotoRequest;
import com.example.animor.Model.request.TagRequest;
import com.example.animor.R;
import com.example.animor.Utils.ApiRequests;
import com.example.animor.Utils.PreferenceUtils;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


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
    private static String imageDownloadUrl; // Para guardar la URL de descarga
    private boolean imagenPendienteSubir = false; // Flag para saber si hay imagen por subir

    // Componentes de la UI
    private EditText etNombre, etFechaNacimiento, etTamano, etDescripcion, etMicrochip;
    private CheckBox cbNacimientoAproximado, cbCastrado;
    private RadioGroup rgSexo;
    private RadioButton rbMacho, rbHembra, rbDesconocido;
    private ImageView imgAnimal;
    private Button btnSeleccionarImagen;
    private Button btnGuardar;
    private ListView listTagsView;
    static LocalDate birthDate;
    Spinner spSpecies;
    String TAG = "CreateAnimalFragment";
    static Sex sex = Sex.valueOf("Unknown");
    ApiRequests api = new ApiRequests();
    List<TagRequest> selectedTags = new ArrayList<>();
    SpeciesDTO animalSpeciesDTO = new SpeciesDTO();
    String imagePath="";
    private String speciesName = "";
    List<TagDTO> receivedTags = PreferenceUtils.getTagList();
    Animal animal;
    AnimalRequest animalRequest = new AnimalRequest();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, TAG+" iniciada");
        return inflater.inflate(R.layout.fragment_create_animal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // formateador de fecha
        calendar = Calendar.getInstance();

        // Firebase Storage
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReferenceFromUrl("gs://named-icon-460311-e4.firebasestorage.app");

        // Inicializar vistas
        initViews(view);

        // Configurar listeners
        setupListeners();
        checkIfEditingAnimal();

        // Configurar Activity Result Launchers
        setupActivityResultLaunchers();
    }

    private void checkIfEditingAnimal() {
        if (getArguments() != null) {
            animal = (Animal) getArguments().getSerializable("animal");
            if (animal != null) {
                Log.d(TAG, "Imagen del animal recibido: "+animal.getPhotoList().get(0).getPhotoUrl());
                Picasso.get()
                        .load(animal.getPhotoList().get(0).getPhotoUrl())
                        .placeholder(R.drawable.gatoinicio)
                        .error(R.drawable.gatoinicio)
                        .into(imgAnimal);
            }
            etNombre.setText(animal.getAnimalName());
            Log.d(TAG, "Animal recibido: SpeciesID: "+animal.getSpeciesId()+"id: "+animal.getAnimalId());
            List<SpeciesDTO> species = PreferenceUtils.getSpeciesList();
            for (SpeciesDTO s : species) {
                if (s.getSpeciesId() == animal.getSpeciesId()) {
                    speciesName = s.getSpeciesName();
                    break;
                }
            }
            DateTimeFormatter formatoSalida = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            String fechaFormateada = animal.getBirthDate().format(formatoSalida);
            etFechaNacimiento.setText(fechaFormateada);
            if(animal.getIsBirthDateEstimated()){
                cbNacimientoAproximado.setChecked(true);
            }
            Log.d(TAG, "Sexo: "+animal.getSex().toString());
            switch(animal.getSex()){
                case Male:
                    rbMacho.setChecked(true);
                    break;
                case Female:
                    rbHembra.setChecked(true);
                    break;
                case Unknown:
                    rbDesconocido.setChecked(true);
                    break;
            }
            etTamano.setText(animal.getSize());
            etDescripcion.setText(animal.getAnimalDescription());
            if(animal.getIsNeutered()){
                cbCastrado.setChecked(true);
            }else{
                cbCastrado.setChecked(false);
            }
            if(animal.getMicrochipNumber()!=null) {
                etMicrochip.setText(animal.getMicrochipNumber());
            }
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Log.d(TAG, "Tags: "+animal.getTagList().get(0));
                for (int i = 0; i < receivedTags.size(); i++) {
                    Tag currentTag = Tag.fromDTO(receivedTags.get(i));

                    // Verificar si el tag actual está en la lista B
                    for (Tag tagB : animal.getTagList()) {
                        if (currentTag.getTagId().equals(tagB.getTagId())) {
                            // Hacer check en la posición i
                            listTagsView.setItemChecked(i, true);

                            TagRequest tag = new TagRequest();
                            tag.setTagName(currentTag.getTagName());
                            tag.setTagId(currentTag.getTagId());
                            selectedTags.add(tag);
                            break;
                        }
                    }
                }
            }, 100); // 100ms de delay

        }
    }

    private void initViews(View view) {
        // EditTexts
        etNombre = view.findViewById(R.id.etNombre);
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
        imgAnimal = view.findViewById(R.id.imgUser);
        btnSeleccionarImagen = view.findViewById(R.id.btnSeleccionarImagen);
        btnGuardar = view.findViewById(R.id.buttonSave);
        btnGuardar.setVisibility(View.VISIBLE);
        spSpecies = view.findViewById(R.id.spinnerSpecies);
        MyApplication.executor.execute(()->{
            List<SpeciesDTO> receivedSpecies = PreferenceUtils.getSpeciesList();
            requireActivity().runOnUiThread(() -> {
                ArrayAdapter<SpeciesDTO> adapter = new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        receivedSpecies);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spSpecies.setAdapter(adapter);
                if (getArguments() != null) {
                    for (int i = 0; i < receivedSpecies.size(); i++) {
                        if (receivedSpecies.get(i).getSpeciesId() == animal.getSpeciesId()) {
                            spSpecies.setSelection(i);
                            break;
                        }
                    }
                }
            });
        });
        listTagsView = view.findViewById(R.id.listTags);
        MyApplication.executor.execute(()->{
            List<Tag> tagsInList = new ArrayList<>();
                for(TagDTO t: receivedTags){
                    Tag tg = new Tag();
                    tg.setTagId(t.getTagId());
                    tg.setTagName(t.getTagName());
                    tagsInList.add(tg);
                }
            requireActivity().runOnUiThread(() -> {
                ArrayAdapter<Tag> adapter = new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_list_item_multiple_choice,
                        tagsInList);
                listTagsView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                listTagsView.setAdapter(adapter);
            });
        });
    }

    private void setupListeners() {
        // Listener para seleccionar fecha
        etFechaNacimiento.setOnClickListener(v -> showDatePickerDialog());

        // Listener para seleccionar imagen
        btnSeleccionarImagen.setOnClickListener(v -> solicitarPermisos());

        // Listener para guardar
        btnGuardar.setOnClickListener(v -> saveAnimal());

        spSpecies.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpeciesDTO selectedSpeciesDTO = (SpeciesDTO) parent.getItemAtPosition(position);
                String speciesName = selectedSpeciesDTO.getSpeciesName();
                int speciesId = selectedSpeciesDTO.getSpeciesId();
                animalSpeciesDTO.setSpeciesId(speciesId);
                animalSpeciesDTO.setSpeciesName(speciesName);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Manejar caso cuando no hay selección
            }
        });
        listTagsView.setOnItemClickListener((adapterView, view, i, l) -> {
            Tag selectedTag = (Tag) adapterView.getItemAtPosition(i);
            TagRequest tag = new TagRequest();
            tag.setTagName(selectedTag.getTagName());
            tag.setTagId(selectedTag.getTagId());
            selectedTags.add(tag);
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
                    // Verificación del permiso esencial
                    boolean permisoEsencialConcedido = false;
                    //vamos a mantener ambos por control de versiones
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        // (Android 13+ solo necesita READ_MEDIA_IMAGES)
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
                    // LocalDate
                    LocalDate fechaNacimiento = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay);
                    // formato en España
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    etFechaNacimiento.setText(formatter.format(fechaNacimiento));
                    birthDate = fechaNacimiento;

                },
                year, month, day
        );

        datePickerDialog.show();
    }

    private void solicitarPermisos() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 14+
            pedirPermisos.launch(new String[] {
                    Manifest.permission.READ_MEDIA_IMAGES,
                    "android.permission.READ_MEDIA_VISUAL_USER_SELECTED"
            });
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13: Solo pedimos READ_MEDIA_IMAGES (obligatorio)
            pedirPermisos.launch(new String[] {
                    Manifest.permission.READ_MEDIA_IMAGES
            });
        } else {
            // Android <13: READ_EXTERNAL_STORAGE
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
        User user = PreferenceUtils.getUser();
        String fileName = "animal_" + System.currentTimeMillis() + ".jpg";
        StorageReference imageRef = storageReference.child("foto/"+user.getUserId() +"/"+fileName);
        //imagePath = storageReference+imageRef.getPath();
        imagePath = imageRef.getPath();
        Log.d("CreateAnimalFragment", "IMAGEPATH: "+imagePath);

        // Subir archivo
        imageRef.putFile(uri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Obtener la URL de descarga
                    imageRef.getDownloadUrl()
                            .addOnSuccessListener(downloadUri -> {
                                imageDownloadUrl = downloadUri.toString();
                                Log.d("URL DE LA IMAGEN", imageDownloadUrl);
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

    // Method para validar el formulario
    public boolean validateForm() {
        boolean isValid = true;

        if (etNombre.getText().toString().trim().isEmpty()) {
            etNombre.setError("Nombre requerido");
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

        // VALIDACIÓN OBLIGATORIA DE FOTO
        if (imagenSeleccionadaUri == null && (animal == null || animal.getPhotoList().get(0) == null)) {
            Toast.makeText(getContext(), "Debe seleccionar una foto para el animal", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }

    // Method para guardar el animal
    public void saveAnimal() {
        if (!validateForm()) {
            return; // El mensaje de error ya se muestra en validateForm()
        }

        // Deshabilitar botón para evitar doble envío
        btnGuardar.setEnabled(false);
        btnGuardar.setText("Guardando...");

        // FLUJO OBLIGATORIO: Primero foto, luego animal
        if (imagenSeleccionadaUri != null) {
            // Hay nueva imagen seleccionada - subirla primero
            subirImagenAFirebase(imagenSeleccionadaUri, this::guardarDatosAnimal);
        } else if (animal != null && animal.getImage() != null && !animal.getImage().isEmpty()) {
            // Estamos editando y ya hay imagen existente - usar la existente
            imageDownloadUrl = animal.getImage();
            imagePath = ""; // O mantener el path existente si lo tienes
            guardarDatosAnimal();
        } else {
            // No debería llegar aquí si validateForm() funciona correctamente
            restaurarEstadoBoton();
            Toast.makeText(getContext(), "Error: No hay imagen disponible", Toast.LENGTH_SHORT).show();
        }
    }

    // Method separado para guardar los datos del animal (VERSIÓN FINAL)
    private void guardarDatosAnimal() {
        if (imageDownloadUrl == null || imageDownloadUrl.isEmpty()) {
            restaurarEstadoBoton();
            Toast.makeText(getContext(), "Error: No se pudo obtener la URL de la imagen", Toast.LENGTH_SHORT).show();
            return;
        }

        // Preparar datos del formulario
        String name = etNombre.getText().toString().trim();
        boolean isBirthDateEstimated = cbNacimientoAproximado.isChecked();

        int selectedId = rgSexo.getCheckedRadioButtonId();
        if (selectedId == R.id.rbMacho) {
            sex = Sex.fromString("Male");
        } else if (selectedId == R.id.rbHembra) {
            sex = Sex.fromString("Female");
        } else if (selectedId == R.id.rbDesconocido) {
            sex = Sex.fromString("Unknown");
        }
        String size = etTamano.getText().toString().trim();
        String animalDescription = etDescripcion.getText().toString().trim();
        boolean isNeutered = cbCastrado.isChecked();
        String microchip = etMicrochip.getText().toString().trim();
        Boolean isAdopted = false;
        int speciesCode = animalSpeciesDTO.getSpeciesId();

        // Crear AnimalRequest
        if (getArguments() != null){
            animalRequest.setAnimalId(animal.getAnimalId());
        }
        animalRequest.setAnimalName(name);
        animalRequest.setSpeciesId(speciesCode);
        animalRequest.setBirthDate(birthDate);
        animalRequest.setBirthDateEstimated(isBirthDateEstimated);
        animalRequest.setSex(sex);
        animalRequest.setSize(size);
        animalRequest.setAnimalDescription(animalDescription);
        animalRequest.setNeutered(isNeutered);
        animalRequest.setMicrochipNumber(microchip.isEmpty() ? null : microchip);
        animalRequest.setAdopted(isAdopted);
        animalRequest.setTagList(selectedTags);

        // GUARDADO EN BACKGROUND
        MyApplication.executor.execute(() -> {
            try {
                // Preparar foto
                PhotoRequest photo = new PhotoRequest();
                photo.setPhotoUrl(imageDownloadUrl);
                photo.setCoverPhoto(true);
                photo.setDisplayOrder(0);
                photo.setFilePath(imagePath);

                List<PhotoRequest> animalPhotos = new ArrayList<>();
                animalPhotos.add(photo);
                animalRequest.setPhotoList(animalPhotos);

                // Guardar animal
                Long receivedAnimalId = api.addAnimalIntoDatabase(animalRequest);

                // Volver al hilo principal para actualizar UI
                requireActivity().runOnUiThread(() -> {
                    if (receivedAnimalId != null && receivedAnimalId > 0) {
                        // ÉXITO - guardar foto asociada en background
                        MyApplication.executor.execute(() -> {
                            api.addPhotoIntoDatabase(receivedAnimalId, photo);
                        });

                        // Actualizar UI una sola vez
                        Toast.makeText(getContext(), "Animal guardado correctamente", Toast.LENGTH_SHORT).show();
                        clearForm();
                        restaurarEstadoBoton();
                    } else {
                        // Error al guardar
                        restaurarEstadoBoton();
                        Toast.makeText(getContext(), "Error al guardar el animal", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                // Error inesperado
                Log.e("CreateAnimalFragment", "Error al guardar animal", e);
                requireActivity().runOnUiThread(() -> {
                    restaurarEstadoBoton();
                    Toast.makeText(getContext(), "Error inesperado: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    // Method para limpiar el formulario
    // limpiar el formulario, aunque no se usa
    public void clearForm() {
        etNombre.setText("");
       // etEspecie.setText("");
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
        imgAnimal.setImageResource(R.drawable.gatoinicio);
        btnSeleccionarImagen.setText("Seleccionar Imagen");
        btnSeleccionarImagen.setEnabled(true);

        // Limpiar variables de imagen
        imagenSeleccionadaUri = null;
        imageDownloadUrl = null;
        imagenPendienteSubir = false;
        selectedTags.clear();
        listTagsView.clearChoices();
    }
    private void restaurarEstadoBoton() {
        btnGuardar.setEnabled(true);
        btnGuardar.setText("Guardar");
    }

}