package com.example.animor.UI.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.animor.R;

public class CreateListingTwoFragment extends Fragment {
    private EditText etCiudad;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_animal, container, false);
    }
    private void initViews(View view) {

        etCiudad = view.findViewById(R.id.etCiudad);
    }

    private void setupListeners() {

        etCiudad.setOnClickListener(v -> selectCity());
    }

    private void selectCity() {
        // Implementar lógica para seleccionar ciudad
        Toast.makeText(getContext(), "Seleccionar ciudad", Toast.LENGTH_SHORT).show();
        // Podrías usar un diálogo con lista de ciudades o una API
    }


    // Método para validar el formulario
        public boolean validateForm() {
            boolean isValid = true;
            if (etCiudad.getText().toString().trim().isEmpty()) {
                etCiudad.setError("Ciudad requerida");
                isValid = false;
            }
            return isValid;

        }    // Método para guardar el listing (llamado desde la Activity)
    public void saveListing() {
        if (!validateForm()) {
            Toast.makeText(getContext(), "Por favor complete todos los campos requeridos", Toast.LENGTH_SHORT).show();
            return;
        }
        String ciudad = etCiudad.getText().toString().trim();
        Toast.makeText(getContext(), "Registro guardado correctamente", Toast.LENGTH_SHORT).show();

    }
    // Método para limpiar el formulario
    public void clearForm() {

        etCiudad.setText("");

    }
}
